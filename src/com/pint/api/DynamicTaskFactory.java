package com.pint.api;

public interface DynamicTaskFactory {
    Task genTask(String name, Object... params);
}