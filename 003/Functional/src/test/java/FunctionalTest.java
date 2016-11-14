import Functional.*;
import Functional.Collections;
import org.junit.Test;
import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class FunctionalTest {

    @Test
    public void mapTest() throws Exception {
        class HashCode extends Function1<Object,Integer> {
            @Override
            public Integer apply(Object x) {
                return x.hashCode();
            }
        }
        HashCode hc = new HashCode();
        List<String> a = Arrays.asList("ABRA", "CADABRA", "SIM", "SALABIM");
        List<Integer> b = Collections.map(hc, a);
        List<Integer> c = Arrays.asList("ABRA".hashCode(),
                                        "CADABRA".hashCode(),
                                        "SIM".hashCode(),
                                        "SALABIM".hashCode());
        assertEquals(b, c);
    }

    @Test
    public void filterTest() throws Exception {
        class IsOdd extends Predicate<Integer> {
            @Override
            public Boolean apply(Integer x) {
                return (x % 2) == 1;
            }
        }
        IsOdd io = new IsOdd();
        List<Integer> a = new ArrayList<Integer>();
        for (int i = 0; i != 1; i = (i + 87) % 100) {
            a.add(i);
        }
        List<Integer> b = Collections.filter(io, a);
        List<Integer> c = Collections.filter(io.not().not(), a);
        assertEquals(b, c);
        for (Integer x : b){
            assertEquals((x % 2) == 1, io.apply(x));
        }
    }
    @Test
    public void composeTest() throws Exception{
        class AddTwo extends Function1<Integer,Integer>{
            @Override
            public Integer apply(Integer x) {
                return x + 2;
            }
        }
        class AddThree extends Function1<Integer,Integer>{
            @Override
            public Integer apply(Integer x) {
                return x + 3;
            }
        }
        AddTwo two = new AddTwo();
        AddThree three = new AddThree();
        Function1<Integer,Integer> five = two.compose(three);
        for (int i = -10; i <= 10; i++) {
            assertEquals(five.apply(i), Integer.valueOf(i + 5));
        }

    }
    @Test
    public void takeUnlessTest() throws Exception{

        class LessThanThree extends Predicate<Integer>{
            @Override
            public Boolean apply(Integer x) {
                return x < 3;
            }
        }
        LessThanThree ltt = new LessThanThree();
        List<Integer> a = Arrays.asList(54351, 54321, 1109, 2, 65421, 87897, 564321);
        List<Integer> b = Collections.takeUnless(ltt, a);
        assertEquals(Arrays.asList(54351, 54321, 1109), b);
    }
    @Test
    public void foldTest() throws Exception{
        class Divide extends Function2<Double, Double, Double>{
            @Override
            public Double apply(Double a, Double b) {
                return a / b;
            }
        }
        Divide d = new Divide();
        Double a = Collections.foldl(d, Double.valueOf(100), Arrays.asList(Double.valueOf(2), Double.valueOf(5)));
        Double b = Collections.foldr(d, Double.valueOf(100), Arrays.asList(Double.valueOf(2), Double.valueOf(5)));
        assertEquals(a, Double.valueOf(10));
        assertEquals(b, Double.valueOf(40));
    }

    @Test
    public void bindTest() throws Exception{
        class AddInt extends Function2<Double, Integer, Double>{

            @Override
            public Double apply(Double a, Integer b) {
                return a + Double.valueOf(b);
            }
        }
        AddInt ai = new AddInt();
        Function1<Double, Double> addFive = ai.bind2(5);
        assertEquals(16.5, addFive.apply(11.5), 0);
    }

}