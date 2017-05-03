package FTP.Server;

import FTP.Exceptions.IncorrectArgsException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Server*/
public interface Server {

    int PORT = 12345;
    enum ServerCommand {
        START, STOP, QUIT
    }

    /**
     * Choose and execute command
     * @param args Passed arguments
     * @return Response
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @NotNull
    String execute(String[] args) throws IncorrectArgsException, IOException;

    /**
     * Start listening on server side
     * @return Response
     * @throws IOException Unknown IO problem
     */
    @NotNull
    String start() throws IOException;

    /**
     * Start listening on server side
     * @return Response
     */
    @NotNull
    String stop();

}
