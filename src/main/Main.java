import MD5.MD5MultiThread;
import MD5.MD5SingleThread;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Main {

    /**
     * compare time of hashing for singlethread version and FJP version
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        long t0 = System.currentTimeMillis();
        new MD5SingleThread().getHash(Paths.get(args[0]));
        long t1 = System.currentTimeMillis();
        new MD5MultiThread().getHash(Paths.get(args[0]));
        long t2 = System.currentTimeMillis();
        System.out.println("singlethread: " + (t1 - t0) + "\nmultithread: " + (t2 - t1));
    }
}
