package XUnit.Annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Test method*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {

    /** Reason to ignore and not to run a test*/
    @NotNull String ignore() default NO_IGNORE;

    @NotNull String NO_IGNORE = "";

    /** Expected class for exceptional situations*/
    @NotNull Class expected() default NO_THROWABLE.class;

    final class NO_THROWABLE {}

}
