package FTP.Client.Commands;

import FTP.Client.Client;
import FTP.Data.ChannelByteReader;
import FTP.Data.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Get command*/
public class GetCommand implements Command {

    private final SocketChannel socketChannel;
    private final String[] args;

    public GetCommand(SocketChannel socketChannel, String[] args) {
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
     * Get file content and write it into file in the special folder.
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
            long size = is.readLong();
            System.out.println("size = " + size);
            if (size == 0) {
                return "No such file";
            }
            response += String.valueOf(size);
            response += "\nwrote to " + Client.FOLDER_WITH_SAVED_FILES + "\n";
            Path path = Paths.get(
                    Client.FOLDER_WITH_SAVED_FILES.toString(),
                    Paths.get(args[1]).getFileName().toString());
            Files.deleteIfExists(path);
            Files.createFile(path);
            byte[] buffer = new byte[is.available()];
            //no need to check
            is.read(buffer);
            Files.write(path, buffer);
        }
        return response;
    }

}
