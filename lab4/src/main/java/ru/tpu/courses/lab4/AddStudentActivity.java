package ru.tpu.courses.lab4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddStudentActivity extends AppCompatActivity {

    private static final String EXTRA_STUDENT = "student";

    private static final String PREF_FIRST_NAME = "first_name";
    private static final String PREF_SECOND_NAME = "second_name";
    private static final String PREF_LAST_NAME = "last_name";
    private static final String PREF_PHOTO_PATH = "photo_path";

    private static final int REQUEST_CAMERA = 0;

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
    private ImageView photo;

    private String photoPath;

    private boolean skipSaveToPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab4_activity_add_student);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = findViewById(R.id.first_name);
        secondName = findViewById(R.id.second_name);
        lastName = findViewById(R.id.last_name);
        photo = findViewById(R.id.photo);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        firstName.setText(prefs.getString(PREF_FIRST_NAME, ""));
        secondName.setText(prefs.getString(PREF_SECOND_NAME, ""));
        lastName.setText(prefs.getString(PREF_LAST_NAME, ""));
        photoPath = prefs.getString(PREF_PHOTO_PATH, null);
        if (photoPath != null) {
            photo.setImageURI(Uri.parse(photoPath));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!skipSaveToPrefs) {
            saveInfoInPrefs();
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
            Student student = new Student(
                    firstName.getText().toString(),
                    secondName.getText().toString(),
                    lastName.getText().toString(),
                    null
            );

            if (studentsCache.contains(student)) {
                Toast.makeText(this, R.string.lab4_error_already_exists, Toast.LENGTH_LONG).show();
                return true;
            }

            skipSaveToPrefs = true;

            Intent data = new Intent();
            data.putExtra(EXTRA_STUDENT, student);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_add_photo) {
            try {
                File tempFile = createTempFile();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            photo.setImageURI(Uri.parse(photoPath));
            try {
                Bitmap scaledPhoto = getScaledBitmap(photoPath, 512, 512);
                scaledPhoto = rotateBitmapIfNeed(scaledPhoto, photoPath);
                saveBitmapToFile(scaledPhoto, photoPath);

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

    private void saveInfoInPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putString(PREF_FIRST_NAME, firstName.getText().toString())
                .putString(PREF_LAST_NAME, lastName.getText().toString())
                .putString(PREF_SECOND_NAME, secondName.getText().toString())
                .putString(PREF_PHOTO_PATH, photoPath)
                .apply();
    }

    private Intent requestPhotoIntent(Uri photoFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
        return intent;
    }

    private File createTempFile() throws IOException {
        String fileName = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                fileName,
                ".jpg",
                storageDir
        );
    }

    private Bitmap getScaledBitmap(String filePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
        if (scaleFactor < 1) {
            scaleFactor = 1;
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int) scaleFactor;

        return BitmapFactory.decodeFile(filePath, bmOptions);
    }

    private void saveBitmapToFile(Bitmap bitmap, String filePath) throws IOException {
        File file = new File(filePath);
        OutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        } finally {
            if (fOut != null) {
                fOut.close();
            }
        }
    }

    private Bitmap rotateBitmapIfNeed(Bitmap bitmap, String filePath) throws IOException {
        ExifInterface exif = new ExifInterface(filePath);
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
        );
        int rotateBy = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotateBy = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotateBy = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotateBy = 270;
                break;
        }
        if (rotateBy == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateBy);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true
        );
        bitmap.recycle();
        return newBitmap;
    }
}
