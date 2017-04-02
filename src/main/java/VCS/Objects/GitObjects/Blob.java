package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Blob
 */
public class Blob extends GitObject {

    private Blob(FileSystem fileSystem, byte[] content) {
        super(fileSystem, buildHash(content), "blob", content.length, content);
    }

    /**
     * Build blob from file
     * @param fileSystem File system
     * @param path path
     * @return Blob which hash if the hash of the passed file
     * @throws IOException Unknown IO problem
     */
    public static Blob buildBlob(FileSystem fileSystem, Path path) throws IOException {
        return new Blob(fileSystem, Files.readAllBytes(path));
    }

}
