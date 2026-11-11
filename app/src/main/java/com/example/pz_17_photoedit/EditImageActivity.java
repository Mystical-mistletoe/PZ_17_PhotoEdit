package com.example.pz_17_photoedit;

import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.Toast;

import java.io.OutputStream;

public class EditImageActivity extends AppCompatActivity {
    Bitmap selectedImage;
    ImageView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        Init();
    }

    private void Init() {
        photoView = findViewById(R.id.imageView);
        selectedImage = MainActivity.selectedImage;
        photoView.setImageBitmap(selectedImage);
    }

    public void RotateLeft(View view)
    { applyMatrix(-90, 1, 1); }
    public void RotateRight(View view)
    { applyMatrix(90, 1, 1); }
    public void ReflectHorizontal(View view)
    { applyMatrix(0, -1, 1); }
    public void ReflectVertical(View view)
    { applyMatrix(0, 1, -1); }

    //универсальный для изменения
    private void applyMatrix(float degrees, float scaleX, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        matrix.postScale(scaleX, scaleY);

        Bitmap result = Bitmap.createBitmap(
                selectedImage, 0, 0,
                selectedImage.getWidth(),
                selectedImage.getHeight(),
                matrix, true);

        selectedImage = result;
        photoView.setImageBitmap(selectedImage);
    }


    //чб
    public void MakeBlackAndWhite(View view) {
        Bitmap bwBitmap = Bitmap.createBitmap(selectedImage.getWidth(),
                selectedImage.getHeight(), selectedImage.getConfig());
        Canvas canvas = new Canvas(bwBitmap);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(selectedImage, 0, 0, paint);

        selectedImage = bwBitmap;
        photoView.setImageBitmap(selectedImage);
        Toast.makeText(this, "Применено ЧБ", Toast.LENGTH_SHORT).show();
    }


    //сохранение

    public void SaveImage(View view) {
        try {
            String fileName = "PhotoEdit_" + System.currentTimeMillis() + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoEdit");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                if (outputStream != null) {
                    outputStream.close();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    getContentResolver().update(uri, values, null, null);
                }
                Toast.makeText(this, "Сохранено в галерею!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }
}