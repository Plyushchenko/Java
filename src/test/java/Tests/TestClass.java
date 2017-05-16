package Tests;

import XUnit.Annotations.*;

public class TestClass {

    public boolean flag = false;

    @Before
    public void unsetFlag() {
        flag = false;
    }

    @After
    public void setFlag() {
        flag = true;
    }

    @Test
    public void checkFlag() {
        if (flag) {
            throw new RuntimeException("flag should be false because of unsetFlag()");
        }
    }

    @Test
    public void emptyTest() {
    }

    @Test (ignore = "10^10 iterations, NO-NO-NO")
    public void ignoreTest() {
        for (long i = 0; i < 1e10; i++) {
            System.out.println("SORRY");
        }
    }

    @Test
    public void shouldPassTest() {
        int x = 10000 / 2 * 9999;
        for (int i = 1; i <= 9999; i++) {
            x -= i;
        }
        if (x != 0) {
            throw new RuntimeException("Sum calculated incorrectly");
        }
    }

    @Test
    public void exceptionThrownTest() {
        int x = 5 / 0;
    }

    @Test (expected = ArithmeticException.class)
    public void expectedExceptionThrownTest() {
        int x = 5 / 0;
    }

    @Test (expected = OutOfMemoryError.class)
    public void unexpectedExceptionThrownTest() {
        int x = 5 / 0;
    }

}
