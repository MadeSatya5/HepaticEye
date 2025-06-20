package com.example.hepaticeye;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AmbilFoto extends AppCompatActivity {

    LinearLayout kamera;
    LinearLayout galeri;
    static final int REQUEST_IMAGE_PICK = 2;
    Uri imageUri;

    private final ActivityResultLauncher<Intent> imagePickLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleSelectedImage(result.getData().getData());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foto);

        kamera = findViewById(R.id.btn_kamera);
        galeri = findViewById(R.id.btn_galeri);

        kamera.setOnClickListener(view -> {
            Intent intent = new Intent(AmbilFoto.this, PanduanFoto.class);
            startActivity(intent);
        });

        galeri.setOnClickListener(view -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickLauncher.launch(pickIntent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK){
            handleSelectedImage(data.getData());
        }
    }

    private void handleSelectedImage(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "Invalid image URI", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            File imageFile = new File(getExternalFilesDir(null), "selected_image.jpg");

            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                imageUri = Uri.fromFile(imageFile);

                Result.getUri(imageUri.toString());

                // Tampilkan loading
                setContentView(R.layout.loading);

                new InferenceLocal(this, imageFile.getAbsolutePath()).execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing selected image", Toast.LENGTH_SHORT).show();
        }
    }

}
