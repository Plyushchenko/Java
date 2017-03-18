
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("you should know how to use this supa hot vcs");
        }

        if (args[0].equals(VCS.commands.init.toString())){
            try {
                VCS.init();
            } catch (IOException e) {
                System.out.println("already inited");
            }
        } else if (args[0].equals(VCS.commands.add.toString())) {
            List<Path> filesToAdd = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                filesToAdd.add(Paths.get(args[i]));
            }
            try {
                VCS.add(filesToAdd);
            } catch (IOException e) {
                System.out.println("add problem");
                e.printStackTrace();
            }
        } else if (args[0].equals(VCS.commands.commit.toString())){
            if (args.length != 2){
                System.out.println("commit + message");
                return;
            }
            try {
                System.out.println("committed " + VCS.commit(args[1]));
            } catch (IOException e) {
                System.out.println("some problems");
            }
        } else if (args[0].equals(VCS.commands.checkout.toString())){
            if (args[1].equals("-b")){
                try {
                    VCS.createBranch(args[2], VCS.getHeadCommitHash());
                    VCS.switchToBranch(args[2]);
                } catch (IOException e) {
                    System.out.println("branch creating problem");
                    e.printStackTrace();
                }
            } else if (isHash(args[1].toLowerCase())){
                try {
                    VCS.switchToCommit(args[1]);
                } catch (IOException e) {
                    System.out.println("switch to another commit problem");
                    e.printStackTrace();
                }
            } else {
                try {
                    VCS.switchToBranch(args[1]);
                } catch (IOException e) {
                    System.out.println("switch to another branch problem");
                    e.printStackTrace();
                }
            }
        } else if (args[0].equals(VCS.commands.branch.toString())){
            if (args.length == 1){
                try {
                    System.out.println(VCS.getCurrentBranch());
                } catch (IOException e) {
                    System.out.println("current branch getting problem");
                    e.printStackTrace();
                }
            } else {
                try {
                    VCS.createBranch(args[1], VCS.getHeadCommitHash());
                } catch (IOException e) {
                    System.out.println("branch creating problem");
                    e.printStackTrace();
                }
            }
        } else if (args[0].equals(VCS.commands.log.toString())){
            try {
                System.out.println(new String(VCS.getLog()));
            } catch (IOException e) {
                System.out.println("log problem");
                e.printStackTrace();
            }
        }

    }

    private static boolean isHash(String s) {
        System.out.println(s + " " + s.length());
        if (s.length() != VCSObject.HASH_LENGTH){
            return false;
        }
        for (int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if (!('0' <= c && c <= '9' || 'a' <= c && c <= 'f')){
                System.out.println(i + " " + c);
                return false;
            }
        }
        return true;
    }

}
