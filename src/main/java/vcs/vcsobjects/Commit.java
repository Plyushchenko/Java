package vcs.vcsobjects;

import java.util.Date;

public class Commit extends VCSObject{

    private String commitMessage;
    private final String COMMIT_AUTHOR = System.getProperty("user.name");
    private final Date COMMIT_DATE = new Date(System.currentTimeMillis());

    public Commit(byte[] content, String message) {
        super(buildHash(content), "commit", content.length, content);
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
    public String getAllInformation() {
        return getHash() + " " + getCommitMessage() + " " + getCommitAuthor() + " " + getCommitDate() + "\n";
    }
}
