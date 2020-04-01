package ru.tpu.courses.lab5;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

/**
 * Базовый класс для выполнения задачи на фоновом потоке и корректной нотификации UI потока о статусе
 * её выполнения.
 *
 * @param <T> тип возвращаемых задачей данных.
 */
public abstract class Task<T> implements Runnable {

    private Observer<T> observer;
    /**
     * <p>
     * {@link Looper} - класс, который в бесконечном цикле выполняет сообщения из очереди. UI поток
     * имеет свой {@link Looper} и рендер UI и обработка пользовательских действий обрабатываются
     * через эту очередь.
     * </p>
     * <p>
     * Класс {@link Handler} синхронизирован и может использоваться безопасно из любых потоков для
     * добавления сообщений в очередь указанному {@link Looper}.
     * </p>
     */
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public Task(@Nullable Observer<T> observer) {
        this.observer = observer;
    }

    @Override
    public final void run() {
        // Устанавливаем низкий приоритет потоку, чтобы не нагружать цпу
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        // Через метод post сообщаем через обсервер статусы выполнения задачи
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

    /**
     * Зануляем обсервер, чтобы не создать утечку памяти (т.к. observer потенциально держит ссылку
     * на Activity)
     */
    public final void unregisterObserver() {
        observer = null;
    }
}
