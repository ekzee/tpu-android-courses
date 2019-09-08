package ru.tpu.courses.lab1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * <b>Знакомство с Android Studio и Git.</b>
 */
public class Lab1Activity extends AppCompatActivity {

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab1Activity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Метод setTitle используется для задания заголовка в тулбаре
        // Метод getString достаёт из ресурсов  с параметрами умеет форматировать строки по аналогии с
        setTitle(getString(R.string.lab1_title, getClass().getSimpleName()));
    }
}
