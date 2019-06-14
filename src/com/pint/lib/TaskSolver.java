package com.pint.lib;

import com.pint.api.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class TaskSolver {
    private ConcurrentHashMap<Task, RunnableTask> taskRunnables;
    private BlockingQueue<RunnableTask> taskList = new LinkedBlockingQueue<>();

    private ArrayList<Task> execList = new ArrayList<>();

    private ReentrantLock runnableLock = new ReentrantLock();

    private ExecutorService serv;

    public TaskSolver(Task t, int threadCnt) {
        serv = Executors.newFixedThreadPool(threadCnt);
        addTask(t);
    }

    private static void addTasks(Task tIn, HashMap<Task, Boolean> states, ArrayList<Task> taskList, HashSet<Task> circleLookup) {
        for (Dependency d : tIn.getDependencies()) {
            Task t = TaskRegistry.getTask(d);
            if (t == null) {
                System.err.println("[ERROR] Unable to find dependency " + d);
                states.put(tIn, Boolean.FALSE);
                return;
            }
            if (circleLookup.contains(t)) {
                System.err.println("[ERROR] Unable to find circular dependency " + d);
                states.put(tIn, Boolean.FALSE);
                return;
            }
        }
        for (Dependency d : tIn.getOptDependencies()) {
            Task t = TaskRegistry.getTask(d);
            if (t == null) {
                System.err.println("[WARN] Unable to find optional dependency " + d);
                states.put(t, Boolean.FALSE);
                return;
            }
            if (circleLookup.contains(t)) {
                System.err.println("[WARN] Unable to find circular optional dependency " + d);
                states.put(t, Boolean.FALSE);
                return;
            }
        }
    }

    public void run(int threadCnt) {
        while Task t = solveQueue.poll();
    }

    void addTask(Task t) {
        addTask(getRunnableOrCalc(t));
    }

    void addTask(RunnableTask t) {
        taskList.add(t);
    }

    public RunnableTask getRunnableOrCalc(Task t) {
        return taskRunnables.computeIfAbsent(t, Task::getRunnable);
    }

    public RunnableTask getRunnable(Task t) {
        return taskRunnables.get(t);
    }
}