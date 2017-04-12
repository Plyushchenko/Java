package VCS.Commands.BranchCommands;

import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Objects.Head;
import VCS.Exceptions.IncorrectArgsException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Branch list command*/
public class BranchListCommand extends Command {

    @NotNull private String branchListAsString = "";

    public BranchListCommand(@NotNull FileSystem fileSystem) {
        super(fileSystem);
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
        String currentBranchName = new Head(fileSystem).getCurrentBranchName();
        List<String> refs = fileSystem.getFolderContent(fileSystem.getRefsLocation());
        Collections.sort(refs);
        for (String ref : refs) {
            if (ref.equals(currentBranchName)) {
                branchListAsString += " *";
            } else {
                branchListAsString += "  ";
            }
            branchListAsString += ref + "\n";
        }
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException {}

    @NotNull
    public String getBranchList() {
        return branchListAsString;
    }
}
