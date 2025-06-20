package com.example.hepaticeye;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class Result extends AppCompatActivity {

    TextView tvpercentage;
    TextView tvClassResult;
    AppCompatButton btnKembali;
    ImageView photo;
    static double percentageResult;
    static String classNameResult;
    static String uriResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hasil);

        tvpercentage = (TextView) findViewById(R.id.persentase);
        tvClassResult = (TextView) findViewById(R.id.hasil);
        AppCompatButton btnKembali = findViewById(R.id.btn_kembali);
        photo = (ImageView) findViewById(R.id.foto);

        if (uriResult != null) {
            Uri imageUri = Uri.parse(uriResult);
            // Load the image into the ImageView
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
            photo.setImageBitmap(bitmap);
        }

        if (classNameResult != null && percentageResult != 0) {
            if("Jaundiced".equals(classNameResult)){
                tvClassResult.setText("Silakan lakukan pemeriksaan di klinik kesehatan terdekat atau dokter tepercaya Anda");
                String formattedResult = String.format("%.2f%%", percentageResult * 100);
                tvpercentage.setText(formattedResult);
                btnKembali.setText("Kembali ke Halaman Utama");

                tvpercentage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);

                btnKembali.setOnClickListener(v -> {
                    Intent intent = new Intent(Result.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            }
            else if("Normal".equals(classNameResult)){
                tvpercentage.setText("Mata Normal");
                tvClassResult.setText("Mata Anda tidak menunjukkan indikasi gejala mata Hepatitis");
                btnKembali.setText("Kembali ke Halaman Utama");

                tvpercentage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);

                btnKembali.setOnClickListener(v -> {
                    Intent intent = new Intent(Result.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }
        else {
            tvpercentage.setText("Gambar Tidak Terdeteksi");
            tvClassResult.setText("Mohon lakukan pengambilan gambar ulang");
            btnKembali.setText("Ambil Ulang Gambar");

            tvpercentage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);

            btnKembali.setOnClickListener(v -> {
                Intent intent = new Intent(Result.this, AmbilFoto.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetResult();
    }

    public static void getResult(String className, double confidence){
        classNameResult = className;
        percentageResult = confidence;
    }

    public static void resetResult() {
        classNameResult = null; // Reset to null
        percentageResult = 0; // Reset to 0
        uriResult = null; // Reset to null
    }

    public static void getUri(String uri){
        uriResult = uri;
    }
}
