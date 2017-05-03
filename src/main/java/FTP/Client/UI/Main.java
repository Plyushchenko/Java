package FTP.Client.UI;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)) {
            try {
                FTP.Client.Client client = new FTP.Client.ClientImpl();
                while (true) {
                    try {
                        String[] clientArgs = scanner.nextLine().trim().split("\\s+");
                        String response = client.execute(clientArgs);
                        System.out.println("response = " + response);
                        if (response.equals("BYE-BYE")) {
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("message = " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("message = " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}