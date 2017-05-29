package VCS.Data;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** File utils for git (mostly wrappers of java.io/java.nio commands */
public class FileSystemImpl extends FileSystem {

    public FileSystemImpl(@NotNull Path workingDirectory) {
        super(workingDirectory);
    }

    public FileSystemImpl(@NotNull Path workingDirectory, boolean isInit) {
        super(workingDirectory, isInit);
    }

    @Override
    public void createDirectory(@NotNull Path path) throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public void createFileOrClearIfExists(@NotNull Path path) throws IOException {
        createDirectory(path.getParent());
        Files.deleteIfExists(path);
        Files.createFile(path);
    }

    @Override
    public void appendToFile(@NotNull Path path, @NotNull String message) throws IOException {
        createFileIfNotExists(path);
        Files.write(path, message.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    @NotNull
    @Override
    public Path buildRefLocation(@NotNull String branchName) {
        return Paths.get(refsLocation + File.separator + branchName);
    }

    @Override
    public void writeToFile(@NotNull Path path, @NotNull byte[] message) throws IOException {
        createFileOrClearIfExists(path);
        Files.write(path, message);
    }

    @Override
    public void writeToFile(@NotNull Path path, @NotNull String message) throws IOException {
        writeToFile(path, message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void writeToFile(@NotNull Path path, @NotNull List<String> content) throws IOException {
        createFileOrClearIfExists(path);
        for (int i = 0; i < content.size(); i++) {
            appendToFile(path, content.get(i));
            if (i + 1 != content.size()) {
                appendToFile(path, "\n");
            }
        }
    }

    @NotNull
    @Override
    public Path buildLogLocation(@NotNull String branchName) {
        return Paths.get(logsLocation + File.separator + branchName);
    }

    @NotNull
    @Override
    public Path buildObjectLocation(@NotNull String objectHash) {
        return Paths.get(objectsLocation + File.separator + objectHash);
    }

    @Override
    public boolean exists(@NotNull Path path) {
        return Files.exists(path);
    }

    @NotNull
    @Override
    public List<String> getFileContentLineByLine(@NotNull Path path) throws IOException {
        return Files.readAllLines(path);
    }

    @NotNull
    @Override
    public Path toAbsolute(@NotNull Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        return Paths.get(workingDirectory + File.separator + path);
    }

    @Override
    public void deleteFile(@NotNull Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    @NotNull
    @Override
    public List<Path> getFolderContent(@NotNull Path path) throws IOException {
        return FileUtils.listFiles(path.toFile(), null, true)
                .stream()
                .map(File::toPath)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<String> getFolderContentAsListOfString(@NotNull Path path) throws IOException {
        List <String> res = new ArrayList<>();
        File[] files = path.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                res.add(file.getName());
            }
        }
        return res;
    }


    @Override
    public void restoreFiles(@NotNull Pair<List<String>, List<String>> content) throws IOException {
        List<String> filePaths = content.getKey();
        List<String> fileHashes = content.getValue();
        for (int i = 0; i < filePaths.size(); i++) {
            byte[] fileContent = getFileContentAsByteArray(buildObjectLocation(fileHashes.get(i)));
           writeToFile(Paths.get(filePaths.get(i)), fileContent);
        }
    }

    @Override
    public byte[] getFileContentAsByteArray(@NotNull Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    @NotNull
    @Override
    public Path buildTreeLocation(@NotNull String branchName) throws IOException {
        Path refLocation = buildRefLocation(branchName);
        Path commitLocation =  buildObjectLocation(getFileContentAsString(refLocation));
        return buildObjectLocation(getFileContentAsString(commitLocation));
    }

    @Override
    public void restoreFile(@NotNull String pathAsString, @NotNull String objectHash)
            throws IOException {
        Path path = Paths.get(pathAsString);
        byte[] content = getFileContentAsByteArray(buildObjectLocation(objectHash));
        writeToFile(path, content);
    }

    @Override
    public void createGitDirectoriesAndFiles() throws IOException {
        createDirectory(getGitLocation());
        createDirectory(getLogsLocation());
        createDirectory(getObjectsLocation());
        createDirectory(getRefsLocation());
        createFileOrClearIfExists(getHeadLocation());
        createFileOrClearIfExists(getIndexLocation());
    }

}
