package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/** List command*/
public class ListCommand implements ClientCommandOnServerSide {

    private final String pathAsString;
    private byte[] response;

    ListCommand(String path) {
        pathAsString = path;
    }

    /**
     * Build list of files and directories as byte array
     * @throws IOException
     */
    @Override
    public void run() throws IOException {
        List<Path> paths = null;
        boolean shouldSendZero = false;
        try {
            Path path = Paths.get(pathAsString);
            if (!Files.isDirectory(path) || !Files.exists(path)) {
                shouldSendZero = true;
            } else {
                paths = Files.list(path).collect(Collectors.toList());
            }
        } catch (Exception e) {
            shouldSendZero = true;
        }
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream();
             DataOutputStream os = new DataOutputStream(bs)) {
            if (shouldSendZero) {
                os.writeInt(0);
            } else {
                os.writeInt(paths.size());
                System.out.println(paths.size());
                for (Path path : paths) {
                    os.writeUTF(path.getFileName().toString());
                    os.writeBoolean(Files.isDirectory(path));
                }
            }
            os.flush();
            response = bs.toByteArray();
        }
    }


    @NotNull
    @Override
    public byte[] getResponse() {
        return response;
    }

}