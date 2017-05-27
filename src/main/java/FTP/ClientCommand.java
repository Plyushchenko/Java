package FTP;

import java.util.HashMap;
import java.util.Map;

/** Client commands*/
public class ClientCommand {

    public enum ClientCommands {
        GET, LIST, QUIT
    }

    public static final Map<String, Integer> CLIENT_COMMAND_CODES;
    static
    {
        CLIENT_COMMAND_CODES = new HashMap<>();
        CLIENT_COMMAND_CODES.put("list", 1);
        CLIENT_COMMAND_CODES.put("get", 2);
    }

}
