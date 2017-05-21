package FTP.Data;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/** Helper class for reading from channels*/
public class ChannelByteReader {

    private final static int BUFFER_SIZE = 4096;
    @NotNull private final ByteBuffer buffer;
    @NotNull private byte[] data;
    private int position;

    public ChannelByteReader() {
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.data = new byte[BUFFER_SIZE];
        this.position = 0;
    }

    /**
     * Read a part of data from channel
     * @param channel Channel to read from
     * @return Number of bytes read (-1 for EOF)
     * @throws IOException Reading problem
     */
    public int read(@NotNull ByteChannel channel) throws IOException {
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) {
            return -1;
        }
        while (position + bytesRead > data.length) {
            byte[] newData = new byte[data.length * 2];
            System.arraycopy(data, 0, newData, 0, position);
            data = newData;
        }
        buffer.flip();
        buffer.get(data, position, bytesRead);
        position += bytesRead;
        buffer.clear();
        return bytesRead;
    }

    @NotNull
    public byte[] getData() {
        return Arrays.copyOf(data, position);
    }

    /**
     * Read all the data from channel
     * @param socketChannel Channel to read from
     * @return Bytes read
     * @throws IOException Reading problem
     */
    @NotNull
    public byte[] readAll(@NotNull SocketChannel socketChannel) throws IOException {
        int bytesRead = read(socketChannel);
        while (bytesRead != -1) {
            bytesRead = read(socketChannel);
        }
        return getData();
    }
}