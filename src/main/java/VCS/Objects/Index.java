package VCS.Objects;

import javafx.util.Pair;
import VCS.Data.FileSystem;
import VCS.Objects.GitObjects.Blob;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;


public class Index {

    private final FileSystem fileSystem;
    public Index(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

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
