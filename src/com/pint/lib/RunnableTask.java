package com.pint.lib;

import com.pint.api.Task;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableTask implements Runnable {
    private TaskSolver solver;

    private TaskState state;
    private Runnable task;
    private HashSet<Task> deps = new HashSet<>();
    private HashSet<Task> optDeps = new HashSet<>();

    private Lock lock = new ReentrantLock();

    private Object notifyDone = new Object();

    public RunnableTask(Runnable taskIn, HashSet<Dependency> depsIn, HashSet<Dependency> optDepsIn) {
        task = taskIn;
        for (Dependency d : depsIn) {
            Task t = d.getTask();
            if (t == null) {

                state = TaskState.ERRORED;
                return;
            }
            deps.add(t);
        }
        for (Dependency d : optDepsIn) {
            Task t = d.getTask();
            if (t == null) {
                System.err.println("[WARN] Unable to find optional dependency " + d);
            }
            optDeps.add(t);
        }
    }

    @Override
    public void run() {
        lock.lock();
        switch (state) {
            case WAITING:
                deps.removeIf(t -> {
                    RunnableTask rt = solver.getRunnableOrCalc(t);
                    if (rt == null) return false;

                })
            case RUNNING:
            case ERRORED:
            case DONE:
                lock.unlock();
                return;
        }
    }

    public boolean isDone() {
        lock.lock();
        boolean b = (state == TaskState.DONE) || (state == TaskState.ERRORED);
        lock.unlock();
        return b;
    }
}