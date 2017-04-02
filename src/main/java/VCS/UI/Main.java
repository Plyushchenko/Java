package VCS.UI;

import VCS.Data.FileSystem;
import VCS.Repo;
import VCS.RepoImpl;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Read howto.txt");
                return;
            }
            Repo repo = new RepoImpl(args, FileSystem.DEFAULT_WORKING_DIRECTORY);
            repo.execute();
            System.out.println("response = " + repo.getResponse());
        } catch (Exception e) {
            System.out.println("message = " + e.getMessage());
            e.printStackTrace();
        }
    }
}
