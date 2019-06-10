package com.pint.lib;

public class Checks {
    public static void checkModuleName(String s) {
        if (!s.matches("^[\\w-]+$")) {
            throw new IllegalArgumentException("Module must be alphanumeric, _, or -");
        }
    }
}