package ru.tpu.courses.lab5;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

public abstract class Task<T> implements Runnable {

    private Observer<T> observer;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public Task(@Nullable Observer<T> observer) {
        this.observer = observer;
    }

    @Override
    public final void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mainHandler.post(() -> {
            if (observer != null) {
                observer.onLoading(Task.this);
            }
        });
        try {
            final T data = executeInBackground();
            mainHandler.post(() -> {
                if (observer != null) {
                    observer.onSuccess(Task.this, data);
                }
            });
        } catch (final Exception e) {
            mainHandler.post(() -> {
                if (observer != null) {
                    observer.onError(Task.this, e);
                }
            });
        }
    }

    @Nullable
    @WorkerThread
    protected abstract T executeInBackground() throws Exception;

    public void unregisterObserver() {
        observer = null;
    }
}
