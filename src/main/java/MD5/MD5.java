package MD5;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * interface for MD5
 */
public interface MD5 {

    /**
     * builds MD5 hash
     * @param path for path (file or folder)
     * @return MD5 hash
     * @throws IOException for unknown file system problem
     * @throws NoSuchAlgorithmException for unknown digest problems
     */
    byte[] getHash(Path path) throws IOException, NoSuchAlgorithmException;

}
