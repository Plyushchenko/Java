package FTP.Server.Commands;

import org.jetbrains.annotations.NotNull;

/** Stop command*/
public class StopCommand implements Command {

    @NotNull private final StartCommand startCommand;

    public StopCommand(@NotNull StartCommand startCommand) {
        this.startCommand = startCommand;
    }

    /** Unset 'isRunning' flag*/
    @Override
    public void run() {
        startCommand.unsetIsRunning();
    }

    @NotNull
    @Override
    public String getResponse() {
        return "stopped";
    }

}
