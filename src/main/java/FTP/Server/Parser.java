package FTP.Server;

import FTP.Exceptions.IncorrectArgsException;
import org.jetbrains.annotations.NotNull;

class Parser {

    @NotNull private final String[] args;
    Parser(@NotNull String[] args) {
        this.args = args;
    }

    @NotNull
    String getPrincipleCommandAsString() {
        switch (args[0]) {
            case "start":
                return "start";
            case "stop":
                return "stop";
            case "quit":
                return "quit";
            default:
                return "unknown server command";
        }
    }

    void checkStartArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 1) {
            throw new IncorrectArgsException("format: start");
        }
    }

    void checkStopArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 1) {
            throw new IncorrectArgsException("format: stop");
        }
    }

    void checkQuitArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 1) {
            throw new IncorrectArgsException("format: quit");
        }
    }

}
