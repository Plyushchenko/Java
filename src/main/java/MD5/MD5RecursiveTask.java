package MD5;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static MD5.MD5MultiThread.buildListFromArray;

/**
 * RecursiveTask for MD5 hashing
 */
class MD5RecursiveTask extends RecursiveTask<List<Byte>> {

    private final Path path;

    MD5RecursiveTask(Path path) {
        this.path = path;
    }

    /**
     * build MD5 hash
     * if path is a folder then fork for all files and subfolders then join
     * if path is a file then just build hash of file
     */
    @Override
    protected List<Byte> compute() {
        try {
            if (Files.isDirectory(path)) {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(path.getFileName().toString().getBytes(StandardCharsets.UTF_8));
                List<MD5RecursiveTask> tasks = new ArrayList<>();
                File[] folderContent = new File(path.toString()).listFiles();
                if (folderContent != null) {
                    for (File file : folderContent) {
                        MD5RecursiveTask task = new MD5RecursiveTask(file.toPath());
                        task.fork();
                        tasks.add(task);
                    }
                }
                for (MD5RecursiveTask task : tasks) {
                    byte[] hash = new byte[16];
                    List<Byte> taskResult = task.join();
                    for (int i = 0; i < 16; i++) {
                        hash[i] = taskResult.get(i);
                    }
                    digest.update(hash);
                }
                return buildListFromArray(digest.digest());
            }
            return buildListFromArray(new MD5SingleThread().getHashOfFile(path));
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

}
