package VCS.Objects;

import VCS.Data.FileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/** Log */
public class Log {

    @NotNull private final FileSystem fileSystem;
    @NotNull private final Path logLocation;

    public Log(@NotNull FileSystem fileSystem, @NotNull String branchName) {
        this.fileSystem = fileSystem;
        System.out.println("branchName = " + branchName);
        logLocation = fileSystem.buildLogLocation(branchName);
    }

    /**
     * Read log file content
     * @return Log content
     * @throws IOException Unknown IO problem
     */
    @NotNull
    public String read() throws IOException {
        return fileSystem.getFileContentAsString(logLocation);
    }

    /**
     * Write to log file
     * @param s String which will be written
     * @throws IOException Unknown IO problem
     */
    public void write(@NotNull String s) throws IOException {
        System.out.println("logLocation = " + logLocation);
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
