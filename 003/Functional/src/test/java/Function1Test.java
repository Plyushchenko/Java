import Functional.Function1;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Function1Test {

    @Test
    public void composeTest() throws Exception{
        class AddTwo extends Function1<Integer,Integer> {
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

}
