package com.example.pz_17_photoedit;

import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static Bitmap selectedImage;
    private static final int PICK_IMAGE = 01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void PickPhoto(View view) {
        //вызывает стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        //запускаем переход с ожиданием обратного результата в виде информации об изображении
        startActivityForResult(photoPickerIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE ) {
            try {
                //получаем URI изображения, преобразуем его в Bitmap
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                Intent intent = new Intent(MainActivity.this, EditImageActivity.class);
                startActivity(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}