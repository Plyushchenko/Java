package FTP.Client;

import FTP.Exceptions.IncorrectArgsException;

class Parser {

    private final String[] args;

    Parser(String[] args) {
        this.args = args;
    }

    String getPrincipleCommandAsString() {
        switch (args[0]) {
            case "get":
                args[0] = "2";
                return "get";
            case "list":
                args[0] = "1";
                return "list";
            case "quit":
                return "quit";
            default:
                return "unknown command";
        }
    }

    void checkGetArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 2) {
            throw new IncorrectArgsException("format: 2 path");
        }
    }

    void checkListArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 2) {
            throw new IncorrectArgsException("format: 1 path");
        }
    }

    void checkQuitArgsFormatCorrectness() throws IncorrectArgsException {
        if (args.length != 1) {
            throw new IncorrectArgsException("format: quit");
        }
    }

    public String[] getArgs() {
        return args;
    }
}
