import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.Messages;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import VCS.Objects.GitObjects.Blob;
import VCS.RepoImpl;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    private final String[] INIT_ARGS = {"init"};
    private final String[] BRANCH_ARGS = {"branch"};
    private Path globalRoot;
    private FileSystem fileSystem;
    private List<String> hashes;

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

}