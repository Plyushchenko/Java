import java.nio.file.Path;
import java.util.List;

public class Tree extends VCSObject {

    public Tree(byte[] content) {
        super(buildHash(content), "tree", content.length, content);
    }

    public Tree(List<Path> filesToCommit, List<String> hashesOfFilesToCommit) {
        this(buildContent(filesToCommit, hashesOfFilesToCommit));
    }

    public static byte[] buildContent(List<Path> filesToCommit, List<String> hashesOfFilesToCommit) {
        String buffer = "";
        for (int i = 0; i < filesToCommit.size(); i++){
            buffer += filesToCommit.get(i) + "\n" + hashesOfFilesToCommit.get(i) + "\n";
        }
        return buffer.getBytes();
    }
}
