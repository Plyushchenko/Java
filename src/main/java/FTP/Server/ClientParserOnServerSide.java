package FTP.Server;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/** Client parser on server side*/
public class ClientParserOnServerSide {

    @NotNull private final String[] args;

    public ClientParserOnServerSide(@NotNull byte[] data) throws IOException {
        args = new String[2];
        try (DataInputStream is = new DataInputStream(new ByteArrayInputStream(data))) {
            args[0] = String.valueOf(is.readInt());
            args[1] = is.readUTF();
        }
    }

    /**
     * Extract principle command as string
     * @return Principle command as string
     */
    @NotNull
    public String getPrincipleCommandAsString() {
        switch (args[0]) {
            case "1":
                return "list";
            case "2":
                return "get";
            default:
                return "unknown command";
        }
    }

    /**
     * Extract path for 'list' command as string
     * @return Path as string
     */
    @NotNull
    public String extractListCommandArgs() {
        return args[1];
    }

    /**
     * Extract path for 'get' command as string
     * @return Path as string
     */
    @NotNull
    public String extractGetCommandArgs() {
        return args[1];
    }

}
