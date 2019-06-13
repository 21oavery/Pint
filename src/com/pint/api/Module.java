package com.pint.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Module {
    String name();
    int maxV() default 1;
    int minV() default 0;
    int bugV() default 0;
    String deps() default "";
    String optDeps() default "";
}