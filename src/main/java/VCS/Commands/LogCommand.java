package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Objects.Head;
import VCS.Objects.Log;

import java.io.IOException;

/** Log command*/
public class LogCommand extends Command {

	private String log;
    public LogCommand(FileSystem fileSystem) {
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
    public void checkArgsCorrectness() {}

    public String getLog() {
        return log;
    }
}
