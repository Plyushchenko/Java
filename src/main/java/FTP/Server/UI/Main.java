package FTP.Server.UI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)) {
            FTP.Server.Server server = new FTP.Server.ServerImpl();
            while (true) {
                try {
                    String[] serverArgs = scanner.nextLine().trim().split("\\s+");
                    String response = server.execute(serverArgs);
                    //TODO что-нибудь пользователю рассказать интересное
                    System.out.println("response = " + response);
                    if (response.equals("BYE-BYE")) {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("message = " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}