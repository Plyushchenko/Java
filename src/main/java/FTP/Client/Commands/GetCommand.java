package FTP.Client.Commands;

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

    @NotNull private final SocketChannel socketChannel;
    @NotNull private final String[] args;
    @NotNull private final Path folderWithSavedFiles;

    public GetCommand(@NotNull SocketChannel socketChannel, @NotNull String[] args,
                      @NotNull Path folderWithSavedFiles) {
        this.socketChannel = socketChannel;
        this.args = args;
        this.folderWithSavedFiles = folderWithSavedFiles;
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
            if (size == 0) {
                return "No such file";
            }
            response += String.valueOf(size);
            Path path = Paths.get(
                    folderWithSavedFiles.toString(),
                    Paths.get(args[1]).getFileName().toString());
            response += "\nWrote to " + path;
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
