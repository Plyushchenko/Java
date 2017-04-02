package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;

import java.util.List;

public class Tree extends GitObject {

    private Tree(FileSystem fileSystem, byte[] content) {
        super(fileSystem, buildHash(content), "tree", content.length, content);
    }

    public Tree(FileSystem fileSystem, List<String> filesToCommit,
                List<String> hashesOfFilesToCommit) {
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
    private static byte[] buildContent(List<String> filesToCommit, List<String>
            hashesOfFilesToCommit) {
        String buffer = "";
        for (int i = 0; i < filesToCommit.size(); i++) {
            buffer += filesToCommit.get(i) + "\n" + hashesOfFilesToCommit.get(i) + "\n";
        }
        return buffer.getBytes();
    }
}
