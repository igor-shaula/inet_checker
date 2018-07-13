package com.igor_shaula.inetchecker.main.inet_polling;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class DelayedSingleTaskEngineExecutor extends DelayedSingleTaskEngine {

//    private static final String CN = "DelayedSingleTaskEngineExecutor";

    @NonNull
    private ScheduledThreadPoolExecutor oneGenerationExecutor = new ScheduledThreadPoolExecutor(1);
    @Nullable
    private ScheduledFuture<?> scheduledFuture;

    @Override
    synchronized void appointNextGeneration(@NonNull Runnable task, long delay) {
        scheduledFuture = oneGenerationExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
//        L.v(CN, "scheduledFuture created with hashCode: " + scheduledFuture.hashCode());
    }

    @Override
    synchronized boolean isCurrentGenerationAlive() {
        return scheduledFuture != null
                && !scheduledFuture.isCancelled()
                && !scheduledFuture.isDone();
    }

    @Override
    synchronized void stopCurrentGeneration() {
        // used try-catch block because for some reason if (scheduledFuture != null) caused NPE inside \\
        try {
            //noinspection ConstantConditions
            scheduledFuture.cancel(true);
//            boolean howCancelled = scheduledFuture.cancel(true);
//            L.v(CN, "scheduledFuture cancelled with hashCode: " + scheduledFuture.hashCode());
//            L.v(CN, "stopCurrentGeneration ` scheduledFuture cancelled " + howCancelled);
//            L.v(CN, "stopCurrentGeneration ` scheduledFuture isCancelled " + scheduledFuture.isCancelled());
//            L.v(CN, "stopCurrentGeneration ` scheduledFuture isDone " + scheduledFuture.isDone());
        } catch (NullPointerException npe) {
//            L.w(CN, "stopCurrentGeneration ` NullPointerException on scheduledFuture happened");
        } finally {
            scheduledFuture = null;
        }
    }
}