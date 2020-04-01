package ru.tpu.courses.lab5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface Observer<T> {
    void onLoading(@NonNull Task<T> task);

    void onSuccess(@NonNull Task<T> task, @Nullable T data);

    void onError(@NonNull Task<T> task, @NonNull Exception e);
}
