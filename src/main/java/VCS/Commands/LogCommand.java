package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Objects.Head;
import VCS.Objects.Log;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Log command*/
public class LogCommand extends Command {

	@NotNull private String log = "";

    public LogCommand(@NotNull FileSystem fileSystem) {
        super(fileSystem);
    }

    /**
     * Log.
     * Read log content
     * @throws IOException Unknown IO problem
     */
    @Override
    public void run() throws IOException {
        String currentBranchName = new Head(fileSystem).getCurrentBranchName();
        log = new Log(fileSystem, currentBranchName).read();
    }

    @Override
    protected void checkArgsCorrectness() {}

    @NotNull
    public String getLog() {
        return log;
    }

}
