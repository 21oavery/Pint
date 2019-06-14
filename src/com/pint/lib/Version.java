package com.pint.lib;

public class Version {
    private int max;
    private int min;
    private int bug;

    public Version(int maxIn, int minIn, int bugIn) {
        max = maxIn;
        min = minIn;
        bug = bugIn;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public int getBug() {
        return bug;
    }
}