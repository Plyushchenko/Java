package Tests;

import XUnit.Annotations.After;
import XUnit.Annotations.AfterClass;
import XUnit.Annotations.Before;
import XUnit.Annotations.Test;

public class TestClass2 {

    private boolean flag = false;

    @AfterClass
    public void setFlag() {
        flag = true;
    }

    @Test
    public void checkFlag() {
        if (flag) {
            throw new RuntimeException("flag should be unset");
        }
    }

    @Test
    public void checkFlag2() {
        if (flag) {
            throw new RuntimeException("flag should be unset");
        }
    }

    @Test
    public void checkFlag3() {
        if (flag) {
            throw new RuntimeException("flag should be unset");
        }
    }

    public void notATest() {
        int x = 5 / 0;
    }

}

