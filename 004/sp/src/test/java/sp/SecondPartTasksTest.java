package sp;

import org.junit.Test;
import sun.misc.Cache;

import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;
import static sp.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        final List<String> PATHS = Arrays.asList(Paths.get("src", "main", "java", "sp", "Album.java").toString(),
                Paths.get("src", "main", "java", "sp", "Artist.java").toString(),
                Paths.get("src", "main", "java", "sp", "Track.java").toString());

        final List<CharSequence> testsValues = Arrays.asList("package sp;", "private final int", "abracadabra");
        final List<List<String>> testsResults = Arrays.asList(Arrays.asList("package sp;", "package sp;", "package sp;"),
                Arrays.asList("    private final int rating;"),
                Arrays.asList());
        for (int i = 0; i < testsValues.size(); i++) {
            assertEquals(testsResults.get(i), findQuotes(PATHS, testsValues.get(i)));
        }
    }

    @Test
    public void testPiDividedBy4() {
        final double EPS = 1e-3;
        assertEquals(piDividedBy4(), Math.PI / 4, EPS);
    }

    @Test
    public void testFindPrinter() {
        final HashMap<String, List<String>> compositions = new HashMap<String, List<String>>(){{
            put("A", Arrays.asList("short", "text"));
            put("B", Arrays.asList("this is a very long text"));
            put("C", Arrays.asList("l", "oooo", "ng", "text"));
        }};
        assertEquals("B", findPrinter(compositions));
    }
    @Test
    public void testCalculateGlobalOrder() {
        HashMap<String, Integer> firstOrder = new HashMap<String, Integer>(){{
            put("problems", 98);
            put("mile", 8);
        }};
        HashMap<String, Integer> secondOrder = new HashMap<String, Integer>(){{
            put("problems", 1);
            put("beach", 0);
        }};
        HashMap<String, Integer> testResult = new HashMap<String, Integer>(){{
            put("problems", 99);
            put("beach", 0);
            put("mile", 8);
        }};
        assertEquals(testResult, calculateGlobalOrder(Arrays.asList(firstOrder, secondOrder)));
    }
}