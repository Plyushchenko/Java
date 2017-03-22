import Functional.Predicate;
import org.junit.Test;

import static Functional.Predicate.ALWAYS_FALSE;
import static Functional.Predicate.ALWAYS_TRUE;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PredicateTest {

    @Test
    public void lazinessTest() throws Exception{
        Predicate <Object> failPredicate = new Predicate<Object>() {
            @Override
            public Boolean apply(Object o) {
                fail();
                return null;
            }
        };
        assertTrue(ALWAYS_TRUE.or(failPredicate).apply(null));
    }

    @Test
    public void andTest() throws Exception{
        assertFalse(ALWAYS_TRUE.and(ALWAYS_FALSE).apply("127.0.0.1"));
    }

    @Test
    public void orTest() throws Exception{
        assertFalse(ALWAYS_FALSE.or(ALWAYS_FALSE.or(ALWAYS_FALSE)).apply("12-85-00"));
    }

}
