package VCS.Commands.BranchCommands;

import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Objects.Head;
import VCS.Exceptions.IncorrectArgsException;

import java.io.IOException;
import java.util.List;

/** Branch list command*/
public class BranchListCommand extends Command {

    private String branchListAsString;

    public BranchListCommand(FileSystem fileSystem) {
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
        branchListAsString = "";
        for (int i = 0; i < refs.size(); i++) {
            if (refs.get(i).equals(currentBranchName)) {
                branchListAsString += " *";
            } else {
                branchListAsString += "  ";
            }
            branchListAsString += refs.get(i);
            if (i + 1 != refs.size()) {
                branchListAsString += "\n";
            }
        }
    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException {}

    public String getBranchList() {
        return branchListAsString;
    }
}
