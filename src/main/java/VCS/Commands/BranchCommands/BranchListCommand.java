package VCS.Commands.BranchCommands;

import VCS.Commands.Command;
import VCS.Data.FileSystem;
import VCS.Objects.HEAD;
import VCS.Exceptions.IncorrectArgsException;

import java.io.IOException;
import java.util.List;

public class BranchListCommand extends Command {

    private String branchListAsString;
    public BranchListCommand(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public void run() throws IncorrectArgsException, IOException {
        String currentBranch = new HEAD(fileSystem).getCurrentBranch();
        List<String> refs = fileSystem.getFolderContent(fileSystem.getRefsLocation());
        branchListAsString = "";
        for (int i = 0; i < refs.size(); i++) {
            if (refs.get(i).equals(currentBranch)) {
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
