package vcs;

import vcs.vcsexceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class VCSFileUtils {

    static final Path CURRENT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    static final Path GIT_LOCATION = Paths.get("./mygit");
    static final Path INDEX_LOCATION = Paths.get(GIT_LOCATION + File.separator + "index");
    static final Path REFS_LOCATION = Paths.get(GIT_LOCATION + File.separator + "refs");
    static final Path HEAD_LOCATION = Paths.get(GIT_LOCATION + File.separator + "HEAD");
    static final Path OBJECTS_LOCATION = Paths.get(GIT_LOCATION + File.separator + "objects");
    static final Path LOGS_LOCATION = Paths.get(GIT_LOCATION + File.separator + "logs");

    static byte[] newLineAsBytes() {
        return System.getProperty("line.separator").getBytes();
    }

    static void createEmptyFile(Path path) throws IOException {
        Files.deleteIfExists(path);
        Files.createDirectories(path.getParent());
        Files.createFile(path);
    }

    static void checkExistenceOfAllFiles(List<Path> paths) throws FileToAddNotExistsException {
        for (Path path : paths) {
            if (!path.isAbsolute()) {
                path = Paths.get(CURRENT_DIRECTORY + File.separator + path);
            }
            if (Files.notExists(path)) {
                throw new FileToAddNotExistsException();
            }
        }
    }

    static void restoreFiles(String commitHash) throws
            TreeReadException, ContentReadException, ContentWriteException, DirectoryCreateException {
        Path treeLocation = Paths.get(OBJECTS_LOCATION + File.separator + commitHash);
        List<String> filePathsAndHashes;
        try {
            filePathsAndHashes = Files.readAllLines(
                    Paths.get(OBJECTS_LOCATION + File.separator + new String(Files.readAllBytes(treeLocation))));
        } catch (IOException e) {
            throw new TreeReadException();
        }
        for (int i = 0; i < filePathsAndHashes.size(); i += 2) {
            Path fileLocation = Paths.get(filePathsAndHashes.get(i));
            String fileHash = filePathsAndHashes.get(i + 1);
            byte[] fileContent;
            try {
                fileContent = Files.readAllBytes(Paths.get(OBJECTS_LOCATION + File.separator + fileHash));
            } catch (IOException e) {
                throw new ContentReadException();
            }
            try {
                Files.createDirectories(fileLocation.getParent());
            } catch (IOException e) {
                throw new DirectoryCreateException();
            }
            try {
                Files.write(fileLocation, fileContent);
            } catch (IOException e) {
                throw new ContentWriteException();
            }
        }
    }

    static void createGitDirectoriesAndFiles() throws IOException {
        Files.createDirectory(GIT_LOCATION);
        Files.createFile(INDEX_LOCATION);
        Files.createDirectory(OBJECTS_LOCATION);
        Files.createDirectory(REFS_LOCATION);
        Files.createFile(HEAD_LOCATION);
        Files.createDirectory(LOGS_LOCATION);
    }

}
