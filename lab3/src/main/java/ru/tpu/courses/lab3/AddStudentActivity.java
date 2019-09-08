package ru.tpu.courses.lab3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    private static final String EXTRA_STUDENT = "student";

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, AddStudentActivity.class);
    }

    public static Student getResultStudent(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_STUDENT);
    }

    private final StudentsCache studentsCache = StudentsCache.getInstance();

    private EditText firstName;
    private EditText secondName;
    private EditText lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab3_activity_add_student);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = findViewById(R.id.first_name);
        secondName = findViewById(R.id.second_name);
        lastName = findViewById(R.id.last_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lab3_add_student, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            Student student = new Student(
                    firstName.getText().toString(), secondName.getText().toString(), lastName.getText().toString()
            );

            if (studentsCache.contains(student)) {
                Toast.makeText(this, R.string.lab3_error_already_exists, Toast.LENGTH_LONG).show();
                return true;
            }

            Intent data = new Intent();
            data.putExtra(EXTRA_STUDENT, student);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
