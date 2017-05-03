package FTP.Server;

import FTP.Exceptions.IncorrectArgsException;
import FTP.Server.Commands.QuitCommand;
import FTP.Server.Commands.StartCommand;
import FTP.Server.Commands.StopCommand;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/** Server implementation*/
public class ServerImpl implements Server {

    private volatile Boolean isRunning = false;
    private StartCommand startCommand;
    private final SocketAddress bindingAddress = new InetSocketAddress(PORT);

    /**
     * Extract principle command and execute it
     * @param args Passed arguments
     * @return Server response
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String execute(String[] args) throws IncorrectArgsException, IOException {
        Parser parser = new Parser(args);
        Server.ServerCommand principleCommand = Server.ServerCommand.valueOf(
                parser.getPrincipleCommandAsString().toUpperCase());
        switch (principleCommand) {
            case START:
                parser.checkStartArgsFormatCorrectness();
                return start();
            case STOP:
                parser.checkStopArgsFormatCorrectness();
                return stop();
            case QUIT:
                parser.checkQuitArgsFormatCorrectness();
                return executeQuit();
            default:
                throw new IncorrectArgsException("no such command");
        }
    }

    /**
     * Run 'start' command
     * @return Response
     * @throws IOException Unknown IO problem
     */
    @NotNull
    @Override
    public String start() throws IOException {
        if (isRunning) {
            return "still running";
        }
        isRunning = true;
        startCommand = new StartCommand(bindingAddress, isRunning);
        startCommand.run();
        return startCommand.getResponse();
    }

    /**
     * Run 'stop' command
     * @return Response
     */
    @NotNull
    @Override
    public String stop() {
        if (!isRunning) {
            return "still not running";
        }
        StopCommand stopCommand = new StopCommand(startCommand);
        stopCommand.run();
        isRunning = false;
        startCommand = null;
        return stopCommand.getResponse();
    }

    private String executeQuit() {
        stop();
        QuitCommand quitCommand = new QuitCommand();
        quitCommand.run();
        return quitCommand.getResponse();
    }

}
