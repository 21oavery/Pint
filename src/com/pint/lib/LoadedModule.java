package com.pint.lib;

import java.util.ArrayList;

public class LoadedModule {
    public ArrayList<LoadedTask> tasks;
    public ArrayList<LoadedModule> subModules;

    public String name;
    public Version version;
}