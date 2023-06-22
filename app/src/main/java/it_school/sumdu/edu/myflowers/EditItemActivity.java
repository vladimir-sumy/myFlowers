package it_school.sumdu.edu.myflowers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class EditItemActivity extends AppCompatActivity {
    private static final int IMAGE_SELECTION_REQUEST_CODE = 2;
    private Flower flower;
    private EditText flowerNameEditText;
    private ImageView flowerImageView;
    private EditText flowerDescriptionEditText;
    private EditText flowerWidthEditText;
    private EditText flowerHeightEditText;
    private EditText flowerCountEditText;
    private EditText flowerPurchaseDateEditText;
    private Button uploadPhotoButton;
    private Button saveButton;
    private Button cancelButton;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Initialize views and variables
        dbHelper = new DbHelper(this);

        // Retrieve the flower object from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("flower")) {
            flower = intent.getParcelableExtra("flower");
        } else {
            Toast.makeText(this, "Failed to retrieve flower data", Toast.LENGTH_SHORT).show();
            finish();
        }

        flowerNameEditText = findViewById(R.id.flowerNameEditText);
        flowerImageView = findViewById(R.id.flowerImageView);
        flowerDescriptionEditText = findViewById(R.id.flowerDescriptionEditText);
        flowerWidthEditText = findViewById(R.id.flowerWidthEditText);
        flowerHeightEditText = findViewById(R.id.flowerHeightEditText);
        flowerCountEditText = findViewById(R.id.flowerCountEditText);
        flowerPurchaseDateEditText = findViewById(R.id.flowerPurchaseDateEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Set the initial values in the edit fields
        flowerNameEditText.setText(flower.getName());
        flowerDescriptionEditText.setText(flower.getDescription());
        flowerWidthEditText.setText(String.valueOf(flower.getWidth()));
        flowerHeightEditText.setText(String.valueOf(flower.getHeight()));
        flowerCountEditText.setText(String.valueOf(flower.getCount()));
        flowerPurchaseDateEditText.setText(flower.getDateOfPurchase());

        // Retrieve the image URI from the intent
        Uri imageUri = flower.getImageUri();

        // Load the image into the ImageView using Picasso or another image loading library
        Picasso.get().load(imageUri).into(flowerImageView);

        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the upload photo button click event
                openImageSelection();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEdit();
            }
        });
    }

    private void openImageSelection() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_SELECTION_REQUEST_CODE);
    }


    private void saveChanges() {
        // Get the updated values from the edit fields
        String name = flowerNameEditText.getText().toString();
        String description = flowerDescriptionEditText.getText().toString();
        float width = Float.parseFloat(flowerWidthEditText.getText().toString());
        float height = Float.parseFloat(flowerHeightEditText.getText().toString());
        int count = Integer.parseInt(flowerCountEditText.getText().toString());
        String purchaseDate = flowerPurchaseDateEditText.getText().toString();

        // Update the flower object with the new values
        flower.setName(name);
        flower.setDescription(description);
        flower.setWidth(width);
        flower.setHeight(height);
        flower.setCount(count);
        flower.setDateOfPurchase(purchaseDate);

        // Save the edited flower to the database
        dbHelper.updateFlower(flower);

        Toast.makeText(EditItemActivity.this, "Flower edited successfully", Toast.LENGTH_SHORT).show();

        // Return the updated flower object back to the calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedFlower", flower);
        resultIntent.putExtra("isEdited", true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void cancelEdit() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
