package VCS.Data;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** File utils for git*/
public abstract class FileSystem {

    public static final Path DEFAULT_WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"));

    private boolean gitExists = true;
    @NotNull final Path workingDirectory;
    @NotNull private final Path gitLocation;
    @NotNull private final Path indexLocation;
    @NotNull final Path refsLocation;
    @NotNull private final Path headLocation;
    @NotNull final Path objectsLocation;
    @NotNull final Path logsLocation;

    public FileSystem(@NotNull Path workingDirectory) {
        this(workingDirectory, false);
    }

    public FileSystem(@NotNull Path workingDirectory, boolean isInit) {
        this.workingDirectory = workingDirectory;
        Path tmp = Paths.get(workingDirectory + File.separator + ".mygit");
        if (!isInit) {
            while (Files.notExists(tmp)) {
                tmp = tmp.getParent().getParent();
                if (tmp == null) {
                    tmp = Paths.get("");
                    gitExists = false;
                    break;
                }
                tmp = Paths.get(tmp + File.separator + ".mygit");
            }
        }
        gitLocation = tmp;
        indexLocation = Paths.get(gitLocation + File.separator + "index");
        refsLocation = Paths.get(gitLocation + File.separator + "refs");
        headLocation = Paths.get(gitLocation + File.separator + "HEAD");
        objectsLocation = Paths.get(gitLocation + File.separator + "objects");
        logsLocation = Paths.get(gitLocation + File.separator + "logs");
    }

    @NotNull
    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    @NotNull
    public Path getGitLocation() {
        return gitLocation;
    }

    @NotNull public Path getFolderWithGitLocation() {
        return gitLocation.getParent();
    }
    @NotNull
    public Path getIndexLocation() {
        return indexLocation;
    }

    @NotNull
    public Path getRefsLocation() {
        return refsLocation;
    }

    @NotNull
    public Path getHeadLocation() {
        return headLocation;
    }

    @NotNull
    Path getObjectsLocation() {
        return objectsLocation;
    }

    @NotNull
    Path getLogsLocation() {
        return logsLocation;
    }

    public boolean gitExists() {
        return gitExists;
    }

    public boolean gitNotExists() {
        return !gitExists;
    }

    public abstract void createDirectory(@NotNull Path path) throws IOException;

    public abstract void createFileOrClearIfExists(@NotNull Path path) throws IOException;

    void createFileIfNotExists(@NotNull Path path) throws IOException {
        if (Files.notExists(path)) {
            createFileOrClearIfExists(path);
        }
    }

    public abstract void appendToFile(@NotNull Path path, @NotNull String s) throws IOException;

    @NotNull
    public abstract Path buildRefLocation(@NotNull String s);

    @NotNull
    public abstract Path buildLogLocation(@NotNull String s);

    @NotNull
    public abstract Path buildObjectLocation(@NotNull String objectHash);

    public abstract void writeToFile(@NotNull Path path, @NotNull byte[] content)
            throws IOException;

    public abstract void writeToFile(@NotNull Path path, @NotNull String s) throws IOException;

    public abstract void writeToFile(@NotNull Path path, @NotNull List<String> content)
            throws IOException;

    public abstract boolean exists(@NotNull Path path);

    public boolean notExists(@NotNull Path path) {
        return !exists(path);
    }

    @NotNull
    public abstract List<String> getFileContentLineByLine(@NotNull Path path) throws IOException;

    @NotNull
    public Pair<List<String>, List<String>> splitLines(@NotNull Path path) throws IOException {
        List<String> content = getFileContentLineByLine(path);
        List<String> l = new ArrayList<>();
        List<String> r = new ArrayList<>();
        for (int i = 0; i < content.size(); i += 2) {
            l.add(content.get(i));
            r.add(content.get(i + 1));
        }
        return new Pair<>(l, r);
    }

    @NotNull
    public List<String> zipLines(@NotNull Pair<List<String>, List<String>> content)
            throws IOException {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < content.getKey().size(); i++) {
            res.add(content.getKey().get(i));
            res.add(content.getValue().get(i));
        }
        return res;
    }

    @NotNull
    public abstract Path toAbsolute(@NotNull Path path);

    @NotNull
    public String getFileContentAsString(@NotNull Path path) throws IOException {
        List<String> lines = getFileContentLineByLine(path);
        String res = "";
        for (int i = 0; i < lines.size(); i++) {
            res += lines.get(i);
            if (i + 1 != lines.size()) {
                res += "\n";
            }
        }
        return res;
    }

    public abstract void deleteFile(@NotNull Path path) throws IOException;

    @NotNull
    public List<Path> getFolderContent(@NotNull Path path) throws IOException {
        return FileUtils.listFiles(path.toFile(), null, true)
                .stream()
                .map(File::toPath)
                .collect(Collectors.toList());
    }

    public abstract void restoreFiles(@NotNull Pair<List<String>, List<String>> content)
            throws IOException;

    public abstract byte[] getFileContentAsByteArray(@NotNull Path path) throws IOException;

    public void copyFile(@NotNull Path src, @NotNull Path dest) throws IOException {
        writeToFile(dest, getFileContentAsByteArray(src));
    }

    @NotNull
    public abstract Path buildTreeLocation(@NotNull String branchName) throws IOException;

    public abstract void restoreFile(@NotNull String pathAsString, @NotNull String objectHash)
            throws IOException;

    public abstract void createGitDirectoriesAndFiles() throws IOException;

}
