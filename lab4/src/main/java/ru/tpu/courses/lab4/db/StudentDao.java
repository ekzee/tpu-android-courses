package ru.tpu.courses.lab4.db;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM student")
    List<Student> getAll();

    @Insert
    void insert(@NonNull Student student);

    @Query(
            "SELECT COUNT(*) FROM student WHERE " +
                    "first_name = :firstName AND " +
                    "second_name = :secondName AND " +
                    "last_name = :lastName"
    )
    int count(@NonNull String firstName, @NonNull String secondName, @NonNull String lastName);
}
