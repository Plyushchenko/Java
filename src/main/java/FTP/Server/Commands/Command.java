package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Server command on server side*/
public interface Command {

    /**
     * Run a command
     * @throws IOException Unknown IO problem
     */
    void run() throws IOException;

    @NotNull
    String getResponse();

}
