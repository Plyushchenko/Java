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

    private final SocketChannel socketChannel;
    private final String[] args;

    public ListCommand(SocketChannel socketChannel, String[] args) {
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
     * @return Server response
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String receiveResponse() throws IOException {
        byte[] responseAsByteArray = new ChannelByteReader().readAll(socketChannel);
        String response = "size = ";
        try (ByteArrayInputStream bs = new ByteArrayInputStream(responseAsByteArray);
                DataInputStream is =  new DataInputStream(bs)) {
            int size = is.readInt();
            response += String.valueOf(size);
            response += "\nname is_dir\n";
            while (size-- > 0) {
                response += is.readUTF();
                response += " ";
                response += is.readBoolean();
                response += "\n";
            }
        }
        return response;
    }

}
