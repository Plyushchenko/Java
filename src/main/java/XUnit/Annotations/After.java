package XUnit.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Run method after test*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {}
