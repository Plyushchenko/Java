package FTP.Data;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/** Helper class to write to channel*/
public class ChannelByteWriter {

    @NotNull private final ByteBuffer buffer;

    public ChannelByteWriter(@NotNull byte[] data) {
        this.buffer = ByteBuffer.wrap(data);
    }

    /**
     * Write a part of data to channel
     * @param byteChannel Channel to write to
     * @return Number of bytes written (-1 if nothing to write)
     * @throws IOException Writing problem
     */
    public int write(@NotNull ByteChannel byteChannel) throws IOException {
        if (buffer.hasRemaining()) {
            return byteChannel.write(buffer);
        } else {
            return -1;
        }
    }

    void writeAll(@NotNull ByteChannel byteChannel) throws IOException {
        while (write(byteChannel) != -1) {}
    }
}
