package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Classic git object */
public abstract class GitObject {

    @NotNull private final FileSystem fileSystem;
    @NotNull private final String hash;
    @NotNull private final String type;
    private final int size;
    @NotNull private final byte[] content;

    GitObject(@NotNull FileSystem fileSystem, @NotNull String hash, @NotNull String type, int size,
              @NotNull byte[] content) {
        this.fileSystem = fileSystem;
        this.hash = hash;
        this.type = type;
        this.size = size;
        this.content = content;
    }

    @NotNull
    public String getHash() {
        return hash;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    @NotNull
    private byte[] getContent() {
        return content;
    }

    /**
     * add VCS object content to objects/getHash() file
     * @throws IOException Unknown IO problem
     */
    public void addObject() throws IOException {
        fileSystem.writeToFile(fileSystem.buildObjectLocation(getHash()), getContent());
    }

    @NotNull
    static String buildHash(@NotNull byte[] content) {
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
