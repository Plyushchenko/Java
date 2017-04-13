package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Objects.Head;
import VCS.Objects.Log;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/** Log command*/
public class LogCommand extends Command {

	@NotNull private String log = "";

    public LogCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger) {
        super(fileSystem, logger);
    }

    /**
     * Log.
     * Read log content
     * @throws IOException Unknown IO problem
     */
    @Override
    public void run() throws IOException {
        logger.info("begin: LogCommand.run()");
        String currentBranchName = new Head(fileSystem).getCurrentBranchName();
        log = new Log(fileSystem, currentBranchName).read();
        logger.info("end: LogCommand.run()");
    }

    @Override
    protected void checkArgsCorrectness() {}

    @NotNull
    public String getLog() {
        return log;
    }

}
