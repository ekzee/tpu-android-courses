package ru.tpu.courses.lab5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Слушатель на события выполнения задачи.
 *
 * @param <T> возвращаемый тип данных.
 */
public interface Observer<T> {
    /**
     * Задача начала своё выполнение.
     */
    void onLoading(@NonNull Task<T> task);

    /**
     * Задача была успешно выполнена.
     */
    void onSuccess(@NonNull Task<T> task, @Nullable T data);

    /**
     * В процессе выполнения задачи был выброшен {@link Exception}.
     */
    void onError(@NonNull Task<T> task, @NonNull Exception e);
}
