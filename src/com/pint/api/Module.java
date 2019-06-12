package com.pint.api;

public @interface Module {
    String name();
    int maxV() default 1;
    int minV() default 0;
    int bugV() default 0;
    String deps() default "";
    String optDeps() default "";
}