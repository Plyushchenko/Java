import Trie.MyTrie;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class MyTrieTest {

   @Test
   public void justAdd() throws Exception{
       final  MyTrie trie = new MyTrie();
       assertTrue(trie.add("teststring"));
       assertTrue(trie.contains("teststring"));
       assertFalse(trie.contains("hahaha"));
       assertEquals(1, trie.size());
       assertTrue(trie.remove("teststring"));
       assertEquals(0, trie.size());
       assertFalse(trie.contains("teststring"));
       assertFalse(trie.contains("hahaha"));
   }

   @Test
   public void addPrefix() throws Exception{
       final  MyTrie trie = new MyTrie();
       assertTrue(trie.add("teststring"));
       assertFalse(trie.contains("test"));
       assertTrue(trie.add("test"));
       assertEquals(2, trie.size());
       assertTrue(trie.contains("test"));
   }

   @Test
   public void removePrefix() throws Exception{
       final  MyTrie trie = new MyTrie();
       assertTrue(trie.add("TEST"));
       assertTrue(trie.add("TESTTSET"));
       assertTrue(trie.remove("TESTTSET"));
       assertTrue(trie.contains("TEST"));
       assertFalse(trie.contains("TESTTSET"));
   }

    @Test
    public void doubleAddAndRemove() throws Exception{
        final  MyTrie trie = new MyTrie();
        assertTrue(trie.add("string"));
        assertFalse(trie.add("string"));
        assertEquals(1, trie.size());
        assertTrue(trie.remove("string"));
        assertFalse(trie.remove("string"));
        assertEquals(0, trie.size());

    }

    @Test
    public void test_all() throws Exception{
        final MyTrie trie = new MyTrie(), trie2 = new MyTrie();
        String[] WORDS = { "a", "aa", "aaa", "aaaacd", "aaab", "abab", "abab", "b", "bb", "aabac", "aaacc" };
        for (String word : WORDS) {
            trie.add(word);
        }
        DataOutputStream out = new DataOutputStream(new FileOutputStream("out"));
        trie.serialize(out);
        out.close();

        DataInputStream in = new DataInputStream(new FileInputStream("out"));
        trie2.deserialize(in);
        in.close();

        DataOutputStream out2 = new DataOutputStream(new FileOutputStream("out2"));
        trie2.serialize(out2);
        out2.close();


        //trie2 contains same prefixes (and words) as trie and nothing else cuz of the same size of all subtrees
        for (String word : WORDS){
           assert(trie2.contains(word));
        }
        assertEquals(trie.size(), trie2.size());
        for (String word: WORDS){
            for (int i = 0; i < word.length(); i++) {
                String prefix = word.substring(0, i);
                assertEquals(trie.howManyStartsWithPrefix(prefix), trie2.howManyStartsWithPrefix(prefix));
                assertEquals(trie.contains(prefix), trie2.contains(prefix));
            }
        }
    }
}