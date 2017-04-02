package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * class for VCS objects. can create SHA1 hash from object content
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

    public String getHash() {
        return hash;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    private byte[] getContent() {
        return content;
    }

    /**
     * add VCS object content to objects/getHash() file
     */
    public void addObject() throws IOException {
        fileSystem.writeToFile(Paths.get(
                fileSystem.getObjectsLocation() + File.separator + getHash()), getContent());
    }

    /**
     * build SHA1 hash from VCS object content as byte array
     */
    static String buildHash(byte[] content){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            //ignore
        }
        if (digest == null)
            return "";
        digest.update(content);
        return javax.xml.bind.DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();

    }

}
