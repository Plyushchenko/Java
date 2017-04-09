package VCS.Objects.GitObjects;

import VCS.Data.FileSystem;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/** Commit */
public class Commit extends GitObject {

    @NotNull private String commitMessage;
    @NotNull private final String COMMIT_AUTHOR = System.getProperty("user.name");
    @NotNull private final Date COMMIT_DATE = new Date(System.currentTimeMillis());

    /**
     * Build Commit
     * @param fileSystem File system
     * @param content Commit content
     * @param message Commit message
     */
    public Commit(@NotNull FileSystem fileSystem, byte[] content, @NotNull String message) {
        super(fileSystem, buildHash(content), "commit", content.length, content);
        commitMessage = message;
    }

    @NotNull
    private String getCommitMessage() {
        return commitMessage;
    }

    @NotNull
    private String getCommitAuthor() {
        return COMMIT_AUTHOR;
    }

    @NotNull
    private Date getCommitDate() {
        return COMMIT_DATE;
    }

    /**
     * Build information about Commit instance as String
     * @return Concatenated Commit fields
     */
    @NotNull
    @Override
    public String toString() {
        return getHash() + " " + getCommitMessage() + " " + getCommitAuthor() + " " +
                getCommitDate() + "\n";
    }
}
