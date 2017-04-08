package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Git object
 */
public abstract class GitObject {

    private static final int HASH_LENGTH = 40;
    private final FileSystem fileSystem;
    private final String hash;
    private final String type;
    private final int size;
    private final byte[] content;

    GitObject(FileSystem fileSystem, String hash, String type, int size, byte[] content) {
        this.fileSystem = fileSystem;
        this.hash = hash;
        this.type = type;
        this.size = size;
        this.content = content;
    }

    /**
     * Get SHA1 hash
     * @return SHA1 hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * Get type of Git object (i.e. blob/commit/tree)
     * @return type of Git object
     */
    public String getType() {
        return type;
    }

    /**
     * Get size of Git objects in bytes
     * @return size of Git object
     */
    public int getSize() {
        return size;
    }

    private byte[] getContent() {
        return content;
    }

    /**
     * add VCS object content to objects/getHash() file
     * @throws IOException Unknown IO problem
     */
    public void addObject() throws IOException {
        fileSystem.writeToFile(Paths.get(
                fileSystem.getObjectsLocation() + File.separator + getHash()), getContent());
    }

    static String buildHash(byte[] content) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            //ignore
        }
        if (digest == null) {
            return "";
        }
        digest.update(content);
        return javax.xml.bind.DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();

    }

}
