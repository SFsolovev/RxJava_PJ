package ru.junior.rx;

public interface Scheduler {
    void execute(Runnable task);
}
