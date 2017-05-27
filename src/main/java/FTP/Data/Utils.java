package FTP.Data;

import FTP.ClientCommand;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/** Some utils for data transformations*/
public class Utils {

    @NotNull
    private static byte[] buildByteArrayFromArgs(@NotNull String[] args) throws IOException {
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(bs)) {
            os.writeInt(ClientCommand.CLIENT_COMMAND_CODES.get(args[0]));
            os.writeUTF(args[1]);
            os.flush();
            return bs.toByteArray();
        }
    }

    public static void sendArgs(@NotNull SocketChannel socketChannel, @NotNull String[] args)
            throws IOException {
        new ChannelByteWriter(buildByteArrayFromArgs(args)).writeAll(socketChannel);
        socketChannel.shutdownOutput();
    }

}
