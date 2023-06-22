package it_school.sumdu.edu.myflowers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView flowerImageView;
    private Button uploadButton;
    private EditText flowerNameEditText;
    private EditText flowerDescriptionEditText;
    private EditText flowerWidthEditText;
    private EditText flowerHeightEditText;
    private EditText flowerCountEditText;
    private EditText flowerPurchaseDateEditText;
    private Button saveButton;
    private Button cancelButton;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        flowerImageView = findViewById(R.id.flowerImageView);
        uploadButton = findViewById(R.id.uploadButton);
        flowerNameEditText = findViewById(R.id.flowerNameEditText);
        flowerDescriptionEditText = findViewById(R.id.flowerDescriptionEditText);
        flowerWidthEditText = findViewById(R.id.flowerWidthEditText);
        flowerHeightEditText = findViewById(R.id.flowerHeightEditText);
        flowerCountEditText = findViewById(R.id.flowerCountEditText);
        flowerPurchaseDateEditText = findViewById(R.id.flowerPurchaseDateEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Set the default value as the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        flowerPurchaseDateEditText.setText(currentDate);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFlower();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                flowerImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFlower() {
        // Check if an image has been selected
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values from EditText fields
        String flowerName = flowerNameEditText.getText().toString().trim();
        String flowerDescription = flowerDescriptionEditText.getText().toString().trim();
        String flowerWidth = flowerWidthEditText.getText().toString().trim();
        String flowerHeight = flowerHeightEditText.getText().toString().trim();
        String flowerCount = flowerCountEditText.getText().toString().trim();
        String flowerPurchaseDate = flowerPurchaseDateEditText.getText().toString().trim();

        // Validate input fields
        if (flowerName.isEmpty() || flowerWidth.isEmpty() || flowerHeight.isEmpty() || flowerCount.isEmpty() || flowerPurchaseDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the flower to the database
        DbHelper dbHelper = new DbHelper(this);
        ContentResolver contentResolver = getContentResolver();

        ContentValues values = new ContentValues();
        values.put("name", flowerName);
        values.put("description", flowerDescription);
        values.put("width", Float.parseFloat(flowerWidth));
        values.put("height", Float.parseFloat(flowerHeight));
        values.put("count", Integer.parseInt(flowerCount));
        values.put("date_of_purchase", flowerPurchaseDate);

        // Save the image to the media store and get the image URI
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "flower_" + timeStamp + ".jpg";

        Uri imageUri = null;
        try {
            imageUri = saveImageToMediaStore(contentResolver, selectedImageUri, imageFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageUri != null) {
            values.put("image", imageUri.toString());
        }

        // Insert the flower into the database
        long newRowId = dbHelper.getWritableDatabase().insert("flowers", null, values);

        Flower newFlower = new Flower( (int)newRowId, flowerName, flowerDescription, imageUri.toString(),Float.parseFloat(flowerWidth),Float.parseFloat(flowerHeight),Integer.parseInt(flowerCount),flowerPurchaseDate);
        // Set other attribute values as needed

        // Create the result intent and set the newFlower object as an extra
        Intent resultIntent = new Intent();
        resultIntent.putExtra("newFlower", newFlower);

        // Set the result code as RESULT_OK and pass the result intent
        setResult(RESULT_OK, resultIntent);

        if (newRowId == -1) {
            Toast.makeText(this, "Failed to add flower", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Flower added", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private Uri saveImageToMediaStore(ContentResolver contentResolver, Uri sourceUri, String fileName) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri targetUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (targetUri != null) {
            OutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = contentResolver.openOutputStream(targetUri);
                inputStream = contentResolver.openInputStream(sourceUri);
                if (outputStream != null && inputStream != null) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }

        return targetUri;
    }

}
