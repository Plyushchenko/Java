package XUnit;

import XUnit.Annotations.Test;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

import static XUnit.Annotations.Test.NO_IGNORE;

public class TestReport {

    private final String methodName;
    private long executionTime = 0;
    private String reasonToIgnore = NO_IGNORE;
    private Class expected = Test.NO_THROWABLE.class;
    private Throwable throwable = null;

    TestReport(Method method, String reasonToIgnore) {
        this.methodName = buildMethodName(method);
        this.reasonToIgnore = reasonToIgnore;
    }

    TestReport(Method method, long executionTime, Class expected, Throwable throwable) {
        this.methodName = buildMethodName(method);
        this.executionTime = executionTime;
        this.expected = expected;
        this.throwable = throwable;
    }

    /**
     * Build test report.
     * IGNORED/PASSED/FAILED: Class.method [time in ms] [info about throwable]
     * @return Test report as string
     */
    @Override
    public String toString() {
        if (!reasonToIgnore.equals(NO_IGNORE)) {
            return "IGNORED: " + methodName + ", because of \"" + reasonToIgnore + "\"";
        }
        if (expected == Test.NO_THROWABLE.class) {
             if (throwable == null) {
                 return "PASSED:  " + methodName + ", " + executionTime + "ms";
             }
            return "FAILED:  " + methodName + ", " + executionTime + "ms, thrown " + throwable
                    .getClass().getName() + " but no throwable expected";
        }
        if (throwable == null) {
            return "FAILED:  " + methodName + ", " + executionTime + "ms, thrown nothing but " +
                    expected + " expected";
        }
        if (throwable.getClass() == expected) {
            return "PASSED:  " + methodName + ", " + executionTime + "ms, thrown " + throwable
                    .getClass().getName() + " as expected";
        }
        return "FAILED:  " + methodName + ", " + executionTime + "ms, thrown " + throwable
            .getClass() + " but " + expected + " expected";
    }

    @NotNull
    private static String buildMethodName(@NotNull Method method) {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

}
