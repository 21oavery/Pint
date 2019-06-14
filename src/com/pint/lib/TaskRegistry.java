package com.pint.lib;

import com.pint.api.Task;
import com.pint.api.TaskFactory;
import com.pint.lib.Checks;
import com.pint.lib.Dependency;

import java.util.concurrent.ConcurrentHashMap;

public class TaskRegistry {
    private static ConcurrentHashMap<String, TaskFactory> taskFactories = new ConcurrentHashMap<>();

    public static void addSimpleTask(String name, Task t) {
        addTask(name, params -> t);
    }

    public static void addTask(String name, TaskFactory t) {
        Checks.checkTaskName(name);
        if (taskFactories.putIfAbsent(name, t) != null) {
            throw new IllegalArgumentException("Task name already in use");
        }
    }

    private static ConcurrentHashMap<Dependency, Task> taskCache = new ConcurrentHashMap<>();

    public static Task getTask(Dependency d) {
        return taskCache.computeIfAbsent(d, t -> {
            TaskFactory f = taskFactories.get(t.getName());
            if (f == null) return null;
            return f.genTask(t.getArgs());
        });
    }
}