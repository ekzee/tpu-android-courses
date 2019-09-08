package ru.tpu.courses.android;

import android.app.Application;

import ru.tpu.courses.lab3.Student;
import ru.tpu.courses.lab3.StudentsCache;

public class App extends Application {
    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        StudentsCache studentsCache = StudentsCache.getInstance();
        studentsCache.addStudent(new Student("Хардкод", "Хардкодович", "Хардкодов"));
    }
}
