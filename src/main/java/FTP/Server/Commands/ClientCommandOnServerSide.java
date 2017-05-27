package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/** Client command on server side*/
public interface ClientCommandOnServerSide {

    /**
     * Run a command
     * @throws IOException Unknown IO problem
     */
    void run() throws IOException;

    @Nullable
    byte[] getResponse();

}
