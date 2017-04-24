import VCS.Commands.BranchCommands.BranchDeleteCommand;
import VCS.Commands.BranchCommands.BranchListCommand;
import VCS.Commands.CheckoutCommands.CheckoutByBranchCommand;
import VCS.Commands.LogCommand;
import VCS.Data.FileSystem;
import VCS.Data.FileSystemImpl;
import VCS.Data.LoggerBuilder;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.Messages;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.Branch;
import VCS.Objects.GitObjects.Blob;
import VCS.Objects.Head;
import VCS.Objects.Log;
import VCS.RepoImpl;
import javafx.util.Pair;
import org.apache.logging.log4j.core.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BranchListCommand.class, LogCommand.class, CheckoutByBranchCommand.class,
        BranchDeleteCommand.class, LoggerBuilder.class, RepoImpl.class})
public class VCSTests {
    private static final List<String> FILE_CONTENTS = new ArrayList<>(Arrays.asList(
            "this\nis\na\ncontent\nof\nfile",
            "and how about this content",
            "hello hello good bye hello",
            "another test content",
            "lalalallalalalalall\nlalalalal\nlalalalalal"));
    private static final List<String> NEW_FILE_CONTENTS = new ArrayList<>(Arrays.asList(
            "this\nis\nthe\ncontent\nof\nfile\n(MODIFIED)",
            "and how about this content. jack. captain jack.",
            "hello hello good hello. still here?????????????\n\n\n\n\n\nahahahahah",
            "HOT FIRE",
            "SUPA HOT FIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIRE"));
    private List<Path> paths;
    private static final String[] INIT_ARGS = {"init"};
    private static final String[] BRANCH_ARGS = {"branch"};
    private static final String[] STATUS_ARGS = {"status"};
    private static final String[] CLEAN_ARGS = {"clean"};
    private Path globalRoot;
    private FileSystem fileSystem;
    private List<String> hashes;
    private Logger logger;

    @Before()
    public void before() throws IOException {
        globalRoot = Files.createTempDirectory("globalRoot");
        paths = new ArrayList<>(Arrays.asList(
                Paths.get(globalRoot + File.separator + "file0"),
                Paths.get(globalRoot + File.separator + "dir" + File.separator + "dir"
                        + File.separator + "file1"),
                Paths.get(globalRoot + File.separator + "file2"),
                Paths.get(globalRoot + File.separator + "dir" + File.separator + "file3")));
    }

