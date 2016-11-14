import Functional.Function1;
import Functional.Function2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Function2Test {
    @Test
    public void bindTest() throws Exception{
        class AddInt extends Function2<Double, Integer, Double> {

            @Override
            public Double apply(Double a, Integer b) {
                return a + Double.valueOf(b);
            }
        }
        AddInt ai = new AddInt();
        Function1<Double, Double> addFive = ai.bind2(5);
        Function1<Integer, Double> addPi = ai.bind1(Math.PI);
        assertEquals(16.5, addFive.apply(11.5), 0);
        assertEquals(3 + Math.PI, addPi.apply(3), 0);
    }

    class Sum extends Function2<Integer, Integer, Integer>{
        @Override
        public Integer apply(Integer a, Integer b) {
            return a + b;
        }
    }
    final Function2<Integer, Integer, Integer> sum = new Sum();

    @Test
    public void compose() throws Exception {


        class Square extends Function1<Integer, Integer>{
            @Override
            public Integer apply(Integer a) {
                return a * a;
            }
        }
        Function2<Integer, Integer, Integer> squareSum = sum.compose(new Square());
        assertEquals(30976, (int)squareSum.apply(97, 79));
    }

    @Test
    public void curryTest() throws Exception {
        Function1<Integer, Function1<Integer, Integer>> curriedSum = sum.curry();
        assertEquals(curriedSum.apply(-10).apply(10), sum.apply(10, -10));
    }
}
