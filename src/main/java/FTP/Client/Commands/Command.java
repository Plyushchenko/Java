package FTP.Client.Commands;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Client command on the client side*/
public interface Command {

    /**
     * Run a command
     * @throws IOException Unknown IO problem
     */
    void run() throws IOException;

    /**
     * Build response string from server response
     * @return Server response
     * @throws IOException Unknown IO problem
     */
    @NotNull
    String receiveResponse() throws IOException;

}
