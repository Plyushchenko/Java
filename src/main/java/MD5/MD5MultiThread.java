package MD5;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class MD5MultiThread implements MD5 {

    @Override
    public byte[] getHash(Path path) throws IOException, NoSuchAlgorithmException {
        List<Byte> tmp = new ForkJoinPool().invoke(new MD5RecursiveTask(path));
        return buildArrayFromList(tmp);
    }

    private byte[] buildArrayFromList(List<Byte> list) {
        byte[] res = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }

}
