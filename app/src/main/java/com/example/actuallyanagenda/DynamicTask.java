package com.example.actuallyanagenda;

public class DynamicTask {
    String ID;
    int duration;
    long due;

    public DynamicTask(String ID, int duration, long due) {
        this.ID = ID;
        this.duration = duration;
        this.due = due;
    }
}