package com.example.actuallyanagenda;

/**
 * Represents the fixed events that can be manually added to the calendar. These function similarly to normal calendar entries, and are immutable as per the auto-scheduler.
 */
public class StaticTask {
    String ID;
    int duration;
    long start;

    public StaticTask(String ID, int duration, long start) {
        this.ID = ID;
        this.duration = duration;
        this.start = start;
    }
}