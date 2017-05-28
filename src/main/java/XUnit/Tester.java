package XUnit;

import XUnit.Annotations.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static XUnit.Annotations.Test.NO_IGNORE;

/** Tester*/
public class Tester {

    @NotNull private final List<Class> classesToTest;
    @NotNull private final List<Method> beforeClassAnnotatedMethods = new ArrayList<>();
    @NotNull private final List<Method> beforeAnnotatedMethods = new ArrayList<>();
    @NotNull private final List<Method> afterAnnotatedMethods = new ArrayList<>();
    @NotNull private final List<Method> afterClassAnnotatedMethods = new ArrayList<>();
    @NotNull private final List<TestToRunInformation> testsToRunInformation = new ArrayList<>();
    @NotNull private final List<TestReport> testReports = new ArrayList<>();

    public Tester(@NotNull String[] args) throws IOException, ClassNotFoundException {
        classesToTest = new Parser(args).buildListOfClassesToTest();
    }

    public Tester() {
        classesToTest = new ArrayList<>();
    }

    /**
     * Test one class.
     * Collect methods with annotations into special lists.
     * Run '@BeforeClass' methods.
     * For each '@Test' method: run '@Before' methods, run '@Test' method, run '@After' method.
     * Run '@AfterClass' methods.
     * @param classToTest Class to test
     * @throws NoSuchMethodException No default constructor provided
     * @throws IllegalAccessException Some problems with method invocation
     * @throws InvocationTargetException Some problems with method invocation
     * @throws InstantiationException Some problems with instantiation
     */
    public void test(@NotNull Class classToTest) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        boolean haveMethodToTest = prepareForClassTesting(classToTest);
        if (!haveMethodToTest) {
            return;
        }
        Object classToTestInstance = ((Class<?>)classToTest).getConstructor().newInstance();
        runAll(beforeClassAnnotatedMethods, classToTestInstance);
        for (TestToRunInformation testToRunInformation : testsToRunInformation) {
            runAll(beforeAnnotatedMethods, classToTestInstance);
            Method methodToTest = testToRunInformation.getMethod();
            Class expected = testToRunInformation.getExpected();
            Throwable throwable = null;
            long executionTime = System.currentTimeMillis();
            try {
                methodToTest.invoke(classToTestInstance);
            } catch (Throwable t) {
                throwable = t.getCause();
            } finally {
                executionTime = System.currentTimeMillis() - executionTime;
            }
            testReports.add(new TestReport(methodToTest, executionTime, expected, throwable));
            runAll(afterAnnotatedMethods, classToTestInstance);
        }
        runAll(afterClassAnnotatedMethods, classToTestInstance);
    }

    /**
     * Test all the methods in 'args' jars
     * @throws NoSuchMethodException No default constructor provided
     * @throws IllegalAccessException Some problems with method invocation
     * @throws InvocationTargetException Some problems with method invocation
     * @throws InstantiationException Some problems with instantiation
     */
    public void testAll() throws InvocationTargetException,
            NoSuchMethodException, InstantiationException,
            IllegalAccessException {
        for (Class classToTest : classesToTest) {
            test(classToTest);
        }
    }

    @NotNull public List<TestReport> getTestReports() {
        return testReports;
    }

    private void clearAll() {
        beforeClassAnnotatedMethods.clear();
        beforeAnnotatedMethods.clear();
        afterAnnotatedMethods.clear();
        afterClassAnnotatedMethods.clear();
        testsToRunInformation.clear();
    }

    private boolean prepareForClassTesting(@NotNull Class classToTest) {
        clearAll();
        boolean haveMethodToTest = false;
        Method[] methods = classToTest.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeClass.class)) {
                beforeClassAnnotatedMethods.add(method);
            }
            if (method.isAnnotationPresent(Before.class)) {
                beforeAnnotatedMethods.add(method);
            }
            if (method.isAnnotationPresent(After.class)) {
                afterAnnotatedMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterClass.class)) {
                afterClassAnnotatedMethods.add(method);
            }
            if (method.isAnnotationPresent(Test.class)) {
                haveMethodToTest = true;
                Test testAnnotation = method.getAnnotation(Test.class);
                if (testAnnotation.ignore().equals(NO_IGNORE)) {
                    testsToRunInformation.add(new TestToRunInformation(method,
                            testAnnotation.expected()));
                } else {
                    testReports.add(new TestReport(method, testAnnotation.ignore()));
                }
            }
        }
        return haveMethodToTest;
    }

    private void runAll(@NotNull List<Method> methods, @NotNull Object o) throws
            InvocationTargetException,
            IllegalAccessException {
        for (Method method : methods) {
            method.invoke(o);
        }
    }

}

