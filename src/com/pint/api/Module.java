package com.pint.api;

public @interface Module {
    String name();
    int maxV();
    int minV();
    int bugV();
    String deps();
    String optDeps();
}