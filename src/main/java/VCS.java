import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class VCS {

    public enum commands{
        init, add, commit, checkout, branch, log
    }

    private static final Path CURRENT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final Path GIT_LOCATION =  Paths.get(CURRENT_DIRECTORY + File.separator + ".git");
    private static final Path INDEX_LOCATION = Paths.get(GIT_LOCATION + File.separator + "index");
    private static final Path REFS_LOCATION = Paths.get(GIT_LOCATION + File.separator + "refs");
    private static final Path HEAD_LOCATION = Paths.get(GIT_LOCATION + File.separator + "HEAD");
    private static final Path OBJECTS_LOCATION = Paths.get(GIT_LOCATION + File.separator + "objects");
    private static final Path LOGS_LOCATION = Paths.get(GIT_LOCATION + File.separator + "logs");

    private static final String MASTER_BRANCH = "master";
    private static boolean isInit = false;


    public static void init() throws IOException {
        isInit = true;
        if (Files.exists(GIT_LOCATION))
            throw new FileAlreadyExistsException("already inited");
        Files.createDirectory(GIT_LOCATION);
        Files.createFile(INDEX_LOCATION);
        Files.createDirectory(OBJECTS_LOCATION);
        Files.createDirectory(REFS_LOCATION);
        Files.createFile(HEAD_LOCATION);
        Files.createDirectory(LOGS_LOCATION);
        String initialCommitHash = commit("initial commit");
        createBranch(MASTER_BRANCH, initialCommitHash);
        switchToBranch(MASTER_BRANCH);
    }

    public static String getHeadCommitHash() throws IOException {
        if (Files.notExists(GIT_LOCATION))
            throw new FileNotFoundException("no git found");

        Path branchLocation = Paths.get(REFS_LOCATION + File.separator + getCurrentBranch());
        return new String(Files.readAllBytes(branchLocation));
    }

    public static String getCurrentBranch() throws IOException {
        if (Files.notExists(GIT_LOCATION))
            throw new FileNotFoundException("no git found");

        return new String(Files.readAllBytes(HEAD_LOCATION));
    }

    public static void switchToCommit(String commitHash) throws IOException {
        createBranch(commitHash, commitHash);
        switchToBranch(commitHash);
    }

    public static void switchToBranch(String branchName) throws IOException {
        if (Files.notExists(GIT_LOCATION))
            throw new FileNotFoundException("no git found");
        Path branchLocation = Paths.get(REFS_LOCATION + File.separator + branchName);
        if (Files.notExists(branchLocation)){
            throw new FileNotFoundException("no branch with this name");
        }
        Files.write(HEAD_LOCATION, branchName.getBytes());
        if (!isInit) {
            restoreFiles(getHeadCommitHash());
        }
    }

    private static void restoreFiles(String commitHash) throws IOException {
        Path treeLocation = Paths.get(OBJECTS_LOCATION + File.separator + commitHash);
        List<String> filePathsAndHashes = Files.readAllLines(
                Paths.get(OBJECTS_LOCATION + File.separator + Files.lines(treeLocation).findFirst().get()));
        filePathsAndHashes.forEach(System.out::println);
        for (int i = 0; i < filePathsAndHashes.size(); i += 2) {
            Path fileLocation = Paths.get(filePathsAndHashes.get(i));
            String fileHash = filePathsAndHashes.get(i + 1);
            byte[] fileContent = Files.readAllBytes(Paths.get(OBJECTS_LOCATION + File.separator + fileHash));
            Files.createDirectories(fileLocation.getParent());
            Files.write(fileLocation, fileContent);
        }
    }

    public static void createBranch(String branchName, String commitHash) throws IOException {
        if (Files.notExists(GIT_LOCATION))
            throw new FileNotFoundException("no git found");

        Path branchLocation = Paths.get(REFS_LOCATION + File.separator + branchName);
        if (Files.exists(branchLocation)){
            throw new FileAlreadyExistsException("already have branch with this name");
        }
        Path logLocation = Paths.get(LOGS_LOCATION + File.separator + branchName);
        Files.write(logLocation, (commitHash + " branch created\n").getBytes());

        Files.write(branchLocation, commitHash.getBytes());
    }

    public static byte[] getLog() throws IOException {
        if (Files.notExists(GIT_LOCATION))
            throw new FileNotFoundException("no git found");
        Path logLocation = Paths.get(LOGS_LOCATION + File.separator + getCurrentBranch());
        return Files.readAllBytes(logLocation);
    }

    public static void add(List<Path> filesToAdd) throws IOException {
        if (Files.notExists(GIT_LOCATION))
            throw new FileNotFoundException("no git found");
        Set<Path> indexedFiles = new HashSet<>(
                Files.readAllLines(INDEX_LOCATION).stream().map(s -> Paths.get(s)).collect(Collectors.toList()));
        List<Path> addedFiles = new ArrayList<>();

        for (Path fileToAdd : filesToAdd){
            fileToAdd = Paths.get(CURRENT_DIRECTORY + File.separator + fileToAdd);
            if (Files.notExists(fileToAdd)) {
                throw new FileNotFoundException("not exists, try again\n" + fileToAdd);
            }
            if (!indexedFiles.contains(fileToAdd)){
                indexedFiles.add(fileToAdd);
                addedFiles.add(fileToAdd);
            }
        }

        for (Path addedFile : addedFiles){
            Files.write(INDEX_LOCATION, addedFile.toString().getBytes(), StandardOpenOption.APPEND);
            Files.write(INDEX_LOCATION, newLine(), StandardOpenOption.APPEND);
        }
    }

    private static byte[] newLine() {
        return System.getProperty("line.separator").getBytes();
    }

    public static String commit(String message) throws IOException {
        if (Files.notExists(GIT_LOCATION))
            throw new FileNotFoundException("no git found");

        List<Path> filesToCommit = Files.readAllLines(INDEX_LOCATION).stream()
                .map(s -> Paths.get(s)).collect(Collectors.toList());
        List<String> hashesOfFilesToCommit = new ArrayList<>();
        for (Path fileToCommit : filesToCommit){
            Blob blob = new Blob(Files.readAllBytes(fileToCommit));
            blob.addObject(OBJECTS_LOCATION);
            hashesOfFilesToCommit.add(blob.getHash());
        }

        Tree tree = new Tree(filesToCommit, hashesOfFilesToCommit);
        tree.addObject(OBJECTS_LOCATION);
        Commit commit = new Commit(tree.getHash().getBytes(), message);
        commit.addObject(OBJECTS_LOCATION);

        if (!isInit) {
            Path branchLocation = Paths.get(REFS_LOCATION + File.separator + getCurrentBranch());
            Files.write(branchLocation, commit.getHash().getBytes());
            Path logLocation = Paths.get(LOGS_LOCATION + File.separator + getCurrentBranch());
            Files.write(logLocation,
                    (commit.getHash() + " " + commit.getCommitMessage() + " " + commit.COMMIT_AUTHOR + " " + commit.COMMIT_DATE + "\n").getBytes(),
                    StandardOpenOption.APPEND);
        }

        return commit.getHash();
    }

}
