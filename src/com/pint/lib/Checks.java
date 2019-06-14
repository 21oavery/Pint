package com.pint.lib;

public class Checks {
    public static void checkTaskName(String s) {
        if (!s.matches("^[\\w.\\-_#]+$")) {
            throw new IllegalArgumentException("Task name must only have alphanumeric characters, periods, dashes, underscores, and hash marks");
        }
    }
}