package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;

import java.util.Date;

public class Commit extends GitObject {

    private String commitMessage;
    private final String COMMIT_AUTHOR = System.getProperty("user.name");
    private final Date COMMIT_DATE = new Date(System.currentTimeMillis());

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
     * build line for .git/log/branchName with all information about commit
     */
    @Override
    public String toString() {
        return getHash() + " " + getCommitMessage() + " " + getCommitAuthor() + " " +
                getCommitDate() + "\n";
    }
}
