package FTP.Client;

import FTP.Client.Commands.GetCommand;
import FTP.Client.Commands.ListCommand;
import FTP.Client.Commands.QuitCommand;
import FTP.ClientCommand;
import FTP.Exceptions.IncorrectArgsException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/** Client implementation*/
public class ClientImpl implements Client {

    private SocketChannel socketChannel;
    private Selector selector;
    private String[] args;
    @NotNull private final Path folderWithSavedFiles;

    public ClientImpl() throws IOException {
        this(DEFAULT_FOLDER_WITH_SAVED_FILES);
    }

    public ClientImpl(@NotNull Path folderWithSavedFiles) throws IOException {
        this.folderWithSavedFiles = folderWithSavedFiles;
        if (Files.exists(folderWithSavedFiles)) {
            return;
        }
        Files.createDirectories(folderWithSavedFiles);
    }

    /**
     * Extract principle command and execute it
     * @param args Passed arguments
     * @return Server response
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String execute(@NotNull String[] args) throws IncorrectArgsException, IOException {
        Parser parser = new Parser(args);
        try {
            ClientCommand.ClientCommands principleCommand = ClientCommand.ClientCommands.valueOf(
                    parser.getPrincipleCommandAsString().toUpperCase());
            this.args = parser.getArgs();
            switch (principleCommand) {
                case GET:
                    parser.checkGetArgsFormatCorrectness();
                    return executeGet();
                case LIST:
                    parser.checkListArgsFormatCorrectness();
                    return executeList();
                case QUIT:
                    parser.checkQuitArgsFormatCorrectness();
                    return executeQuit();
                default:
                    throw new IncorrectArgsException("no such command");
            }
        } catch (IllegalArgumentException e) {
            throw new IncorrectArgsException("no such command");
        }

    }

    /**
     * Connect to server
     * @throws IOException Unable to connect to server
     */
    @Override
    public void connect() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        try {
            socketChannel.connect(SERVER_ADDRESS);
            if (selector.select(1500) == 0 || !socketChannel.finishConnect()) {
                throw new SocketTimeoutException("Unable to connect to server");
            }
        } catch (Exception e) {
            disconnect();
            throw new IOException("Server is off or is stopped");
        }
    }

    /**
     * Disconnect from server
     * @throws IOException Unknown IO problem
     */
    @Override
    public void disconnect() throws IOException {
        selector.close();
        socketChannel.close();
    }

    /**
     * Connect to server; run command; disconnect from server
     * @return Server response
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String executeGet() throws IOException {
        connect();
        GetCommand getCommand = new GetCommand(socketChannel, args, folderWithSavedFiles);
        getCommand.run();
        String response = getCommand.receiveResponse();
        disconnect();
        return response;
    }

    /**
     * Connect to server; run command; disconnect from server
     * @return Server response
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String executeList() throws IOException {
        connect();
        ListCommand listCommand = new ListCommand(socketChannel, args);
        listCommand.run();
        String response = listCommand.receiveResponse();
        disconnect();
        return response;
    }

    /**
     * Run command
     * @return Command response
     */
    private String executeQuit() {
        QuitCommand quitCommand = new QuitCommand();
        quitCommand.run();
        return quitCommand.receiveResponse();
    }

}
