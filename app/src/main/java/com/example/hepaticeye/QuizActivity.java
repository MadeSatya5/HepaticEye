package com.example.hepaticeye;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import java.util.Arrays;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private String[] answers = new String[8];

    private void setupSingleChoiceButtons(int questionIndex, AppCompatButton... buttons) {
        List<AppCompatButton> btnList = Arrays.asList(buttons);
        for (AppCompatButton btn : btnList) {
            btn.setOnClickListener(v -> {
                for (AppCompatButton b : btnList) {
                    b.setBackgroundResource(R.drawable.button_border); // default
                }
                btn.setBackgroundResource(R.drawable.button_rounded); // selected
                answers[questionIndex] = btn.getText().toString(); // simpan jawaban
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        Button berikutnya = findViewById(R.id.berikutnya);
        berikutnya.setOnClickListener(v -> {
            for (int i = 0; i < answers.length; i++) {
                if (answers[i] == null) {
                    Toast.makeText(this, "Silakan isi semua pertanyaan sebelum lanjut.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            double quizScore = getQuizScore();

            // Simpan ke SharedPreferences
            SharedPreferences prefs = getSharedPreferences("HepaticPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("quizScore", (float) quizScore); // simpan sebagai float
            editor.apply(); // commit perubahan

            Intent intent = new Intent(QuizActivity.this, AmbilFoto.class);
            startActivity(intent);
        });

        // Question 1
        setupSingleChoiceButtons(0,
                findViewById(R.id.q1_ya),
                findViewById(R.id.q1_tidak));

        // Question 2
        setupSingleChoiceButtons(1,
                findViewById(R.id.q2_meningkat),
                findViewById(R.id.q2_stagnan),
                findViewById(R.id.q2_naikturun),
                findViewById(R.id.q2_tidaktahu));

        // Question 3
        setupSingleChoiceButtons(2,
                findViewById(R.id.q3_ya),
                findViewById(R.id.q3_tidak));

        // Question 4
        setupSingleChoiceButtons(3,
                findViewById(R.id.q4_ya),
                findViewById(R.id.q4_tidak));

        // Question 5
        setupSingleChoiceButtons(4,
                findViewById(R.id.q5_ya),
                findViewById(R.id.q5_tidak));

        // Question 6
        setupSingleChoiceButtons(5,
                findViewById(R.id.q6_ya),
                findViewById(R.id.q6_tidak));

        // Question 7
        setupSingleChoiceButtons(6,
                findViewById(R.id.q7_ya),
                findViewById(R.id.q7_tidak));

        // Question 8
        setupSingleChoiceButtons(7,
                findViewById(R.id.q8_ya),
                findViewById(R.id.q8_tidak));
    }

    private double getQuizScore() {
        double score = 0;

        // Soal 1
        if ("Ya".equalsIgnoreCase(answers[0])) score += 1;

        // Soal 2
        if ("meningkat".equalsIgnoreCase(answers[1])) score += 2;
        else if (!"tidaktahu".equalsIgnoreCase(answers[1])) score += 1;

        // Soal 3–8
        for (int i = 2; i < answers.length; i++) {
            if ("Ya".equalsIgnoreCase(answers[i])) {
                score += 1;
            }
        }

        // Skor maksimal: soal 1 (1) + soal 2 (2) + soal 3–8 (6) = 9
        return score / 9.0; // normalisasi ke 0.0 – 1.0
    }

}
