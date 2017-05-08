package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Stop command*/
public class StopCommand implements Command {

    @Nullable private final StartCommand startCommand;

    public StopCommand(@Nullable StartCommand startCommand) {
        this.startCommand = startCommand;
    }

    /** Unset 'isRunning' flag*/
    @Override
    public void run() {
        if (startCommand == null) {
            return;
        }
        startCommand.unsetIsRunning();
    }

    @NotNull
    @Override
    public String getResponse() {
        return "stopped";
    }

}
