package VCS.Objects;

import javafx.util.Pair;
import VCS.Data.FileSystem;
import VCS.Objects.GitObjects.Blob;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Index
 */
public class Index {

    private final FileSystem fileSystem;

    /**
     * Create Index instance
     * @param fileSystem File system
     */
    public Index(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Update index file content with passed files.
     * <sup>
     * Read current index file content.
     * Do the following for each file from passed files:
     * If the state of the file in the working folder is not the same as the state of file in index
     * then build blob and update hash of this file.
     * If the file is not represented in index then build blob, add this file and it's hash in index
     * </sup>
     * @param filePaths Paths of files
     * @throws IOException Unknown IO problem
     */
    public void updateContent(List<String> filePaths) throws IOException {
        Pair<List<String>, List<String>> content = fileSystem.splitLines(
                fileSystem.getIndexLocation());
        List<String> indexedFiles = content.getKey();
        List<String> indexedHashes = content.getValue();
        for (String fileToAdd : filePaths) {
            fileToAdd = fileSystem.toAbsolute(Paths.get(fileToAdd)).toString();
            int i = indexedFiles.indexOf(fileToAdd);
            String fileHash = Blob.buildBlob(fileSystem, Paths.get(fileToAdd)).getHash();
            if (i == -1) {
                indexedFiles.add(fileToAdd);
                indexedHashes.add(fileHash);
            } else {
                if (!indexedHashes.get(i).equals(fileHash)) {
                    indexedHashes.set(i, fileHash);
                }
            }
        }
        fileSystem.writeToFile(fileSystem.getIndexLocation(), fileSystem.zipLines(content));
    }

}
