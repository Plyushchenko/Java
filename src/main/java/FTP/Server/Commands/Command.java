package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Server command on server side*/
public interface Command {

    void run() throws IOException;

    @NotNull
    String getResponse();

}
