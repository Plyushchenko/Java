package FTP.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ChannelByteWriter {

    private final ByteBuffer buffer;

    public ChannelByteWriter(byte[] data) {
        this.buffer = ByteBuffer.wrap(data);
    }

    public int write(ByteChannel byteChannel) throws IOException {
        if (buffer.hasRemaining()) {
            return byteChannel.write(buffer);
        } else {
            return -1;
        }
    }

    void writeAll(ByteChannel byteChannel) throws IOException {
        while (write(byteChannel) != -1);
    }
}
