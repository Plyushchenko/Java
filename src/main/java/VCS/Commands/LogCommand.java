package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Objects.HEAD;
import VCS.Objects.Log;

import java.io.IOException;

public class LogCommand extends Command {

	private String log;
    public LogCommand(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public void run() throws IOException {
    	checkArgsCorrectness();
        String currentBranch = new HEAD(fileSystem).getCurrentBranch();
        log = new Log(fileSystem, currentBranch).read();
    }

    @Override
    public void checkArgsCorrectness() {

    }

    public String getLog() {
        return log;
    }
}
