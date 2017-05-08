package FTP.Server.Commands;

import FTP.ClientCommand;
import FTP.Data.ChannelByteReader;
import FTP.Data.ChannelByteWriter;
import FTP.Exceptions.IncorrectArgsException;
import FTP.Server.ClientParserOnServerSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

/** Start command*/
public class StartCommand implements Command {

    private static final long SELECT_TIMEOUT = 1500;
    @NotNull private SocketAddress socketAddress;
    @NotNull private volatile Boolean isRunning;

    public StartCommand(@NotNull SocketAddress socketAddress, @NotNull Boolean isRunning) {
        this.socketAddress = socketAddress;
        this.isRunning = isRunning;
    }

    /**Set up listening and handle connections */
    @Override
    public void run() {
        Thread thread = new Thread(() -> {
            try (Selector selector = Selector.open();
                 ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
                serverChannel.bind(socketAddress);
                serverChannel.configureBlocking(false);
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                while (isRunning) {
                    selector.select(SELECT_TIMEOUT);
                    Iterator<SelectionKey> selectionKeyIterator =
                            selector.selectedKeys().iterator();
                    while (selectionKeyIterator.hasNext()) {
                        SelectionKey selectionKey = selectionKeyIterator.next();
                        if (selectionKey.isAcceptable()) {
                            accept(selectionKey);
                        } else if (selectionKey.isReadable()) {
                            byte[] data = read(selectionKey);
                            if (data == null) {
                                continue;
                            }
                            redirectToWriting(selectionKey, runClientCommand(data));
                        } else if (selectionKey.isWritable()) {
                            write(selectionKey);
                        }
                        selectionKeyIterator.remove();
                    }
                }
            } catch (IOException | IncorrectArgsException e) {
                //TODO Это очень странно
                //e.printStackTrace();
            }
        });
        thread.start();
    }

    private void redirectToWriting(@NotNull SelectionKey selectionKey, @NotNull byte[] data)
            throws IOException {
        try {
            selectionKey.channel().register(selectionKey.selector(), SelectionKey.OP_WRITE,
                    new ChannelByteWriter(data));
        } catch (ClosedChannelException e) {
            close(selectionKey);
        }
    }

    @NotNull
    private byte[] runClientCommand(@NotNull byte[] data) throws IOException,
            IncorrectArgsException {
        ClientParserOnServerSide parser = new ClientParserOnServerSide(data);
        ClientCommand principleCommand = ClientCommand.valueOf(
                parser.getPrincipleCommandAsString().toUpperCase());
        switch (principleCommand) {
            case GET:
                return get(parser.extractGetCommandArgs());
            case LIST:
                return list(parser.extractListCommandArgs());
            default:
                throw new IncorrectArgsException("no such command");
        }
    }

    @NotNull
    private byte[] list(@NotNull String path) throws IOException {
        ListCommand listCommand = new ListCommand(path);
        listCommand.run();
        return listCommand.getResponse();
    }

    @NotNull
    private byte[] get(@NotNull String path) throws IOException {
        GetCommand getCommand = new GetCommand(path);
        getCommand.run();
        return getCommand.getResponse();
    }

    private void accept(@NotNull SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
        if (socketChannel == null) {
            return;
        }
        socketChannel.configureBlocking(false);
        socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ,
                new ChannelByteReader());
    }

    @Nullable
    private byte[] read(@NotNull SelectionKey selectionKey) throws IOException {
        final ChannelByteReader reader = (ChannelByteReader) selectionKey.attachment();
        byte[] data = null;
        try {
            int bytesRead = reader.read((ByteChannel) selectionKey.channel());
            if (bytesRead == -1) {
                data = reader.getData();
                selectionKey.interestOps(0);
            }
        } catch (IOException e) {
            close(selectionKey);
        }
        return data;
    }

    private void close(@NotNull SelectionKey selectionKey) throws IOException {
        selectionKey.cancel();
        selectionKey.channel().close();
    }

    private void write(@NotNull SelectionKey selectionKey) throws IOException {
        final ChannelByteWriter writer = (ChannelByteWriter) selectionKey.attachment();
        try {
            int bytesWritten = writer.write((ByteChannel) selectionKey.channel());
            if (bytesWritten == -1) {
                close(selectionKey);
            }
        } catch (IOException e) {
            close(selectionKey);
        }
    }

    void unsetIsRunning() {
        isRunning = false;
    }

    @NotNull
    @Override
    public String getResponse() {
        return "started";
    }

}
