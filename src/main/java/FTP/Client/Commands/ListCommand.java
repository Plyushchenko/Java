package FTP.Client.Commands;

import FTP.Data.ChannelByteReader;
import FTP.Data.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/** List command*/
public class ListCommand implements Command {

    @NotNull private final SocketChannel socketChannel;
    @NotNull private final String[] args;

    public ListCommand(@NotNull SocketChannel socketChannel, @NotNull String[] args) {
        this.socketChannel = socketChannel;
        this.args = args;
    }

    /**
     * Send arguments to server
     * @throws IOException Unknown IO problem
     */
    @Override
    public void run() throws IOException {
        Utils.sendArgs(socketChannel, args);
    }

    /**
     * Get folder content and return it in following format:
     * <sup>
     * size = 'size'
     * name is_dir
     * name1 is_dir1
     * ...
     * </sup>
     * Unfortunately, there is no way to see the difference between 'empty folder' and 'no folder'
     * because of client-server protocol format
     * @return Server response
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String receiveResponse() throws IOException {
        byte[] responseAsByteArray = new ChannelByteReader().readAll(socketChannel);
        StringBuilder response = new StringBuilder("size = ");
        try (ByteArrayInputStream bs = new ByteArrayInputStream(responseAsByteArray);
                DataInputStream is =  new DataInputStream(bs)) {
            int size = is.readInt();
            response.append(String.valueOf(size));
            response.append("\nname is_dir\n");
            while (size-- > 0) {
                response.append(is.readUTF());
                response.append(" ");
                response.append(is.readBoolean());
                response.append("\n");
            }
        }
        return response.toString();
    }

}
