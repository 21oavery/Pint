package com.pint.lib;

public interface Provider {
    Runnable request(Environment e, Dependency d);
    Dependency[] deps()
}