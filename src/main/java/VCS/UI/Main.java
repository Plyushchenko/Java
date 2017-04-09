package VCS.UI;

import VCS.Data.FileSystem;
import VCS.Repo;
import VCS.RepoImpl;

/** User interface */
public class Main {

    /**
     * Execute command by passing args to RepoImpl instance
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Read howto.txt");
                return;
            }
            Repo repo = new RepoImpl(args, FileSystem.DEFAULT_WORKING_DIRECTORY);
            String response = repo.execute();
            System.out.println("response = " + response);
        } catch (Exception e) {
            System.out.println("message = " + e.getMessage());
            e.printStackTrace();
        }
    }

}
