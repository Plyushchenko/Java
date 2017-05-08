package FTP.Client;

import FTP.Exceptions.IncorrectArgsException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Client*/
public interface Client {

    @NotNull InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(
            InetAddress.getLoopbackAddress(), 12345);
    @NotNull Path DEFAULT_FOLDER_WITH_SAVED_FILES =
            Paths.get(System.getProperty("user.home"), "" + ".ftp");

    /**
     * Choose and execute command
     * @param args Passed arguments
     * @return Server response
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @NotNull String execute(@NotNull String[] args) throws IncorrectArgsException, IOException;

    /**
     * Connect to server
     * @throws IOException Unknown IO problem
     */
    void connect() throws IOException;

    /**
     * Disconnect from server
     * @throws IOException Unknown IO problem
     */
    void disconnect() throws IOException;

    /**
     * Execute 'list' command
     * @return Server response
     * @throws IOException Unknown IO problem
     */
    @NotNull String executeList() throws IOException;

    /**
     * Execute 'get' command
     * @return Server response
     * @throws IOException Unknown IO problem
     */
    @NotNull String executeGet() throws IOException;

}
