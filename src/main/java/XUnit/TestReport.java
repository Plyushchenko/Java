package XUnit;

import XUnit.Annotations.Test;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static XUnit.Annotations.Test.NO_IGNORE;

public class TestReport {

    @NotNull private final String methodName;
    private long executionTime = 0;
    @NotNull private String reasonToIgnore = NO_IGNORE;
    @NotNull private Class expected = Test.NO_THROWABLE.class;
    @Nullable private Throwable throwable = null;
    private String status;
    private final String stringValue;

    TestReport(@NotNull Method method, @NotNull String reasonToIgnore) {
        this.methodName = buildMethodName(method);
        this.reasonToIgnore = reasonToIgnore;
        stringValue = buildStringValue();
    }

    TestReport(@NotNull Method method, long executionTime, @NotNull Class expected,
               @Nullable Throwable throwable) {
        this.methodName = buildMethodName(method);
        this.executionTime = executionTime;
        this.expected = expected;
        this.throwable = throwable;
        stringValue = buildStringValue();
    }

    private String buildStringValue() {
        if (!reasonToIgnore.equals(NO_IGNORE)) {
            status = "IGNORED";
            return status + ": " + methodName + ", because of \"" + reasonToIgnore + "\"";
        }
        if (expected == Test.NO_THROWABLE.class) {
            if (throwable == null) {
                status = "PASSED";
                return status + ":  " + methodName + ", " + executionTime + "ms";
            }
            status = "FAILED";
            return status + ":  " + methodName + ", " + executionTime + "ms, thrown " + throwable
                    .getClass().getName() + " but no throwable expected";
        }
        if (throwable == null) {
            status = "FAILED";
            return status + ":  " + methodName + ", " + executionTime + "ms, thrown nothing but " +
                    expected + " expected";
        }
        if (throwable.getClass() == expected) {
            status = "PASSED";
            return status + ":  " + methodName + ", " + executionTime + "ms, thrown " + throwable
                    .getClass().getName() + " as expected";
        }
        status = "FAILED";
        return status + ":  " + methodName + ", " + executionTime + "ms, thrown " + throwable
                .getClass() + " but " + expected + " expected";
    }

    /**
     * Build test report.
     * IGNORED/PASSED/FAILED: Class.method [time in ms] [info about throwable]
     * @return Test report as string
     */
    @Override
    @NotNull
    public String toString() {
        return stringValue;
    }

    @NotNull
    private static String buildMethodName(@NotNull Method method) {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    @NotNull
    public String getMethodName() {
        return methodName;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    @NotNull
    public String getReasonToIgnore() {
        return reasonToIgnore;
    }

    @NotNull
    public Class getExpected() {
        return expected;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

}
