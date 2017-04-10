import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
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
    private final String[] INIT_ARGS = {"init"};
    private Path globalRoot;
    private FileSystem fileSystem;

    @Before()
    public void before() throws IOException {
        globalRoot = Files.createTempDirectory("globalRoot");
    }

    @Test
    public void initTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
    }

    @Test (expected = IncorrectArgsException.class)
    public void doubleInitTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        init(globalRoot);
        init(globalRoot);
    }

    @Test(expected = IncorrectArgsException.class)
    public void NonExistingRepoTest() throws UncommittedChangesException, IncorrectArgsException,
            UnstagedChangesException, IOException {
        new RepoImpl(new String[]{"commit", "-m", "?"}, globalRoot).execute();
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
        initTest();
        List<Path> paths = new ArrayList<>(Arrays.asList(
                Paths.get(globalRoot + File.separator + "file0"),
                Paths.get(globalRoot + File.separator + "dir" + File.separator + "dir"
                        + File.separator + "file1")));
        fileSystem.writeToFile(paths.get(0), FILE_CONTENTS.get(0));
        fileSystem.writeToFile(paths.get(1), FILE_CONTENTS.get(1));
        List<String> hashes = new ArrayList<>(
                Arrays.asList(hashFile(paths.get(0)), hashFile(paths.get(1))));
        String[] args = new String[]{"add", paths.get(0).toString(), paths.get(1).toString()};
        new RepoImpl(args, globalRoot).execute();
        checkIndex(paths, hashes);
        paths.add(Paths.get(globalRoot + File.separator + "file2"));
        fileSystem.writeToFile(paths.get(1), NEW_FILE_CONTENTS.get(1));
        fileSystem.writeToFile(paths.get(2), NEW_FILE_CONTENTS.get(2));
        hashes.set(1, hashFile(paths.get(1)));
        hashes.add(hashFile(paths.get(2)));
        args = new String[]{"add", paths.get(0).toString(), paths.get(1).toString(),
                paths.get(2).toString()};
        new RepoImpl(args, globalRoot).execute();
        checkIndex(paths, hashes);
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