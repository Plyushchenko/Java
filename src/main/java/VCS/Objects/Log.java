package VCS.Objects;

import VCS.Data.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

public class Log {
    private final FileSystem fileSystem;
    private final Path logLocation;
    public Log(FileSystem fileSystem, String branchName) {
        this.fileSystem = fileSystem;
        logLocation = fileSystem.buildLogLocation(branchName);
    }

    public String read() throws IOException {
        return fileSystem.getFileContentAsString(logLocation);
    }

    public void write(String s) throws IOException {
        fileSystem.appendToFile(logLocation, s);
    }
}
