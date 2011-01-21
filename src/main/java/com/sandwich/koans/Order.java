package com.sandwich.koans;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.*;
import static java.lang.annotation.ElementType.*;

@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Order {
	int value() default 1000;
}
