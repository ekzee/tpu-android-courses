package ru.tpu.courses.lab4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.tpu.courses.lab4.adapter.StudentsAdapter;
import ru.tpu.courses.lab4.add.AddStudentActivity;
import ru.tpu.courses.lab4.db.Lab4Database;
import ru.tpu.courses.lab4.db.Student;
import ru.tpu.courses.lab4.db.StudentDao;

/**
 * <b>Взаимодействие с файловой системой, SQLite</b>
 * <p>
 * В лабораторной работе вместо сохранения студентов в оперативную память будем сохранять их в
 * базу данных SQLite, которая интегрирована в ОС Android. Для более удобного взаимодействия с
 * ней будем использовать ORM библиотеку Room (подключение см. в build.gradle).
 * </p>
 * <p>
 * В {@link AddStudentActivity} введенные поля теперь сохраняются в
 * {@link android.content.SharedPreferences} - удобный способ для хранения небольших данных в
 * файловой системе, а также напрямую поработаем с {@link java.io.File} для работы с фото,
 * полученного с камеры.
 * </p>
 */
public class Lab4Activity extends AppCompatActivity {

    private static final int REQUEST_STUDENT_ADD = 1;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab4Activity.class);
    }

    private StudentDao studentDao;

    private RecyclerView list;
    private FloatingActionButton fab;

    private StudentsAdapter studentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Получаем объект для выполнения запросов к БД. См. Lab4Database.
         */
        studentDao = Lab4Database.getInstance(this).studentDao();

        setTitle(getString(R.string.lab4_title, getClass().getSimpleName()));

        setContentView(R.layout.lab4_activity);
        list = findViewById(android.R.id.list);
        fab = findViewById(R.id.fab);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        // Точно такой же список, как и в lab3, но с добавленным выводом фото
        list.setAdapter(studentsAdapter = new StudentsAdapter());
        studentsAdapter.setStudents(studentDao.getAll());

        fab.setOnClickListener(
                v -> startActivityForResult(
                        AddStudentActivity.newIntent(this),
                        REQUEST_STUDENT_ADD
                )
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_STUDENT_ADD && resultCode == RESULT_OK) {
            Student student = AddStudentActivity.getResultStudent(data);

            studentDao.insert(student);

            studentsAdapter.setStudents(studentDao.getAll());
            studentsAdapter.notifyItemRangeInserted(studentsAdapter.getItemCount() - 2, 2);
            list.scrollToPosition(studentsAdapter.getItemCount() - 1);
        }
    }
}
