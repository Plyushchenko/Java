import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public abstract class VCSObject {

    public static final int HASH_LENGTH = 40;
    private final String hash;
    private final String type;
    private final int size;
    private final byte[] content;

    public VCSObject(String hash, String type, int size, byte[] content) {
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

    public byte[] getContent() {
        return content;
    }

    public void addObject(Path objectsLocation) throws IOException {
        Path objectLocation = Paths.get(objectsLocation + File.separator + getHash());
        Files.write(objectLocation, getContent());
    }

    public static String buildHash(byte[] content){
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
