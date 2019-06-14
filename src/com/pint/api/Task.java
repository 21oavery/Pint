package com.pint.api;

import com.pint.lib.Dependency;
import com.pint.lib.RunnableTask;

public interface Task {
    RunnableTask getRunnable();
    Dependency[] getDependencies();
    Dependency[] getOptDependencies();
}