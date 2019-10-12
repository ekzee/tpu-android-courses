package ru.tpu.courses.lab3;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Кэш списка студентов в оперативной памяти. Самый быстрый тип кэша, но ограничен размерами
 * оперативной памяти и имеет время жизни равное жизни приложения. Т.е. если приложение будет
 * выгружено из оперативной памяти - то все данные из этого кэша пропадут.
 * Такой тип кэшей используется для временных данных, потеря которых не важна, в большинстве случаев
 * чтобы не делать дополнительные запросы на сервер.
 */
public class StudentsCache {

    private static StudentsCache instance;

    /**
     * Классическая реализация паттерна Singleton. Нам необходимо, чтобы в приложении был только
     * один кэш студентов.
     */
    public static StudentsCache getInstance() {
        if (instance == null) {
            synchronized (StudentsCache.class) {
                if (instance == null) {
                    instance = new StudentsCache();
                }
            }
        }
        return instance;
    }

    private Set<Student> students = new LinkedHashSet<>();

    private StudentsCache() {
    }

    @NonNull
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public void addStudent(@NonNull Student student) {
        students.add(student);
    }

    public boolean contains(@NonNull Student student) {
        return students.contains(student);
    }
}
