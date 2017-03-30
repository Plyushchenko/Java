package MD5;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public interface MD5 {

    byte []getHash(Path path) throws IOException, NoSuchAlgorithmException;

}
