package ru.artempugachev.simpleweight;


import java.util.Calendar;

public class Stats {
    private long MONTH_MS = (long) 1000*60*3600*24*30;
    private void update() {
        //  updates 7 and 30 days values
    }

    public float[] getCurrent() {
        // getting stats as array.
        float week = 0;
        float month = 0;

        //  get now and month ago timestamp values
        long nowTimeStamp = System.currentTimeMillis();
        long monthAgo = (long) nowTimeStamp - MONTH_MS;

        return new float[] {week, month};
    }
}
