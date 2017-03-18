import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class Commit extends VCSObject{

    private String commitMessage;
    public final String COMMIT_AUTHOR = System.getProperty("user.name");
    public final Date COMMIT_DATE = new Date(System.currentTimeMillis());

    public Commit(byte[] content, String message) {
        super(buildHash(content), "commit", content.length, content);
        commitMessage = message;
    }

    public String getCommitMessage() {
        return commitMessage;
    }
}
