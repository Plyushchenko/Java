package vcs.vcsobjects;

import vcs.vcsexceptions.ContentReadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Blob extends VCSObject{

    private Blob(byte[] content) {
        super(buildHash(content), "blob", content.length, content);
    }

    /**
     * build blob from file
     */
    public static Blob buildBlob(Path fileToCommit) throws ContentReadException {
        try {
            return new Blob(Files.readAllBytes(fileToCommit));
        } catch (IOException e) {
            throw new ContentReadException();
        }
    }

}
