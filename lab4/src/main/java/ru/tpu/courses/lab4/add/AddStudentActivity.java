package ru.tpu.courses.lab4.add;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

import ru.tpu.courses.lab4.Const;
import ru.tpu.courses.lab4.R;
import ru.tpu.courses.lab4.db.Lab4Database;
import ru.tpu.courses.lab4.db.Student;
import ru.tpu.courses.lab4.db.StudentDao;

/**
 * Аналогичный экран ввода информации о студенте, как и в lab3. Но теперь введенная информация
 * сохраняется в {@link android.content.SharedPreferences} (см {@link TempStudentPref}), что
 * позволяет восстановить введенную информацию после ухода и возвращения на экран. Также теперь
 * можно добавить фотографию через приложение камеры. Для работы с картинками см
 * {@link BitmapProcessor}.
 */
public class AddStudentActivity extends AppCompatActivity {

    private static final String EXTRA_STUDENT = "student";

    private static final int REQUEST_CAMERA = 0;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, AddStudentActivity.class);
    }

    public static Student getResultStudent(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_STUDENT);
    }

    private StudentDao studentDao;

    private TempStudentPref studentPref;
    private BitmapProcessor bitmapProcessor;

    private EditText firstName;
    private EditText secondName;
    private EditText lastName;
    private ImageView photo;

    private String photoPath;

    private boolean skipSaveToPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab4_activity_add_student);

        studentPref = new TempStudentPref(this);
        bitmapProcessor = new BitmapProcessor(this);
        studentDao = Lab4Database.getInstance(this).studentDao();

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = findViewById(R.id.first_name);
        secondName = findViewById(R.id.second_name);
        lastName = findViewById(R.id.last_name);
        photo = findViewById(R.id.photo);

        firstName.setText(studentPref.getFirstName());
        secondName.setText(studentPref.getSecondName());
        lastName.setText(studentPref.getLastName());
        photoPath = studentPref.getPhotoPath();
        if (photoPath != null) {
            photo.setImageURI(Uri.parse(photoPath));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!skipSaveToPrefs) {
            studentPref.set(
                    firstName.getText().toString(),
                    secondName.getText().toString(),
                    lastName.getText().toString(),
                    photoPath
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lab4_add_student, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            saveStudent();
            return true;
        }

        if (item.getItemId() == R.id.action_add_photo) {
            requestPhotoFromCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                Bitmap scaledPhoto = bitmapProcessor.readScaledBitmap(photoPath, 512, 512);
                scaledPhoto = bitmapProcessor.rotateBitmapIfNeed(scaledPhoto, photoPath);
                bitmapProcessor.saveBitmapToFile(scaledPhoto, photoPath);

                photo.setImageURI(Uri.parse(photoPath));
            } catch (IOException e) {
                Toast.makeText(this, "Не удалось получить фото", Toast.LENGTH_SHORT).show();
                photoPath = null;
                photo.setImageURI(null);
                e.printStackTrace();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Intent requestPhotoIntent(Uri photoFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
        return intent;
    }

    private void requestPhotoFromCamera() {
        try {
            if (!TextUtils.isEmpty(photoPath)) {
                new File(photoPath).delete();
            }
            File tempFile = bitmapProcessor.createTempFile();
            photoPath = tempFile.getPath();
            Uri photoUri = FileProvider.getUriForFile(
                    this,
                    Const.FILE_PROVIDER_AUTHORITY,
                    tempFile
            );
            Intent requestPhotoIntent = requestPhotoIntent(photoUri);
            startActivityForResult(requestPhotoIntent, REQUEST_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Не удалось создать файл для фотографии", Toast.LENGTH_SHORT
            ).show();
            photoPath = null;
        }
    }

    private void saveStudent() {
        Student student = new Student(
                firstName.getText().toString(),
                secondName.getText().toString(),
                lastName.getText().toString(),
                photoPath
        );

        // Проверяем, что все поля были указаны
        if (TextUtils.isEmpty(student.firstName) ||
                TextUtils.isEmpty(student.secondName) ||
                TextUtils.isEmpty(student.lastName)) {
            // Класс Toast позволяет показать системное уведомление поверх всего UI
            Toast.makeText(this, R.string.lab4_error_empty_fields, Toast.LENGTH_LONG).show();
            return;
        }

        if (studentDao.count(student.firstName, student.secondName, student.lastName) > 0) {
            Toast.makeText(
                    this,
                    R.string.lab4_error_already_exists,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        skipSaveToPrefs = true;

        studentPref.clear();

        Intent data = new Intent();
        data.putExtra(EXTRA_STUDENT, student);
        setResult(RESULT_OK, data);
        finish();
    }
}
