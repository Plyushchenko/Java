import vcs.VCS;
import vcs.vcsexceptions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("you should know how to use this supa hot vcs");
                return;
            }
            if (args[0].equals(VCS.commands.init.toString())) {
                if (args.length != 1) {
                    System.out.println("format: git init");
                    return;
                }
                VCS.init();
                System.out.println("git inited in " + System.getProperty("user.dir"));
            } else if (args[0].equals(VCS.commands.add.toString())) {
                if (args.length == 1){
                    System.out.println("format: git add file1 file2 ....");
                    return;
                }
                List<Path> filesToAdd = new ArrayList<>();
                for (int i = 1; i < args.length; i++) {
                    filesToAdd.add(Paths.get(args[i]));
                }
                VCS.add(filesToAdd);
            } else if (args[0].equals(VCS.commands.commit.toString())) {
                if (args.length != 2) {
                    System.out.println("format: git commit 'message'");
                    return;
                }
                System.out.println("committed " + VCS.commit(args[1]));
            } else if (args[0].equals(VCS.commands.checkout.toString())) {
                if (args[1].equals("-b")) {
                    if (args.length != 3) {
                        System.out.println("format: git checkout -b 'branchName'");
                        return;
                    }
                    VCS.createBranch(args[2]);
                    VCS.checkout(args[2]);
                } else {
                    if (args.length != 2){
                        System.out.println("format: git checkout 'branchName'");
                        return;
                    }
                    VCS.checkout(args[1]);
                }
            } else if (args[0].equals(VCS.commands.branch.toString())) {
                if (args.length == 1) {
                    VCS.buildBranchNamesList().forEach(System.out::println);
                } else {
                    if (args[1].equals("-d")){
                        if (args.length != 3){
                            System.out.println("format: git branch -d 'branchName'");
                            return;
                        }
                        VCS.removeBranch(args[2]);
                    } else {
                        if (args.length != 2) {
                            System.out.println("format: git branch 'branchName'");
                        }
                        VCS.createBranch(args[1]);
                    }
                }
            } else if (args[0].equals(VCS.commands.log.toString())) {
                System.out.println(new String(VCS.getCurrentBranchLog()));
            } else if (args[0].equals(VCS.commands.merge.toString())) {
                if (args.length != 2) {
                    System.out.println("format: git merge 'branchToMergeName'");
                    return;
                }
                System.out.println("WARNING: all added but not commited yet changes will be committed with merge; versions from "
                        + args[1] + " will suppress versions from current branch");
                VCS.merge(args[1]);
            }
            else {
                System.out.println("unknown argument");
            }
        } catch (GitAlreadyInitedException e) {
            System.out.println("you already have .git folder in this directory");
        } catch (GitInitException e) {
            System.out.println("unable to init git properly; remove .git folder and try again");
        } catch (HeadWriteException e) {
            System.out.println("unable to write to HEAD file");
        } catch (BranchWriteException e) {
            System.out.println("unable to write information about branch");
        } catch (BranchReadException e) {
            System.out.println("unable to read information about branch");
        } catch (BranchAlreadyCreatedException e) {
            System.out.println("branch with this name already exists");
        } catch (TreeReadException e) {
            System.out.println("unable to read information about tree");
        } catch (NoGitFoundException e) {
            System.out.println("no .git folder in this directory; you may use 'git init'");
        } catch (ContentWriteException e) {
            System.out.println("unable to write information about file content");
        } catch (IndexReadException e) {
            System.out.println("unable to read index file");
        } catch (ContentReadException e) {
            System.out.println("unable to read information about file content");
        } catch (FileToAddNotExistsException e) {
            System.out.println("some of files don't exist; please check arguments");
        } catch (HeadReadException e) {
            System.out.println("unable to read HEAD file");
        } catch (LogWriteException e) {
            System.out.println("unable to write to log");
        } catch (DirectioryCreateException e) {
            System.out.println("unable to create directory");
        } catch (LogReadException e) {
            System.out.println("unable to read log file");
        } catch (NoBranchFoundException e) {
            System.out.println("no branch with this name; please check arguments");
        } catch (AddException e) {
            System.out.println("unable to add");
        } catch (NoIndexFoundException e) {
            System.out.println("index file not found");
        } catch (NothingChangedSinceLastCommitException e) {
            System.out.println("nothing changed since last commit; add some changes using git add");
        } catch (NothingChangedSinceLastAddException e) {
            System.out.println("nothing changed since last add; add some changes using git add");
        } catch (MasterBranchDeleteException e) {
            System.out.println("you can't delete master branch");
        }
    }



}
