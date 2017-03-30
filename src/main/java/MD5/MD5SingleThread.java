package MD5;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MD5SingleThread implements MD5{

    @Override
    public byte[] getHash(Path path) throws IOException, NoSuchAlgorithmException {
        if (Files.isDirectory(path)) {
            return getHashOfFolder(path);
        }
        return getHashOfFile(path);
    }

    byte[] getHashOfFile(Path path) throws IOException, NoSuchAlgorithmException {
        System.out.println("  FILE: " + path);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        DigestInputStream dis = new DigestInputStream(new FileInputStream(path.toString()), digest);
        byte[] buffer = new byte[1 << 20];
        while (dis.read(buffer) > -1);
        return digest.digest();
    }


    private byte[] getHashOfFolder(Path path) throws NoSuchAlgorithmException, IOException {
        System.out.println("FOLDER: + " + path);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(path.getFileName().toString().getBytes(StandardCharsets.UTF_8));
        File[] folderContent = new File(path.toString()).listFiles();
        if (folderContent != null) {
            for (File file : folderContent) {
                if (file.isDirectory()) {
                    digest.update(getHashOfFolder(file.toPath()));
                } else {
                    digest.update(getHashOfFile(file.toPath()));
                }
                System.out.println(Arrays.toString(digest.digest()));
            }
        }
        return digest.digest();
    }

}
