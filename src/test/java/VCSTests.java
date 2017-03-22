import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vcs.VCS;
import vcs.vcsexceptions.GitAlreadyInitedException;
import vcs.vcsexceptions.GitInitException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    //private static final List<Path> FILE_PATHS = new ArrayList<>(Arrays.asList());
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

    public void addFiles() throws GitAlreadyInitedException, GitInitException {
        VCS.init();

    }

}
