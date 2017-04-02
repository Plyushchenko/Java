package VCS.Data;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileSystemImpl extends FileSystem {
    public FileSystemImpl(Path workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public void createDirectory(Path path) throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public void createFileOrClearIfExists(Path path) throws IOException {
        createDirectory(path.getParent());
        Files.deleteIfExists(path);
        Files.createFile(path);
    }

    @Override
    public void appendToFile(Path path, String message) throws IOException {
        createFileIfNotExists(path);
        Files.write(path, message.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    @Override
    public Path buildRefLocation(String branchName) {
        return Paths.get(refsLocation + File.separator + branchName);
    }

    @Override
    public void writeToFile(Path path, byte[] message) throws IOException {
        createFileOrClearIfExists(path);
        Files.write(path, message);
    }

    @Override
    public void writeToFile(Path path, String message) throws IOException {
        writeToFile(path, message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void writeToFile(Path path, List<String> content) throws IOException {
        createFileOrClearIfExists(path);
        for (int i = 0; i < content.size(); i++) {
            appendToFile(path, content.get(i));
            if (i + 1 != content.size()) {
                appendToFile(path, "\n");
            }
        }
    }

    @Override
    public Path buildLogLocation(String branchName) {
        return Paths.get(logsLocation + File.separator + branchName);
    }

    @Override
    public Path buildObjectLocation(String objectHash) {
        return Paths.get(objectsLocation + File.separator + objectHash);
    }

    @Override
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    @Override
    public List<String> getFileContentLineByLine(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    @Override
    public Path toAbsolute(Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        return Paths.get(workingDirectory + File.separator + path);
    }

    @Override
    public void deleteFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    @Override
    public void restoreFiles(Pair<List<String>, List<String>> content) throws IOException {
        List<String> filePaths = content.getKey();
        List<String> fileHashes = content.getValue();
        for (int i = 0; i < filePaths.size(); i++) {
            byte[] fileContent = getFileContentAsByteArray(buildObjectLocation(fileHashes.get(i)));
           writeToFile(Paths.get(filePaths.get(i)), fileContent);
        }
    }

    @Override
    public byte[] getFileContentAsByteArray(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    @Override
    public void copyFile(Path src, Path dest) throws IOException {
        writeToFile(dest, getFileContentAsByteArray(src));
    }

    @Override
    public Path buildCommitLocation(String branchName) throws IOException {
        Path refLocation = buildRefLocation(branchName);
        return buildObjectLocation(getFileContentAsString(refLocation));
    }

    @Override
    public Path buildTreeLocation(String branchName) throws IOException {
        return buildObjectLocation(getFileContentAsString(buildCommitLocation(branchName)));
    }

}
