package Trie;

import java.util.HashMap;
import java.util.Set;


class MyTrieNode {

    private boolean isTerminal = false;
    private int subtreeTerminals = 0;
    private final HashMap<Character, MyTrieNode> children = new HashMap<>();

    public int subtreeTerminals() {

        return subtreeTerminals;
    }

    public void changeSubtreeTerminals(int value) {
        subtreeTerminals += value;
    }

    public MyTrieNode getChild(char c) {

        return children.get(c);
    }

    public MyTrieNode makeChild(char key, MyTrieNode value) {

        return children.put(key, value);
    }

    public void setIsTerminal() {

        isTerminal = true;
    }

    public boolean isTerminal() {

        return isTerminal;
    }

    public void unsetIsTerminal() {

        isTerminal = false;
    }

    public void unsetChild(char c) {

        children.remove(c);
    }
}
