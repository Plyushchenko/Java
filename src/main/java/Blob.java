public class Blob extends VCSObject{

    public Blob(byte[] content) {
        super(buildHash(content), "blob", content.length, content);
    }
}
