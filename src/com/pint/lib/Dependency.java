package com.pint.lib;

import com.pint.api.Task;

import java.util.Arrays;

public class Dependency {
    private String taskName;
    private Object[] args;

    public Dependency(String taskNameIn, Object... argsIn) {
        Checks.checkTaskName(taskNameIn);
        taskName = taskNameIn;
        args = argsIn;
    }

    String getName() {
        return taskName;
    }

    Object[] getArgs() {
        return args;
    }

    public Task getTask() {
        return TaskRegistry.getTask(this);
    }

    @Override
    public String toString() {
        return taskName + "(" + ((args.length == 0) ? "" : "...") + ")";
    }

    @Override
    public int hashCode() {
        return taskName.hashCode() * 31 + Arrays.deepHashCode(args);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || ((o instanceof Dependency) && ((Dependency) o).taskName.equals(taskName) && Arrays.deepEquals(((Dependency) o).args, args));
    }
}