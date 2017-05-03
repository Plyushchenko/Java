package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;

/** Quit command*/
public class QuitCommand implements Command {

    /** Do nothing*/
    @Override
    public void run() {

    }

    @NotNull
    public String getResponse() {
        return "BYE-BYE";
    }
}
