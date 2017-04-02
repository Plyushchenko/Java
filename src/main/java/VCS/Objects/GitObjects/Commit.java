package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;

import java.util.Date;

/**
 * Commit
 */
public class Commit extends GitObject {

    private String commitMessage;
    private final String COMMIT_AUTHOR = System.getProperty("user.name");
    private final Date COMMIT_DATE = new Date(System.currentTimeMillis());

    /**
     * Build Commit
     * @param fileSystem File system
     * @param content Commit content
     * @param message Commit message
     */
    public Commit(FileSystem fileSystem, byte[] content, String message) {
        super(fileSystem, buildHash(content), "commit", content.length, content);
        commitMessage = message;
    }

    private String getCommitMessage() {
        return commitMessage;
    }

    private String getCommitAuthor() {
        return COMMIT_AUTHOR;
    }

    private Date getCommitDate() {
        return COMMIT_DATE;
    }

    /**
     * Build information about Commit instance as String
     * @return Concatenated Commit fields
     */
    @Override
    public String toString() {
        return getHash() + " " + getCommitMessage() + " " + getCommitAuthor() + " " +
                getCommitDate() + "\n";
    }
}
