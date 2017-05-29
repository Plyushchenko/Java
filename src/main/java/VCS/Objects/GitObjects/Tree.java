package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Git tree */
public class Tree extends GitObject {

    private Tree(@NotNull FileSystem fileSystem, @NotNull byte[] content) {
        super(fileSystem, buildHash(content), "tree", content.length, content);
    }

    public Tree(@NotNull FileSystem fileSystem, @NotNull List<String> filesToCommit,
                @NotNull List<String> hashesOfFilesToCommit) {
        this(fileSystem, buildContent(filesToCommit, hashesOfFilesToCommit));
    }

    /**
     * build tree content like this:
     * file1
     * hash1 (hash of blob which is built from file1 content)
     * file2
     * hash2
     * ...
     */
    @NotNull
    private static byte[] buildContent(@NotNull List<String> filesToCommit,
                                       @NotNull List<String> hashesOfFilesToCommit) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < filesToCommit.size(); i++) {
            buffer.append(filesToCommit.get(i)).append("\n").append(hashesOfFilesToCommit.get(i))
                    .append("\n");
        }
        return buffer.toString().getBytes();
    }
}
