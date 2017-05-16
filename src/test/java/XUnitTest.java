import Tests.TestClass;
import Tests.TestClass2;
import XUnit.Tester;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class XUnitTest {


    @Test
    public void checkTestClassesTest() throws IOException, ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        List<String> expectedReports = new ArrayList<>(Arrays.asList(
                "IGNORED: TestClass.ignoreTest",
                "FAILED:  TestClass.exceptionThrownTest",
                "PASSED:  TestClass.expectedExceptionThrownTest",
                "FAILED:  TestClass.unexpectedExceptionThrownTest",
                "PASSED:  TestClass.checkFlag",
                "PASSED:  TestClass.emptyTest",
                "PASSED:  TestClass.shouldPassTest",
                "PASSED:  TestClass2.checkFlag",
                "PASSED:  TestClass2.checkFlag2",
                "PASSED:  TestClass2.checkFlag3"
        ));
        Tester tester = new Tester();
        tester.test(TestClass.class);
        tester.getTestReports().forEach(testReport -> {
            assertTrue(expectedReports.stream().anyMatch( expectedReport -> testReport.toString().contains
                    (expectedReport)));
        });
        tester.test(TestClass2.class);
        tester.getTestReports().forEach(testReport -> {
            assertTrue(expectedReports.stream().anyMatch( expectedReport -> testReport.toString().contains
                    (expectedReport)));
        });
    }

}
