package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;

public class MergeCommand extends Command {

    protected MergeCommand(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public void run() {

    }

    @Override
    public void checkArgsCorrectness() throws IncorrectArgsException {

    }

}
