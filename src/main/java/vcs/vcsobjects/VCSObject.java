package vcs.vcsobjects;

import vcs.vcsexceptions.ContentWriteException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * class for VCS objects. can create SHA1 hash from object content
 */
public abstract class VCSObject {

    private static final int HASH_LENGTH = 40;
    private final String hash;
    private final String type;
    private final int size;
    private final byte[] content;

    VCSObject(String hash, String type, int size, byte[] content) {
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
    public void addObject(Path objectsLocation) throws ContentWriteException {
        Path objectLocation = Paths.get(objectsLocation + File.separator + getHash());
        try {
            Files.write(objectLocation, getContent());
        } catch (IOException e) {
            throw new ContentWriteException();
        }
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

    /**
     * checks whether s matches SHA1 hash format
     */
    public static boolean isHash(String s) {
        if (s.length() != VCSObject.HASH_LENGTH){
            return false;
        }
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (!('0' <= c && c <= '9' || 'a' <= c && c <= 'f')){
                return false;
            }
        }
        return true;
    }
}
