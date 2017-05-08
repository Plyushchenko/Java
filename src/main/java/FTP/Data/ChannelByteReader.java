package FTP.Data;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

//TODO документация
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

    @NotNull
    public byte[] readAll(@NotNull SocketChannel socketChannel) throws IOException {
        int bytesRead = read(socketChannel);
        while (bytesRead != -1) {
            bytesRead = read(socketChannel);
        }
        return getData();
    }
}