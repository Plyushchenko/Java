import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vcs.VCS;
import vcs.vcsexceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VCSTests {

    private static File GIT_FOLDER_AS_FILE = new File(".mygit");
    private static Path GIT_FOLDER_AS_PATH = Paths.get(".mygit");
    private static final Path INDEX_LOCATION = Paths.get(GIT_FOLDER_AS_PATH + File.separator + "index");
    private static final Path REFS_LOCATION = Paths.get(GIT_FOLDER_AS_PATH + File.separator + "refs");
    private static final Path HEAD_LOCATION = Paths.get(GIT_FOLDER_AS_PATH + File.separator + "HEAD");
    private static final Path OBJECTS_LOCATION = Paths.get(GIT_FOLDER_AS_PATH + File.separator + "objects");
    private static final Path LOGS_LOCATION = Paths.get(GIT_FOLDER_AS_PATH + File.separator + "logs");
    private static final List<Path> GIT_FOLDER_AND_ITS_CONTENT = new ArrayList<>(Arrays.asList(GIT_FOLDER_AS_PATH,
            INDEX_LOCATION, REFS_LOCATION, HEAD_LOCATION, OBJECTS_LOCATION, LOGS_LOCATION));
    private static File TESTDIR_AS_FILE = new File("testdir");
    private static final List<Path> FILE_PATHS = new ArrayList<>(Arrays.asList(
            Paths.get("testdir/file1.txt"),
            Paths.get("testdir/dir/file2.txt"),
            Paths.get("testdir/dir/subdir/subsubdir/file3.txt")));
    private static final List<byte[]> FILE_CONTENTS = new ArrayList<>(Arrays.asList(
            "this\nis\na\ncontent\nof\nfile".getBytes(),
            "and how about this content".getBytes(),
            "hello hello good bye hello".getBytes()));
    private static final List<byte[]> NEW_FILE_CONTENTS = new ArrayList<>(Arrays.asList(
            "this\nis\nthe\ncontent\nof\nfile\n(MODIFIED)".getBytes(),
            "and how about this content. jack. captain jack.".getBytes(),
            "hello hello good bye hello. still here????????????????????\n\n\n\n\n\n\nahahahahah".getBytes()));

    private void removeFolderAndItsContent(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        removeFolderAndItsContent(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        directory.delete();
    }

    @Test
    public void initTest() throws GitAlreadyInitedException, GitInitException {
        try {
            VCS.init();
            assert(Files.exists(GIT_FOLDER_AS_PATH));
            GIT_FOLDER_AND_ITS_CONTENT.forEach(path -> {
                assert(Files.exists(path));
            });
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
        }
    }

    @Test (expected = GitAlreadyInitedException.class)
    public void doubleGitInitTest() throws GitAlreadyInitedException, GitInitException {
        try {
            VCS.init();
            VCS.init();
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
        }
    }

    @Test (expected = NoGitFoundException.class)
    public void commitToNonExisitingRepositoryTest() throws ContentWriteException, BranchReadException,
            NothingChangedSinceLastCommitException, BranchWriteException, HeadReadException, IndexReadException,
            LogWriteException, NoGitFoundException, ContentReadException {
        VCS.commit("test");
    }

    @Test (expected = NoBranchFoundException.class)
    public void checkoutToNonExistingBranchTest() throws GitAlreadyInitedException, GitInitException, BranchAlreadyCreatedException,
            HeadReadException, BranchReadException, LogWriteException, NoGitFoundException, BranchWriteException, NoBranchFoundException,
            TreeReadException, ContentReadException, HeadWriteException, ContentWriteException, DirectioryCreateException {
        try {
            VCS.init();
            for (int i = 0; i < 10; i++) {
                VCS.createBranch("branch №" + i);
            }
            VCS.checkout("BAD_BRANCH");
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
        }
    }

    @Test (expected =  BranchAlreadyCreatedException.class)
    public void createAlredyExistingBranchTest() throws GitAlreadyInitedException, GitInitException, BranchAlreadyCreatedException,
            HeadReadException, BranchReadException, LogWriteException, NoGitFoundException, BranchWriteException {
        try {
            VCS.init();
            VCS.createBranch("master");
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
        }
    }

    @Test
    public void addFilesTest() throws GitAlreadyInitedException, GitInitException, IOException, NothingChangedSinceLastAddException,
            NoIndexFoundException, IndexReadException, AddException, FileToAddNotExistsException, NoGitFoundException {
        try {
            VCS.init();
            List<Path> filesToAdd = FILE_PATHS.subList(0, 2);
            for (int i = 0; i < 2; i++) {
                Files.createDirectories(FILE_PATHS.get(i).getParent());
                Files.createFile(FILE_PATHS.get(i));
                Files.write(FILE_PATHS.get(i), FILE_CONTENTS.get(i));
            }
            VCS.add(filesToAdd);
            for (int i = 0; i < 2; i++) {
                assertTrue(indexContains(FILE_PATHS.get(i).toAbsolutePath()));
            }
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
            removeFolderAndItsContent(TESTDIR_AS_FILE);
        }
    }

    @Test
    public void createAndCheckoutBranchTest() throws GitAlreadyInitedException, GitInitException, IOException,
            NothingChangedSinceLastAddException, NoIndexFoundException, IndexReadException, AddException,
            FileToAddNotExistsException, NoGitFoundException, BranchReadException, LogWriteException, BranchWriteException,
            BranchAlreadyCreatedException, HeadReadException, NoBranchFoundException, TreeReadException, ContentReadException,
            HeadWriteException, ContentWriteException, DirectioryCreateException {
        try {
            VCS.init();
            List<String> branchNamesList = VCS.buildBranchNamesList();
            assertEquals(1, branchNamesList.size());
            assertTrue(branchNamesList.contains("* master"));

            String branchName = "newSupaBranch";
            VCS.createBranch(branchName);
            VCS.checkout(branchName);
            branchNamesList = VCS.buildBranchNamesList();
            assertEquals(2, branchNamesList.size());
            assertTrue(branchNamesList.contains("* " + branchName));
            assertTrue(branchNamesList.contains(" master"));
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
            removeFolderAndItsContent(TESTDIR_AS_FILE);
        }
    }

    @Test
    public void switchToBranchTest() throws IOException, GitAlreadyInitedException, GitInitException, NothingChangedSinceLastAddException,
            NoIndexFoundException, IndexReadException, AddException, FileToAddNotExistsException, NoGitFoundException,
            BranchReadException, HeadReadException, ContentReadException, NothingChangedSinceLastCommitException,
            ContentWriteException, LogWriteException, BranchWriteException, BranchAlreadyCreatedException, NoBranchFoundException,
            DirectioryCreateException, TreeReadException, HeadWriteException {
        try {
            VCS.init();
            Files.createDirectories(FILE_PATHS.get(0).getParent());
            Files.createFile(FILE_PATHS.get(0));
            Files.write(FILE_PATHS.get(0), FILE_CONTENTS.get(0));
            VCS.add(Collections.singletonList(FILE_PATHS.get(0)));
            VCS.commit("commit");
            VCS.createBranch("another");
            VCS.checkout("another");
            Files.write(FILE_PATHS.get(0), NEW_FILE_CONTENTS.get(0));
            VCS.add(Collections.singletonList(FILE_PATHS.get(0)));
            VCS.commit("another commit");
            VCS.checkout("master");
            assertTrue(Files.exists(FILE_PATHS.get(0)));
            assertTrue(Arrays.equals(Files.readAllBytes(FILE_PATHS.get(0)), FILE_CONTENTS.get(0)));
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
            removeFolderAndItsContent(TESTDIR_AS_FILE);
        }
    }

    @Test
    public void switchToCommitTest() throws GitAlreadyInitedException, GitInitException, IOException, NothingChangedSinceLastAddException,
            NoIndexFoundException, IndexReadException, AddException, FileToAddNotExistsException, NoGitFoundException,
            BranchReadException, HeadReadException, ContentReadException, NothingChangedSinceLastCommitException,
            ContentWriteException, LogWriteException, BranchWriteException, BranchAlreadyCreatedException, NoBranchFoundException,
            DirectioryCreateException, TreeReadException, HeadWriteException {
        try {
            VCS.init();
            Files.createDirectories(FILE_PATHS.get(2).getParent());
            Files.createFile(FILE_PATHS.get(2));
            Files.write(FILE_PATHS.get(2), FILE_CONTENTS.get(2));
            VCS.add(Collections.singletonList(FILE_PATHS.get(2)));
            String commitHash = VCS.commit("commit");
            Files.write(FILE_PATHS.get(2), NEW_FILE_CONTENTS.get(2));
            VCS.add(Collections.singletonList(FILE_PATHS.get(2)));
            VCS.commit("another commit");
            VCS.checkout(commitHash);
            assertTrue(Files.exists(FILE_PATHS.get(2)));
            assertTrue(Arrays.equals(Files.readAllBytes(FILE_PATHS.get(2)), FILE_CONTENTS.get(2)));
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
            removeFolderAndItsContent(TESTDIR_AS_FILE);
        }

    }
    @Test
    public void mergeTest() throws GitAlreadyInitedException, GitInitException, IOException, NothingChangedSinceLastAddException,
            NoIndexFoundException, IndexReadException, AddException, FileToAddNotExistsException, NoGitFoundException,
            BranchReadException, HeadReadException, ContentReadException, NothingChangedSinceLastCommitException,
            ContentWriteException, LogWriteException, BranchWriteException, BranchAlreadyCreatedException, NoBranchFoundException,
            DirectioryCreateException, TreeReadException, HeadWriteException {
        try {
            VCS.init();
            for (int i = 0; i < 3; i++) {
                Files.createDirectories(FILE_PATHS.get(i).getParent());
                Files.createFile(FILE_PATHS.get(i));
                Files.write(FILE_PATHS.get(i), FILE_CONTENTS.get(i));
            }
            VCS.add(FILE_PATHS);
            VCS.commit("commit 3 files");
            VCS.createBranch("another");
            VCS.checkout("another");
            Files.write(FILE_PATHS.get(2), NEW_FILE_CONTENTS.get(2));
            VCS.add(Collections.singletonList(FILE_PATHS.get(2)));
            VCS.commit("3rd file changed");
            VCS.checkout("master");
            VCS.merge("another");
            assertTrue(Arrays.equals(Files.readAllBytes(FILE_PATHS.get(0)), FILE_CONTENTS.get(0)));
            assertTrue(Arrays.equals(Files.readAllBytes(FILE_PATHS.get(1)), FILE_CONTENTS.get(1)));
            assertTrue(Arrays.equals(Files.readAllBytes(FILE_PATHS.get(2)), NEW_FILE_CONTENTS.get(2)));
        } finally {
            removeFolderAndItsContent(GIT_FOLDER_AS_FILE);
            removeFolderAndItsContent(TESTDIR_AS_FILE);
        }
    }

    private boolean indexContains(Path path) throws IOException {
        List<String> indexContent = Files.readAllLines(INDEX_LOCATION);
        for (String s : indexContent){
            if (s.equals(path.toString())){
                return true;
            }
        }
        return false;
    }

}
