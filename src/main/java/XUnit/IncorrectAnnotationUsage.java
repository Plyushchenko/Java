package XUnit;

import org.jetbrains.annotations.NotNull;

/** '@Test' was used with '@BeforeClass'/'@Before'/'@After'/'@AfterClass'*/
class IncorrectAnnotationUsage extends Exception {

    IncorrectAnnotationUsage(@NotNull String message) {
        super(message);
    }

}
