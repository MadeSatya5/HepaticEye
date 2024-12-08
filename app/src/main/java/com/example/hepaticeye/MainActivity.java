package com.example.hepaticeye;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    Button scan;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan = (Button)findViewById(R.id.scan);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (it.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(it, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the image from the Intent
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            // Save image to file (temporary storage)
            File imageFile = new File(getExternalFilesDir(null), "captured_image.jpg");
            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                imageUri = Uri.fromFile(imageFile);  // Uri pointing to the saved image

                Result.getUri(imageUri.toString()); // Send image captured to result

                Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();

                // Call the method to upload the image
                new InferenceLocal(this, imageFile.getAbsolutePath()).execute();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

class InferenceLocal extends AsyncTask<Void, Void, String> {

    private String imagePath;
    private Context context;

    public InferenceLocal(Context context, String imagePath) {
        this.context = context;
        this.imagePath = imagePath;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String API_KEY = "oAf4wXvRnMj3tm7jWjD6"; // Your API Key
        String MODEL_ENDPOINT = "hepaticeye/1"; // model endpoint

        // File input stream for the image
        File imageFile = new File(imagePath);

        // Construct the URL for API endpoint
        String uploadURL = "https://detect.roboflow.com/" + MODEL_ENDPOINT + "?api_key=" + API_KEY;

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        BufferedReader reader = null;
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        try {
            // Open a URL connection
            URL url = new URL(uploadURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setDoOutput(true);

            outputStream = new DataOutputStream(connection.getOutputStream());

            // Send image as multipart data
            sendFormData(outputStream, boundary, imageFile);

            // Get response from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFormData(DataOutputStream outputStream, String boundary, File imageFile) throws IOException {
        String fileName = imageFile.getName();
        String fileMimeType = "image/jpeg"; // Assuming the image is JPEG; you can adjust this based on your file type

        // Send the form data header
        outputStream.writeBytes("--" + boundary + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n");
        outputStream.writeBytes("Content-Type: " + fileMimeType + "\r\n");
        outputStream.writeBytes("\r\n");

        // Write the file content to the output stream
        FileInputStream fileInputStream = new FileInputStream(imageFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();

        // End the multipart form data
        outputStream.writeBytes("\r\n");
        outputStream.writeBytes("--" + boundary + "--\r\n");
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Handle the result from the API (for example, update the UI)
        if (result != null) {
            // Do something with the response (e.g., display result in the UI)
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray predictionsArray = jsonObject.getJSONArray("predictions");

                if(predictionsArray.length() > 0 ){
                    String className = predictionsArray.getJSONObject(0).getString("class");
                    System.out.println("Class: " + className);

                    double confidence = predictionsArray.getJSONObject(0).getDouble("confidence");
                    System.out.println("Confidence: " + confidence);

                    Result.getResult(className, confidence);
                }
                else{
                    Toast.makeText(context, "No predictions found", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }finally {
                System.out.println("API Response: " + result);
                Intent intent = new Intent(context, Result.class);
                context.startActivity(intent);
            }
        }
        else {
            System.out.println("Error: No response from API");
            Toast.makeText(context, "Error: No response from API", Toast.LENGTH_SHORT).show();
        }
    }
}





