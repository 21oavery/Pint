package com.pint.lib;

import java.io.File;
import java.util.HashSet;

public class Environment {
    private File mainDir;
    private HashSet<String> finished = new HashSet<>();

    public Environment() {
    }
}