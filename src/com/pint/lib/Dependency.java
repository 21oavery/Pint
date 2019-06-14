package com.pint.lib;

import com.pint.api.Task;

import javax.naming.OperationNotSupportedException;
import java.util.Arrays;
import java.util.List;

public class Dependency {
    private String taskName;
    private Object[] args;
    private Task taskCache;
    private Dependency[] depCache;
    private Dependency[] optDepCache;

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
        if (taskCache != null) return taskCache;
        return TaskRegistry.getTask(this);
    }

    public Dependency[] getDependencies() throws OperationNotSupportedException {
        if (depCache != null) return depCache;
        Task t = getTask();
        if (t == null) throw new OperationNotSupportedException("Task could not be found");
        return depCache = t.getDependencies();
    }

    public Dependency[] getOptDependencies() throws OperationNotSupportedException {
        if (optDepCache != null) return optDepCache;
        Task t = getTask();
        if (t == null) throw new OperationNotSupportedException("Task could not be found");
        return optDepCache = t.getOptDependencies();
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