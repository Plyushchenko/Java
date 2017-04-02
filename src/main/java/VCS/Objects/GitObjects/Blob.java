package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Blob extends GitObject {

    private Blob(FileSystem fileSystem, byte[] content) {
        super(fileSystem, buildHash(content), "blob", content.length, content);
    }

    /**
     * build blob from file
     */
    public static Blob buildBlob(FileSystem fileSystem, Path fileToCommit) throws IOException {
        return new Blob(fileSystem, Files.readAllBytes(fileToCommit));
    }

}
