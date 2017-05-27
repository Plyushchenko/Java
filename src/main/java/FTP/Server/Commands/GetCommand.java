package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Get command*/
public class GetCommand implements ClientCommandOnServerSide {

    @NotNull private final String pathAsString;
    @Nullable private byte[] response;

    GetCommand(@NotNull String path) {
        pathAsString = path;
    }

    /**
     * Read file content and write it into 'response' array
     * @throws IOException Unknown IO problem
     */
    @Override
    public void run() throws IOException {
        byte[] fileContent = {};
        boolean shouldSendZero = false;
        try {
            Path path = Paths.get(pathAsString);
            if (!Files.isRegularFile(path) || !Files.exists(path)) {
                shouldSendZero = true;
            } else {
                fileContent = Files.readAllBytes(path);
            }
        } catch (Exception e) {
            shouldSendZero = true;
        }
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream();
             DataOutputStream os = new DataOutputStream(bs)) {
            if (shouldSendZero) {
                os.writeLong(0);
            } else {
                os.writeLong(fileContent.length);
                os.write(fileContent);
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