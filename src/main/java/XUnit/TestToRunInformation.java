package XUnit;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/* Helper class for test to run information*/
class TestToRunInformation {

    @NotNull private final Method method;
    @NotNull private final Class expected;

    TestToRunInformation(@NotNull Method method, @NotNull Class expected) {
        this.method = method;
        this.expected = expected;
    }

    @NotNull Method getMethod() {
        return method;
    }

    @NotNull Class getExpected() {
        return expected;
    }

}
