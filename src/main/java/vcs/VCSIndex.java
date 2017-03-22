package vcs;

import vcs.vcsexceptions.ContentReadException;
import vcs.vcsexceptions.IndexReadException;
import vcs.vcsexceptions.IndexWriteException;
import vcs.vcsexceptions.NothingChangedSinceLastAddException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static vcs.VCSFileUtils.*;
import static vcs.vcsobjects.Blob.buildBlob;

class VCSIndex {

    static void updateIndex(List<String> indexContent) throws IndexWriteException {
        try {
            createEmptyFile(INDEX_LOCATION);
        } catch (IOException e) {
            throw new IndexWriteException();
        }

        for (String line : indexContent) {
            try {
                Files.write(INDEX_LOCATION, line.getBytes(), StandardOpenOption.APPEND);
                Files.write(INDEX_LOCATION, newLineAsBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new IndexWriteException();
            }
        }
    }

    static List<String> buildIndexContentAfterAdd(List<Path> filesToAdd)
            throws IndexReadException, ContentReadException, NothingChangedSinceLastAddException {
        List<String> indexContent = getIndexContent();
        List<Path> indexedFiles = new ArrayList<>();
        List<String> indexedHashes = new ArrayList<>();
        for (int i = 0; i < indexContent.size(); i += 2) {
            indexedFiles.add(Paths.get(indexContent.get(i)));
            indexedHashes.add(indexContent.get(i + 1));
        }
        indexedFiles.forEach(System.out::println);
        indexedHashes.forEach(System.out::println);

        boolean somethingChangedSinceLastAdd = false;
        for (Path fileToAdd : filesToAdd) {
            if (!fileToAdd.isAbsolute()) {
                fileToAdd = Paths.get(CURRENT_DIRECTORY + File.separator + fileToAdd);
            }
            int i = indexedFiles.indexOf(fileToAdd);
            if (i == -1) {
                indexedFiles.add(fileToAdd);
                indexedHashes.add(buildBlob(fileToAdd).getHash());
                somethingChangedSinceLastAdd = true;
            } else {
                String fileHash = buildBlob(fileToAdd).getHash();
                if (!indexedHashes.contains(fileHash)) {
                    indexedHashes.set(i, fileHash);
                    somethingChangedSinceLastAdd = true;
                }
            }
        }
        indexedFiles.forEach(System.out::println);
        indexedHashes.forEach(System.out::println);
        if (!somethingChangedSinceLastAdd) {
            throw new NothingChangedSinceLastAddException();
        }
        List<String> indexContentAfterAdd = new ArrayList<>();
        for (int i = 0; i < indexedFiles.size(); i++) {
            indexContentAfterAdd.add(indexedFiles.get(i).toString());
            indexContentAfterAdd.add(indexedHashes.get(i));
        }
        return indexContentAfterAdd;
    }

    static List<String> getIndexContent() throws IndexReadException {
        try {
            return Files.readAllLines(INDEX_LOCATION);
        } catch (IOException e) {
            throw new IndexReadException();
        }
    }

}
