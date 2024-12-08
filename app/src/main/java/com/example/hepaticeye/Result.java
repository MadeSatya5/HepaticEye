package com.example.hepaticeye;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Result extends AppCompatActivity {

    TextView tvpercentage;
    TextView tvClassResult;
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
        photo = (ImageView) findViewById(R.id.foto);

        if (uriResult != null) {
            Uri imageUri = Uri.parse(uriResult);
            // Load the image into the ImageView
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
            photo.setImageBitmap(bitmap);
        }

        if (classNameResult != null && percentageResult != 0) {
            if("Jaundiced".equals(classNameResult)){
                tvClassResult.setText("Kemungkinan gejala mata Hepatitis");
                String formattedResult = String.format("%.2f%%", percentageResult * 100);
                tvpercentage.setText(formattedResult);
            }
            else if("Normal".equals(classNameResult)){
                tvpercentage.setText("Mata Normal");
            }
        }
        else {
            tvpercentage.setText("Tidak Terdeteksi");
        }
    }

    public static void getResult(String className, double confidence){
        classNameResult = className;
        percentageResult = confidence;
    }

    public static void getUri(String uri){
        uriResult = uri;
    }
}
