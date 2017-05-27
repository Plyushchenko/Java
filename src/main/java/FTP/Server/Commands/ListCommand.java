package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** List command*/
public class ListCommand implements ClientCommandOnServerSide {

    @NotNull private final String pathAsString;
    @Nullable private byte[] response;

    ListCommand(@NotNull String path) {
        pathAsString = path;
    }

    /**
     * Build list of files and directories as byte array
     * @throws IOException Unknown IO problem
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
                Collections.sort(paths);
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
                for (Path path : paths) {
                    os.writeUTF(path.getFileName().toString());
                    os.writeBoolean(Files.isDirectory(path));
                }
            }
            os.flush();
            response = bs.toByteArray();
        }
    }

    @Nullable
    @Override
    public byte[] getResponse() {
        return response;
    }

}