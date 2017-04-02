package VCS.Objects;

import VCS.Data.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Log
 */
public class Log {
    private final FileSystem fileSystem;
    private final Path logLocation;

    /**
     * Build Log instance
     * @param fileSystem File system
     * @param branchName Branch name
     */
    public Log(FileSystem fileSystem, String branchName) {
        this.fileSystem = fileSystem;
        logLocation = fileSystem.buildLogLocation(branchName);
    }

    /**
     * Read log file content
     * @return Log content
     * @throws IOException Unknown IO problem
     */
    public String read() throws IOException {
        return fileSystem.getFileContentAsString(logLocation);
    }

    /**
     * Write to log file
     * @param s String which will be written
     * @throws IOException Unknown IO problem
     */
    public void write(String s) throws IOException {
        fileSystem.appendToFile(logLocation, s);
    }

    /**
     * Delete log file
     * @throws IOException Unknown IO problem
     */
    public void delete() throws IOException {
        fileSystem.deleteFile(logLocation);
    }
}
