package VCS.Commands.BranchCommands;

import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Objects.Head;
import VCS.Exceptions.IncorrectArgsException;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/** Branch list command*/
public class BranchListCommand extends Command {

    @NotNull private StringBuilder branchListAsString = new StringBuilder();

    public BranchListCommand(@NotNull FileSystem fileSystem, @NotNull Logger logger) {
        super(fileSystem, logger);
    }

    /**
     * Create branch list
     * <pre>
     * Format:
     * branch1
     * branch2
     * *currentBranchName
     * branch3
     * ...
     * </pre>
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    public void run() throws IncorrectArgsException, IOException {
        logger.info("begin: BranchListCommand.run()");
        String currentBranchName = new Head(fileSystem).getCurrentBranchName();
        List<String> refs = fileSystem.getFolderContentAsListOfString(fileSystem.getRefsLocation());
        Collections.sort(refs);
        for (String ref : refs) {
            if (ref.equals(currentBranchName)) {
                branchListAsString.append(" *");
            } else {
                branchListAsString.append("  ");
            }
            branchListAsString.append(ref).append("\n");
        }
        logger.info("end: BranchListCommand.run()");
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException {}

    @NotNull
    public String getBranchList() {
        return branchListAsString.toString();
    }
}