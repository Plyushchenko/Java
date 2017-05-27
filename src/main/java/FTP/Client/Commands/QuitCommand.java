package FTP.Client.Commands;

import org.jetbrains.annotations.NotNull;

/** Quit command */
public class QuitCommand implements Command {

    /** Do nothing*/
    @Override
    public void run() {

    }

    /**
     * Get response
     * @return response
     */
    @NotNull
    @Override
    public String receiveResponse() {
        return "BYE-BYE";
    }

}
