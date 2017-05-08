import FTP.Client.Client;
import FTP.Client.ClientImpl;
import FTP.Exceptions.IncorrectArgsException;
import FTP.Server.Server;
import FTP.Server.ServerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.apache.commons.lang3.RandomStringUtils;

public class FtpTests {

    private static final List<String> FILE_CONTENTS = new ArrayList<>(Arrays.asList(
            RandomStringUtils.random((int)1e3),
            RandomStringUtils.random((int)1e4),
            RandomStringUtils.random((int)1e5),
            RandomStringUtils.random((int)1e6)));

    private List<Path> filePaths;
    private List<Path> folderPaths;
    private List<Path> savedPaths;
    private Path folderWithSavedFiles;
    private Server server;

    @Before()
    public void before() throws IOException {
        folderWithSavedFiles = Files.createTempDirectory("saved");
        Path folderWithFiles = Files.createTempDirectory("work");
        filePaths = new ArrayList<>(Arrays.asList(
                Paths.get(folderWithFiles + File.separator + "file0"),
                Paths.get(folderWithFiles + File.separator + "dir" + File.separator + "dir"
                        + File.separator + "file1"),
                Paths.get(folderWithFiles + File.separator + "file2"),
                Paths.get(folderWithFiles + File.separator + "dir" + File.separator + "file3")));
        folderPaths = new ArrayList<>(Arrays.asList(
                folderWithFiles,
                Paths.get(folderWithFiles + File.separator + "dir"),
                Paths.get(folderWithFiles + File.separator + "dir" + File.separator + "dir")));
        savedPaths = new ArrayList<>(Arrays.asList(
                Paths.get(folderWithSavedFiles + File.separator + "file0"),
                Paths.get(folderWithSavedFiles + File.separator + "file1"),
                Paths.get(folderWithSavedFiles + File.separator + "file2"),
                Paths.get(folderWithSavedFiles + File.separator + "file3")));
        for (int i = 0; i < filePaths.size(); i++) {
            Path filePath = filePaths.get(i);
            filePath.getParent().toFile().mkdirs();
            Files.createFile(filePath);
            Files.write(filePath, FILE_CONTENTS.get(i).getBytes());
        }
        server = new ServerImpl();
    }

    @After
    public void after() throws IOException, IncorrectArgsException, InterruptedException {
        server.execute(new String[]{"quit"});
        //Requires some time to finish server not to wait for exception too long
        Thread.sleep(2000);
    }

    @Test
    public void stoppedServerTest() {
       try {
           server.execute(new String[]{"start"});
           //Requires some time to finish server not to wait for exception too long
           Thread.sleep(2000);
           server.execute(new String[]{"stop"});
           //Requires some time to finish server not to wait for exception too long
           Thread.sleep(2000);
           Client client = new ClientImpl(folderWithSavedFiles);
           client.execute(new String[]{"list", "?"});
       } catch (Exception e) {
           assertEquals(e.getMessage(), "Server is off or is stopped");
       }
    }

    @Test
    public void quittedServerTest() {
        try {
            server.execute(new String[]{"quit"});
            //Requires some time to finish server not to wait for exception too long
            Thread.sleep(2000);
            Client client = new ClientImpl(folderWithSavedFiles);
            client.execute(new String[]{"list", "?"});
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Server is off or is stopped");
        }
    }

    @Test
    public void getTest() throws IOException, IncorrectArgsException, InterruptedException {
        server.execute(new String[]{"start"});
        //Requires some time to finish server not to wait for exception too long
        Thread.sleep(2000);
        Client client = new ClientImpl(folderWithSavedFiles);
        for (int i = 0; i < filePaths.size(); i++) {
            client.execute(new String[]{"get", filePaths.get(i).toString()});
            for (int j = 0; j <= i; j++) {
                assertTrue(Files.exists(savedPaths.get(j)));
                assertTrue(Arrays.equals(
                        FILE_CONTENTS.get(j).getBytes(), Files.readAllBytes(savedPaths.get(j))));
            }
            for (int j = i + 1; j < filePaths.size(); j++) {
                assertFalse(Files.exists(savedPaths.get(j)));
            }
        }
        for (Path folderPath : folderPaths) {
            String response = client.execute(new String[]{"get", folderPath.toString()});
            assertEquals(response, "No such file");
        }
    }

    /*
    @Test
    public void f() {
        for (Path path : filePaths) {
            System.out.println(path);
        }
        System.out.println();
        Collections.sort(filePaths);
        for (Path path : filePaths) {
            System.out.println(path);
        }
        System.out.println();
        System.out.println();
        for (Path path : folderPaths) {
            System.out.println(path);
        }
        System.out.println();
        Collections.sort(folderPaths);
        for (Path path : folderPaths) {
            System.out.println(path);
        }
        System.out.println();

    }
    */

    @Test
    public void listTest() throws IOException, IncorrectArgsException, InterruptedException {
        server.execute(new String[]{"start"});
        //Requires some time to finish server not to wait for exception too long
        Thread.sleep(2000);
        Client client = new ClientImpl(folderWithSavedFiles);
        String response = client.execute(new String[]{"list", folderPaths.get(0).toString()});
        assertEquals("size = 3\n" +
                "name is_dir\n" +
                "dir true\n" +
                "file0 false\n" +
                "file2 false\n", response);
        response = client.execute(new String[]{"list", folderPaths.get(1).toString()});
        assertEquals("size = 2\n" +
                "name is_dir\n" +
                "dir true\n" +
                "file3 false\n", response);
        response = client.execute(new String[]{"list", folderPaths.get(2).toString()});
        assertEquals("size = 1\n" +
                "name is_dir\n" +
                "file1 false\n", response);
        for (Path filePath : filePaths) {
            response = client.execute(new String[]{"list", filePath.toString()});
            assertEquals("size = 0\n" +
                    "name is_dir\n", response);
        }
    }

}
