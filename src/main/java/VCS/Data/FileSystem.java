package VCS.Data;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class FileSystem {

    public static final Path DEFAULT_WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"));

    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    public Path getGitLocation() {
        return gitLocation;
    }

    public Path getIndexLocation() {
        return indexLocation;
    }

    public Path getRefsLocation() {
        return refsLocation;
    }

    public Path getHEADLocation() {
        return HEADLocation;
    }

    public Path getObjectsLocation() {
        return objectsLocation;
    }

    public Path getLogsLocation() {
        return logsLocation;
    }

    final Path workingDirectory;
    private final Path gitLocation;
    private final Path indexLocation;
    final Path refsLocation;
    private final Path HEADLocation;
    final Path objectsLocation;
    final Path logsLocation;

    public FileSystem(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        gitLocation = Paths.get(workingDirectory + File.separator + ".mygit");
        indexLocation = Paths.get(gitLocation + File.separator + "index");
        refsLocation = Paths.get(gitLocation + File.separator + "refs");
        HEADLocation = Paths.get(gitLocation + File.separator + "HEAD");
        objectsLocation = Paths.get(gitLocation + File.separator + "objects");
        logsLocation = Paths.get(gitLocation + File.separator + "logs");
    }

    public abstract void createDirectory(Path path) throws IOException;

    public abstract void createFileOrClearIfExists(Path headLocation) throws IOException;

    void createFileIfNotExists(Path path) throws IOException {
        if (Files.notExists(path)) {
            createFileOrClearIfExists(path);
        }
    }

    public abstract void appendToFile(Path path, String message) throws IOException;

    public abstract Path buildRefLocation(String branchName);

    public abstract Path buildLogLocation(String branchName);

    public abstract Path buildObjectLocation(String objectHash);

    public abstract void writeToFile(Path path, byte[] content) throws IOException;

    public abstract void writeToFile(Path path, String message) throws IOException;

    public abstract void writeToFile(Path path, List<String> content) throws IOException;

    public abstract boolean exists(Path path);

    public boolean notExists(Path path) {
        return !exists(path);
    }

    public abstract List<String> getFileContentLineByLine(Path path) throws IOException;

    public Pair<List<String>, List<String>> splitLines(Path path) throws IOException {
        List<String> content = getFileContentLineByLine(path);
        List<String> l = new ArrayList<>();
        List<String> r = new ArrayList<>();
        for (int i = 0; i < content.size(); i += 2) {
            l.add(content.get(i));
            r.add(content.get(i + 1));
        }
        return new Pair<>(l, r);
    }

    public List<String> zipLines(Pair<List<String>, List<String>> content)
            throws IOException {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < content.getKey().size(); i++) {
            res.add(content.getKey().get(i));
            res.add(content.getValue().get(i));
        }
        return res;
    }

    public abstract Path toAbsolute(Path path);

    public String getFileContentAsString(Path path) throws IOException {
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

    public abstract void deleteFile(Path path) throws IOException;

    public List<String> getFolderContent(Path path) throws IOException {
        List <String> res = new ArrayList<>();
        File[] files = path.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                res.add(file.getName());
            }
        }
        return res;
    }

    public abstract void restoreFiles(Pair<List<String>, List<String>> content) throws IOException;

    public abstract byte[] getFileContentAsByteArray(Path path) throws IOException;

    public abstract void copyFile(Path commitPath, Path indexLocation) throws IOException;

    public abstract Path buildTreeLocation(String branchName) throws IOException;
}