    @Test
    public void initTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
    }

    @Test
    public void doubleInitTest() throws UncommittedChangesException, UnstagedChangesException,
            IOException {
        try {
            init(globalRoot);
            init(globalRoot);
        } catch (IncorrectArgsException e) {
            assertEquals(e.getMessage(), Messages.GIT_ALREADY_EXISTS);
        }
    }

    @Test
    public void nonExistingRepoTest() throws UncommittedChangesException, UnstagedChangesException,
            IOException {
        try{
            new RepoImpl(new String[]{"commit", "-m", "?"}, globalRoot).execute();
        } catch (IncorrectArgsException e) {
            assertEquals(e.getMessage(), Messages.GIT_DOESN_T_EXIST);
        }
    }

    /**
     * file0 <- content0, file1 <- content1;
     * git add;
     * file1 <- newContent1, file2 <- newContent2;
     * git add;
     */
    @Test
    public void addTest() throws IOException, UnstagedChangesException, UncommittedChangesException,
            IncorrectArgsException {
        init(globalRoot);
        fileSystem.writeToFile(paths.get(0), FILE_CONTENTS.get(0));
        fileSystem.writeToFile(paths.get(1), FILE_CONTENTS.get(1));
        hashes = new ArrayList<>(Arrays.asList(hashFile(paths.get(0)), hashFile
                (paths.get(1))));
        new RepoImpl(new String[]{"add", paths.get(0).toString(), paths.get(1).toString()},
                globalRoot).execute();
        checkIndex(paths.subList(0, 2), hashes.subList(0, 2));
        fileSystem.writeToFile(paths.get(1), NEW_FILE_CONTENTS.get(1));
        fileSystem.writeToFile(paths.get(2), NEW_FILE_CONTENTS.get(2));
        hashes.set(1, hashFile(paths.get(1)));
        hashes.add(hashFile(paths.get(2)));
        new RepoImpl(
                new String[]{"add", paths.get(0).toString(), paths.get(1).toString(),
                        paths.get(2).toString()},
                globalRoot).execute();
        checkIndex(paths.subList(0, 3), hashes.subList(0, 3));
    }

    /**
     * commit;
     * get content of index
     * get content of tree associated with commit in two ways:
     * 1. head -> branch ref -> commit -> tree
     * 2. commit -> tree
     * compare three arrays representing tree content
     */
    @Test
    public void commitTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        addTest();
        String[] args = {"commit", "-m", "test"};
        String commitHash = new RepoImpl(args, globalRoot).execute();
        byte[] indexContent = fileSystem.getFileContentAsByteArray(fileSystem.getIndexLocation());
        byte[] headCommitContent = fileSystem.getFileContentAsByteArray(
                fileSystem.buildTreeLocation("master"));
        byte[] commitContent = fileSystem.getFileContentAsByteArray(
                fileSystem.buildObjectLocation(fileSystem.getFileContentAsString(
                                fileSystem.buildObjectLocation(commitHash))));
        assertTrue(Arrays.equals(indexContent, headCommitContent));
        assertTrue(Arrays.equals(indexContent, commitContent));
        assertTrue(Arrays.equals(commitContent, headCommitContent));
    }

    /**
     * init repo if it's not inited yet
     * create 'a', 'b', 'c', 'd' branches
     * check branch list
     * checkout b
     * check branch list
     * delete c
     * check branch list
     */
    @Test
    public void branchWithoutChangesTest() throws UncommittedChangesException,
            IncorrectArgsException, UnstagedChangesException, IOException {
        try {
            init(globalRoot);
        } catch (IncorrectArgsException e) {
            //ignore
        }
        List<String> branches = Arrays.asList("a", "b", "c", "d");
        for (String branchName: branches) {
            String[] args = new String[]{"branch", branchName};
            new RepoImpl(args, globalRoot).execute();
        }
        String atMaster = "  a\n  b\n  c\n  d\n *master\n";
        assertEquals(atMaster, new RepoImpl(BRANCH_ARGS, globalRoot).execute());
        new RepoImpl(new String[]{"checkout", "b"}, globalRoot).execute();
        String atB = "  a\n *b\n  c\n  d\n  master\n";
        assertEquals(atB, new RepoImpl(BRANCH_ARGS, globalRoot).execute());
        new RepoImpl(new String[]{"branch", "-d", "c"}, globalRoot).execute();
        String atBNoC = "  a\n *b\n  d\n  master\n";
        assertEquals(atBNoC, new RepoImpl(BRANCH_ARGS, globalRoot).execute());
        try {
            new RepoImpl(new String[]{"checkout", "b"}, globalRoot).execute();
        } catch (IncorrectArgsException e) {
            assertEquals(e.getMessage(), Messages.THIS_IS_THE_CURRENT_BRANCH);
        }
    }

    @Test (expected = UnstagedChangesException.class)
    public void checkoutWithUnstagedChanges() throws UncommittedChangesException,
            IncorrectArgsException, UnstagedChangesException, IOException {
        addTest();
        fileSystem.writeToFile(paths.get(1), "?");
        branchWithoutChangesTest();
    }

    @Test (expected = UncommittedChangesException.class)
    public void checkoutWithUncommittedChanges() throws UncommittedChangesException,
            IncorrectArgsException, UnstagedChangesException, IOException {
        addTest();
        branchWithoutChangesTest();
    }

    @Test
    public void fileStateAtBranchesTest() throws UncommittedChangesException,
            IncorrectArgsException, UnstagedChangesException, IOException {
        commitTest();
        new RepoImpl(new String[]{"checkout", "-b", "b"}, globalRoot).execute();
        fileSystem.writeToFile(paths.get(3), FILE_CONTENTS.get(3));
        hashes.add(Blob.buildBlob(fileSystem, paths.get(3)).getHash());
        fileSystem.writeToFile(paths.get(0), NEW_FILE_CONTENTS.get(0));
        new RepoImpl(new String[]{"add", paths.get(0).toString(), paths.get(3).toString()},
                globalRoot).execute();
        new RepoImpl(new String[]{"commit", "-m", "?"}, globalRoot).execute();
        new RepoImpl(new String[]{"checkout", "master"}, globalRoot).execute();
        assertEquals(FILE_CONTENTS.get(0), fileSystem.getFileContentAsString(paths.get(0)));
        new RepoImpl(new String[]{"branch", "-d", "b"}, globalRoot).execute();
    }

    @Test
    public void checkoutByCommitTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
        List<String> hashes = new ArrayList<>();
        String[] addArgs = new String[]{"add", paths.get(0).toString()};
        for (int i = 0; i < FILE_CONTENTS.size(); i++) {
            fileSystem.writeToFile(paths.get(0), FILE_CONTENTS.get(i));
            new RepoImpl(addArgs, globalRoot).execute();
            hashes.add(new RepoImpl(new String[]{"commit", "-m", "commit#" + String.valueOf(i)},
                    globalRoot).execute());
        }
        for (int i = 0; i < hashes.size(); i++) {
            new RepoImpl(new String[]{"checkout", hashes.get(i)}, globalRoot).execute();
            assertEquals(FILE_CONTENTS.get(i), fileSystem.getFileContentAsString(paths.get(0)));
            assertEquals(hashes.get(i), new Head(fileSystem).getHeadCommitHash());
        }
    }

    @Test
    public void checkoutByBranchTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
        String[] addArgs = new String[]{"add", paths.get(0).toString(), paths.get(1).toString()};
        String[] commitArgs = new String[]{"commit", "-m", "commit message"};
        List<String> branches = Arrays.asList("master", "a", "b", "c", "d");
        for (int i = 0; i < branches.size(); i++) {
            try {
                String[] args = new String[]{"checkout", "-b", branches.get(i)};
                new RepoImpl(args, globalRoot).execute();
            } catch (IncorrectArgsException e) {
                assertEquals("master", branches.get(i));
                assertEquals(Messages.BRANCH_ALREADY_EXISTS, e.getMessage());
            }
            fileSystem.writeToFile(paths.get(0), FILE_CONTENTS.get(i));
            fileSystem.writeToFile(paths.get(1), NEW_FILE_CONTENTS.get(i));
            new RepoImpl(addArgs, globalRoot).execute();
            new RepoImpl(commitArgs, globalRoot).execute();
        }
        for (int i = 0; i < branches.size(); i++) {
            String[] args = new String[]{"checkout", branches.get(i)};
            new RepoImpl(args, globalRoot).execute();
            assertEquals(FILE_CONTENTS.get(i), fileSystem.getFileContentAsString(paths.get(0)));
            assertEquals(NEW_FILE_CONTENTS.get(i), fileSystem.getFileContentAsString(paths.get(1)));
        }
    }

    @Test
    public void mergeTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
        fileSystem.writeToFile(paths.get(0), FILE_CONTENTS.get(0));
        fileSystem.writeToFile(paths.get(1), FILE_CONTENTS.get(1));
        new RepoImpl(new String[]{"add", paths.get(0).toString(), paths.get(1).toString()},
                globalRoot).execute();
        String commitHash = new RepoImpl(new String[]{"commit", "-m", "commit at master"},
                globalRoot).execute();
        fileSystem.writeToFile(paths.get(2), FILE_CONTENTS.get(2));
        new RepoImpl(new String[]{"add", paths.get(2).toString()}, globalRoot).execute();
        new RepoImpl(new String[]{"commit", "-m", "another commit at master"}, globalRoot)
                .execute();
        new RepoImpl(new String[]{"checkout", commitHash}, globalRoot).execute();
        new RepoImpl(new String[]{"checkout", "-b", "b"}, globalRoot).execute();
        fileSystem.writeToFile(paths.get(3), NEW_FILE_CONTENTS.get(3));
        new RepoImpl(new String[]{"add", paths.get(3).toString()}, globalRoot).execute();
        new RepoImpl(new String[]{"commit", "-m", "commit at b"}, globalRoot).execute();
        new RepoImpl(new String[]{"merge", "master"}, globalRoot).execute();
        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        List<Boolean> found = new ArrayList<>(Collections.nCopies(4, Boolean.FALSE));
        for (int i = 0; i < indexedFiles.size(); i++) {
            int j = paths.indexOf(Paths.get(indexedFiles.get(i)));
            assertNotEquals(-1, j);
            found.set(j, Boolean.TRUE);
            assertEquals(hashFile(paths.get(j)), indexedHashes.get(i));
        }
        assertTrue(found.get(0) == Boolean.TRUE && found.get(1) == Boolean.TRUE &&
                found.get(2) == Boolean.FALSE && found.get(3) == Boolean.TRUE);

    }

    @Test
    public void statusTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
        for (int i = 0; i < paths.size(); i++) {
            fileSystem.writeToFile(paths.get(i), FILE_CONTENTS.get(i));
        }
        String status = new RepoImpl(STATUS_ARGS, globalRoot).execute();
        for (Path path : paths) {
            assertTrue(status.contains("untracked: " + path.toString() + "\n"));
        }
        new RepoImpl(new String[]{"add", paths.get(0).toString(), paths.get(1).toString()},
                globalRoot).execute();
        status = new RepoImpl(STATUS_ARGS, globalRoot).execute();
        assertTrue(status.contains("staged: " + paths.get(0).toString() + "\n"));
        assertTrue(status.contains("staged: " + paths.get(1).toString() + "\n"));
        assertTrue(status.contains("untracked: " + paths.get(2).toString() + "\n"));
        assertTrue(status.contains("untracked: " + paths.get(3).toString() + "\n"));
        new RepoImpl(new String[]{"commit", "-m", "commit at master"}, globalRoot).execute();
        fileSystem.deleteFile(paths.get(1));
        status = new RepoImpl(STATUS_ARGS, globalRoot).execute();
        assertTrue(status.contains("staged: " + paths.get(0).toString() + "\n"));
        assertTrue(status.contains("deleted: " + paths.get(1).toString() + "\n"));
        assertTrue(status.contains("untracked: " + paths.get(2).toString() + "\n"));
        assertTrue(status.contains("untracked: " + paths.get(3).toString() + "\n"));
        fileSystem.writeToFile(paths.get(0), NEW_FILE_CONTENTS.get(0));
        status = new RepoImpl(STATUS_ARGS, globalRoot).execute();
        assertTrue(status.contains("modified: " + paths.get(0).toString() + "\n"));
        assertTrue(status.contains("deleted: " + paths.get(1).toString() + "\n"));
        assertTrue(status.contains("untracked: " + paths.get(2).toString() + "\n"));
        assertTrue(status.contains("untracked: " + paths.get(3).toString() + "\n"));
        new RepoImpl(new String[]{"add", paths.get(0).toString(), paths.get(3).toString()},
                globalRoot).execute();
        new RepoImpl(new String[]{"reset", paths.get(1).toString()}, globalRoot).execute();
        new RepoImpl(new String[]{"commit", "-m", "another one"}, globalRoot).execute();
        status = new RepoImpl(STATUS_ARGS, globalRoot).execute();
        assertTrue(status.contains("staged: " + paths.get(0).toString() + "\n"));
        assertFalse(status.contains(paths.get(1).toString()));
        assertTrue(status.contains("untracked: " + paths.get(2).toString() + "\n"));
        assertTrue(status.contains("staged: " + paths.get(3).toString() + "\n"));
    }

    @Test
    public void resetTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
        String args[] = new String[paths.size() + 1];
        args[0] = "add";
        for (int i = 0; i < paths.size(); i++) {
            fileSystem.writeToFile(paths.get(i), FILE_CONTENTS.get(i));
            args[i + 1] = paths.get(i).toString();
        }
        new RepoImpl(args, globalRoot).execute();
        String status = new RepoImpl(STATUS_ARGS, globalRoot).execute();
        for (Path path : paths) {
            assertTrue(status.contains("staged: " + path.toString() + "\n"));
        }
        for (int i = 0; i < paths.size(); i++) {
            new RepoImpl(new String[]{"reset", paths.get(i).toString()}, globalRoot).execute();
            status = new RepoImpl(STATUS_ARGS, globalRoot).execute();
            for (int j = 0; j <= i; j++) {
                assertTrue(status.contains("untracked: " + paths.get(j).toString() + "\n"));
            }
            for (int j = i + 1; j < paths.size(); j++) {
                assertTrue(status.contains("staged: " + paths.get(j).toString() + "\n"));
            }
        }
    }

    @Test
    public void rmTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
        fileSystem.writeToFile(paths.get(0), FILE_CONTENTS.get(0));
        new RepoImpl(new String[]{"add", paths.get(0).toString()}, globalRoot).execute();
        new RepoImpl(new String[]{"rm", paths.get(0).toString()}, globalRoot).execute();
        String status = new RepoImpl(STATUS_ARGS, globalRoot).execute();
        assertFalse(status.contains(paths.get(0).toString()));
        assertTrue(fileSystem.notExists(paths.get(0)));
    }

    @Test
    public void cleanTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
        for (int i = 0; i < paths.size(); i++) {
            fileSystem.writeToFile(paths.get(i), FILE_CONTENTS.get(i));
        }
        new RepoImpl(new String[]{"add", paths.get(0).toString()}, globalRoot).execute();
        new RepoImpl(CLEAN_ARGS, globalRoot).execute();
        assertTrue(fileSystem.exists(paths.get(0)));
        for (Path path : paths.subList(1, paths.size())) {
            assertTrue(fileSystem.notExists(path));
        }
    }

    private void init(Path path) throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        RepoImpl repo = new RepoImpl(INIT_ARGS, path);
        repo.execute();
        fileSystem = repo.getFileSystem();
    }

    private void checkIndex(List<Path> paths, List<String> hashes) throws IOException {
        Pair<List<String>, List<String>> indexContent = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = indexContent.getKey();
        List<String> indexedHashes = indexContent.getValue();
        for (int i = 0; i < paths.size(); i++) {
            assertEquals(paths.get(i).toString(), indexedFiles.get(i));
            assertEquals(hashes.get(i), indexedHashes.get(i));
        }
    }

    private String hashFile(Path path) throws IOException {
        return Blob.buildBlob(fileSystem, path).getHash();
    }


    private void initForMockTests() throws Exception {
        fileSystem = spy(new FileSystemImpl(globalRoot));
        logger = mock(Logger.class);
        doNothing().when(logger).trace(anyString());
        when(fileSystem.getFolderContentAsListOfString(fileSystem.getRefsLocation()))
                .thenReturn(new ArrayList<>(Arrays.asList("master", "a", "b")));
        Head head = mock(Head.class);
        when(head.getCurrentBranchName()).thenReturn("a");
        whenNew(Head.class).withArguments(any()).thenReturn(head);
    }

    @Test
    public void branchListMockTest() throws Exception {
        initForMockTests();
        BranchListCommand branchListCommand = new BranchListCommand(fileSystem, logger);
        branchListCommand.run();
        assertEquals(" *a\n  b\n  master\n", branchListCommand.getBranchList());
    }

    @Test
    public void checkoutByCurrentBranchMockTest() throws Exception {
        initForMockTests();
        CheckoutByBranchCommand checkoutByBranchCommand
                = new CheckoutByBranchCommand(fileSystem, logger, "a");
        try {
            checkoutByBranchCommand.run();
        } catch (IncorrectArgsException e) {
            assertEquals(Messages.THIS_IS_THE_CURRENT_BRANCH, e.getMessage());
        }
    }

    @Test
    public void checkoutByNonExistingBranchMockTest() throws Exception {
        initForMockTests();
        CheckoutByBranchCommand checkoutByBranchCommand
                = new CheckoutByBranchCommand(fileSystem, logger, "abracadabra");
        try {
            checkoutByBranchCommand.run();
        } catch (IncorrectArgsException e) {
            assertEquals(Messages.BRANCH_DOESN_T_EXIST, e.getMessage());
        }
    }

    @Test
    public void deleteCurrentBranchMockTest() throws Exception {
        initForMockTests();
        Branch branch = mock(Branch.class);
        when(branch.notExists()).thenReturn(false);
        when(branch.getBranchName()).thenReturn("a");
        whenNew(Branch.class).withArguments(fileSystem, "a").thenReturn(branch);
        Log log = mock(Log.class);
        whenNew(Log.class).withArguments(fileSystem, "a").thenReturn(log);
        doNothing().when(log).delete();
        BranchDeleteCommand branchDeleteCommand = new BranchDeleteCommand(fileSystem, logger, "a");
        try {
            branchDeleteCommand.run();
        } catch (IncorrectArgsException e) {
            assertEquals(Messages.THIS_IS_THE_CURRENT_BRANCH, e.getMessage());
        }
    }

}