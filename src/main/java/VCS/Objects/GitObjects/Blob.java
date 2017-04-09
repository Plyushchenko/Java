package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Blob */
public class Blob extends GitObject {

    private Blob(@NotNull FileSystem fileSystem, @NotNull byte[] content) {
        super(fileSystem, buildHash(content), "blob", content.length, content);
    }

    /**
     * Build blob from file
     * @param fileSystem File system
     * @param path path
     * @return Blob which hash if the hash of the passed file
     * @throws IOException Unknown IO problem
     */
    @NotNull
    public static Blob buildBlob(@NotNull FileSystem fileSystem, @NotNull Path path)
            throws IOException {
        return new Blob(fileSystem, Files.readAllBytes(path));
    }

}
