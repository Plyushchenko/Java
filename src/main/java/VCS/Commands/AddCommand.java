package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.Messages;
import VCS.Objects.Index;
import VCS.Exceptions.IncorrectArgsException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/** Add command */
public class AddCommand extends Command {

    @NotNull private final List<String> filePaths;

    public AddCommand(@NotNull FileSystem fileSystem, @NotNull List<String> filePaths) {
        super(fileSystem);
        this.filePaths = filePaths;
    }

    /**
     * Add.
     * Check that arguments are correct
     * Update index content with passed files
     * @throws IncorrectArgsException Incorrect args passed
     * @throws IOException Unknown IO problem
     */
    @Override
    public void run() throws IncorrectArgsException, IOException {
        checkArgsCorrectness();
        new Index(fileSystem).updateContent(filePaths);
    }

    /**
     * Check that all the files exists
     * @throws IncorrectArgsException Incorrect args passed
     */
    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException {
        for (String s : filePaths) {
            if (fileSystem.notExists(Paths.get(s))) {
                throw new IncorrectArgsException(Messages.FILE_DOESN_T_EXIST + s);
            }
        }
    }
}
