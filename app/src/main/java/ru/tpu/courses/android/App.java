package ru.tpu.courses.android;

import android.app.Application;

import ru.tpu.courses.lab3.Student;
import ru.tpu.courses.lab3.StudentsCache;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // region lab3
        StudentsCache studentsCache = StudentsCache.getInstance();
        studentsCache.addStudent(new Student("Хардкод", "Хардкодович", "Хардкодов"));
        // endregion lab3
    }
}
