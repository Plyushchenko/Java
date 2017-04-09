package VCS.Commands;

import VCS.Data.FileSystem;
import VCS.Exceptions.IncorrectArgsException;
import VCS.Exceptions.UncommittedChangesException;
import VCS.Exceptions.UnstagedChangesException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class StatusCommand extends Command {


    @NotNull private String status = "";

    public StatusCommand(@NotNull FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public void run() throws IncorrectArgsException, IOException, UnstagedChangesException,
            UncommittedChangesException {
        //дойти до корневой папки, взять индекс, если равны хеши - то added, разные - modified,
        // если нет в индексе, но есть тут - created, наоборот - deleted
    }

    @Override
    protected void checkArgsCorrectness() throws IncorrectArgsException, IOException {

    }

    @NotNull
    public String getStatus() {
        return status;
    }
}
