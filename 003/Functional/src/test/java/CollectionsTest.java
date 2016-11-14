import Functional.Collections;
import Functional.Function1;
import Functional.Function2;
import Functional.Predicate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CollectionsTest {

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
            public Boolean apply(Integer x) {
                return (x % 2) == 1;
            }
        }
        IsOdd io = new IsOdd();
        List<Integer> a = new ArrayList<>();
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
    public void foldTest() throws Exception{
        class Divide extends Function2<Double, Double, Double> {
            @Override
            public Double apply(Double a, Double b) {
                return a / b;
            }
        }
        Divide d = new Divide();
        Double a = Collections.foldl(d, 100d, Arrays.asList(2d, 5d));
        Double b = Collections.foldr(d, 100d, Arrays.asList(2d, 5d));
        assertEquals(a, Double.valueOf(10));
        assertEquals(b, Double.valueOf(40));
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

}
