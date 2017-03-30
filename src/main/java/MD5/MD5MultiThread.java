package MD5;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Multithread type of MD5 hasher
 * use FJP
 */
public class MD5MultiThread implements MD5 {

    @Override
    public byte[] getHash(Path path) throws IOException, NoSuchAlgorithmException {
        List<Byte> tmp = new ForkJoinPool().invoke(new MD5RecursiveTask(path));
        return buildArrayFromList(tmp);
    }

    static byte[] buildArrayFromList(List<Byte> list) {
        byte[] res = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }

    static List<Byte> buildListFromArray(byte[] bytes) {
        List<Byte> res = new ArrayList<>();
        for (byte b : bytes) {
            res.add(b);
        }
        return res;

    }

}
