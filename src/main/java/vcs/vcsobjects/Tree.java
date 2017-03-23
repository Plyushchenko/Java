package vcs.vcsobjects;

import java.nio.file.Path;
import java.util.List;

public class Tree extends VCSObject {

    private Tree(byte[] content) {
        super(buildHash(content), "tree", content.length, content);
    }

    public Tree(List<Path> filesToCommit, List<String> hashesOfFilesToCommit) {
        this(buildContent(filesToCommit, hashesOfFilesToCommit));
    }

    /**
     * build tree content like this:
     * file1
     * hash1 (hash of blob which is built from file1 content)
     * file2
     * hash2
     * ...
     */
    private static byte[] buildContent(List<Path> filesToCommit, List<String> hashesOfFilesToCommit) {
        String buffer = "";
        for (int i = 0; i < filesToCommit.size(); i++){
            buffer += filesToCommit.get(i) + "\n" + hashesOfFilesToCommit.get(i) + "\n";
        }
        return buffer.getBytes();
    }
}
