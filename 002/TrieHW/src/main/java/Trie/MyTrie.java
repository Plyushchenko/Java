package Trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyTrie  implements Trie, StreamSerializable{

    private MyTrieNode root;
    public MyTrie() {
        root = new MyTrieNode();
    }

    @Override
    public boolean add(String element) {
        MyTrieNode now = root;
        for (int i = 0; i < element.length(); i++) {
            if (now.getChild(element.charAt(i)) == null){
                now.makeChild(element.charAt(i), new MyTrieNode());
            }
            now = now.getChild(element.charAt(i));
        }
        if (!now.isTerminal()) {
            now.setIsTerminal();
            updateTerminals(element, 1);
            return true;
        }
        return false;
    }

    private void updateTerminals(String element, int value) {
        root.changeSubtreeTerminals(value);
        MyTrieNode now = root;
        for (int i = 0; i < element.length(); i++)
        {
            now = now.getChild(element.charAt(i));
            now.changeSubtreeTerminals(value);
        }
    }

    @Override
    public boolean contains(String element) {
        MyTrieNode now = root;
        for (int i = 0; i < element.length(); i++) {
            if ((now = now.getChild(element.charAt(i))) == null) {
                return false;
            }
        }
        return now.isTerminal();
    }

    @Override
    public boolean remove(String element) {
        MyTrieNode now = root;
        for (int i = 0; i < element.length(); i++)
        {
            if (now.getChild(element.charAt(i)) == null){
                return false;
            }
            now = now.getChild(element.charAt(i));
        }
        if (now.isTerminal()) {
            now.unsetIsTerminal();
            updateTerminals(element, -1);
            cleanSubtree(element);
            return true;
        }
        return false;
    }

    private void cleanSubtree(String element) {
        MyTrieNode now = root;
        for (int i = 0; i < element.length(); i++) {
            if (now.getChild(element.charAt(i)).subtreeTerminals() == 0) {
                now.unsetChild(element.charAt(i));
                return;
            }
            now = now.getChild(element.charAt(i));
        }
    }

    @Override
    public int size() {
        return root.subtreeTerminals();
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        MyTrieNode now = root;
        for (int i = 0; i < prefix.length(); i++)
        {
            if (now.getChild(prefix.charAt(i)) == null){
                return 0;
            }
            now = now.getChild(prefix.charAt(i));
        }
        return now.subtreeTerminals();
    }


    @Override
    public void serialize(OutputStream out) throws IOException {
        serializeImplementation(root, out);
    }

    private byte[] longToByteArray(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i > -1; i--) {
            result[i] = (byte) (value & 0xff);
            value >>= 8;
        }
        return result;
    }

    private long byteArrayToLong(byte[] value)
    {
        long result = 0;
        for (int i = 0; i < 8; i++){
            result |= (long)(value[i]);
            if (i < 7) result <<= 8;
        }
        return result;
    }

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private void serializeImplementation(MyTrieNode now, OutputStream out) throws IOException{
        long childrenMask = 0;
        if (now.isTerminal()) {
            childrenMask |= (1l << 62);
        }
        for (int i = 0; i < LETTERS.length(); i++){
            if (now.getChild(LETTERS.charAt(i)) != null){
                childrenMask |= (1 << i);
            }
        }
        out.write(longToByteArray(childrenMask));
        for (int i = 0; i < LETTERS.length(); i++){
            if (now.getChild(LETTERS.charAt(i)) != null){
                serializeImplementation(now.getChild(LETTERS.charAt(i)), out);
            }
        }
    }
    @Override
    public void deserialize(InputStream in) throws IOException {
        deserializeImplementation(root, in);
        updateTerminalsAfterDeserialize(root);
    }

    private void updateTerminalsAfterDeserialize(MyTrieNode now) {
        int value = now.isTerminal() ? 1 : 0;
        for (int i = 0; i < LETTERS.length(); i++) {
            if (now.getChild(LETTERS.charAt(i)) != null) {
                updateTerminalsAfterDeserialize(now.getChild(LETTERS.charAt(i)));
                value += now.getChild(LETTERS.charAt(i)).subtreeTerminals();
            }
        }
        now.changeSubtreeTerminals(value);
    }
    private void deserializeImplementation(MyTrieNode now, InputStream in) throws IOException {
        byte[] bytesForChildrenMask = new byte[8];

        if (in.read(bytesForChildrenMask) != 8){
            throw new IOException();
        }

        long childrenMask = byteArrayToLong(bytesForChildrenMask);
        if (((childrenMask >> 62) & 1) == 1){
            now.setIsTerminal();
        }
        if (childrenMask == (1l << 62)){
            return;
        }
        for (int i = 0; i < LETTERS.length(); i++){
            if (((childrenMask >> i) & 1) == 1){
                now.makeChild(LETTERS.charAt(i), new MyTrieNode());
                deserializeImplementation(now.getChild(LETTERS.charAt(i)), in);
            }
        }
    }

}