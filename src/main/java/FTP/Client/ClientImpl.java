package FTP.Client;

import FTP.Client.Commands.GetCommand;
import FTP.Client.Commands.ListCommand;
import FTP.Client.Commands.QuitCommand;
import FTP.ClientCommand;
import FTP.Exceptions.IncorrectArgsException;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;

/** Client implementation*/
public class ClientImpl implements Client {

    private SocketChannel socketChannel;
    private String[] args;

    public ClientImpl() throws IOException {
        System.out.println("!!!!");
        if (Files.exists(Client.FOLDER_WITH_SAVED_FILES)) {
            return;
        }
        Files.createDirectories(Client.FOLDER_WITH_SAVED_FILES);
    }

    /**
     * Extract principle command and execute it
     * @param args Passed arguments
     * @return Server response
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    public String execute(String[] args) throws IncorrectArgsException, IOException {
        Parser parser = new Parser(args);
        try {
            ClientCommand principleCommand = ClientCommand.valueOf(
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
                    System.out.println("???");
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
    //TODO Что-то не то делает, когда не получается подключиться
    @Override
    public void connect() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        try {
            socketChannel.connect(SERVER_ADDRESS);
            if (selector.select(1500) == 0 || !socketChannel.finishConnect()) {
                throw new Exception();
            }
        } catch (Exception e) {
            selector.close();
            socketChannel.close();
            throw new IOException("Server is off or is stopped");
        }
    }

    /**
     * Disconnect from server
     * @throws IOException Unknown IO problem
     */
    @Override
    public void disconnect() throws IOException {
        socketChannel.close();
    }

    /**
     * Connect to server; run command; disconnect from server
     * @return Server response
     * @throws IOException Unknown IO problem
     */
    @Override
    public String executeGet() throws IOException {
        connect();
        GetCommand getCommand = new GetCommand(socketChannel, args);
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
    @Override
    public String executeList() throws IOException {
        connect();
        ListCommand listCommand = new ListCommand(socketChannel, args);
        listCommand.run();
        String response = listCommand.receiveResponse();
        disconnect();
        System.out.println("response = " + response);
        return response;
    }

    /**
     * Run command
     * @return Command response
     * @throws IOException Unknown IO problem
     */
    private String executeQuit() {
        QuitCommand quitCommand = new QuitCommand();
        quitCommand.run();
        return quitCommand.receiveResponse();
    }

}
