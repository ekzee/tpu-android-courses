package ru.tpu.courses.lab4.add;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BitmapProcessor {
    private final Context context;

    public BitmapProcessor(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    public File createTempFile() throws IOException {
        String fileName = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(new Date());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                fileName,
                ".jpg",
                storageDir
        );
    }

    /**
     * Считывает картинку по указанному пути, при этом уменьшая её размер таким образом, чтобы
     * ширина и высота были не больше указанных maxWidth и maxHeight.
     */
    @NonNull
    public Bitmap readScaledBitmap(String filePath, int maxWidth, int maxHeight) {
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

    public void saveBitmapToFile(
            @NonNull Bitmap bitmap,
            @NonNull String filePath
    ) throws IOException {
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

    @NonNull
    public Bitmap rotateBitmapIfNeed(
            @NonNull Bitmap bitmap,
            @NonNull String filePath
    ) throws IOException {
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
