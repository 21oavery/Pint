package com.pint.lib;

import java.util.regex.Pattern;

public class TreeLocation {
    private static final Pattern locCheck = Pattern.compile("[^\\w-]");
    private final String[] elements;

    public TreeLocation(String s) {
        if (locCheck.matcher(s).matches()) {
            throw new IllegalArgumentException("Invalid location");
        }
        elements = s.split("\\.");
    }
}