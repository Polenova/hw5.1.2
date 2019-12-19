package ru.android.polenova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    public static final int IMAGE_RESULT_CODE = 543;
    private static final String IMAGE_RESULT_KEY = "IMAGE_RESULT_KEY";

    public static Bitmap getImageFromIntent(@NonNull Intent intent) {
        File imageFile = (File) intent.getSerializableExtra(IMAGE_RESULT_KEY);
        return BitmapFactory.decodeFile(Objects.requireNonNull(imageFile).getAbsolutePath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        boolean granted = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            checkExternalStorageReadable();
        } else {
            requestPermissions();
        }
    }

    private void checkExternalStorageReadable() {
        if (isExternalStorageReadable()) {
            setup();
        } else {
            Toast.makeText(this, "Внешнее хранилище недоступно", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkExternalStorageReadable();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Без разрешения функция не будет доступна", Toast.LENGTH_SHORT).show();
                requestPermissions();
            } else {
                Toast.makeText(this, "Без разрешения функция не доступна! Включите в настройках разрешение", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    private void setup() {
        Button buttonOK = findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textViewImage = findViewById(R.id.EditTextNameImage);
                String picturesName = textViewImage.getText().toString();
                final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), picturesName);
                if (file.exists()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(IMAGE_RESULT_KEY, file);
                    setResult(IMAGE_RESULT_CODE, resultIntent);
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, "Такого файла нет!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}