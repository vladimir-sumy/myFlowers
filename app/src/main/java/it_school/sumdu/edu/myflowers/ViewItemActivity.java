package it_school.sumdu.edu.myflowers;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewItemActivity extends AppCompatActivity {
    private static final int EDIT_ITEM_REQUEST_CODE = 1;

    private Flower flower;
    private TextView flowerNameTextView;
    private ImageView flowerImageView;
    private TextView flowerDescriptionTextView;
    private TextView flowerWidthTextView;
    private TextView flowerHeightTextView;
    private TextView flowerCountTextView;
    private TextView flowerPurchaseDateTextView;
    private Button returnToListButton;
    private Button editButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        // Retrieve the flower object from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("flower")) {
            flower = intent.getParcelableExtra("flower");
        } else {
            Toast.makeText(this, "Failed to retrieve flower data", Toast.LENGTH_SHORT).show();
            finish();
        }

        flowerNameTextView = findViewById(R.id.flowerNameTextView);
        flowerImageView = findViewById(R.id.flowerImageView);
        flowerDescriptionTextView = findViewById(R.id.flowerDescriptionTextView);
        flowerWidthTextView = findViewById(R.id.flowerWidthTextView);
        flowerHeightTextView = findViewById(R.id.flowerHeightTextView);
        flowerCountTextView = findViewById(R.id.flowerCountTextView);
        flowerPurchaseDateTextView = findViewById(R.id.flowerPurchaseDateTextView);
        returnToListButton = findViewById(R.id.returnToListButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Update the UI with the flower data
        updateUI();

        returnToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToList();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditItemActivity();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFlower();
            }
        });
    }

    private void returnToList() {
        int position = getIntent().getIntExtra("position", -1);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isEdited", true);
        resultIntent.putExtra("position", position);
        resultIntent.putExtra("editedFlower", flower);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateUI() {
        // Update the UI with the flower data
        flowerNameTextView.setText(flower.getName());
        // Load the flower image using an image loading library or custom logic
        Picasso.get().load(flower.getImageUri()).into(flowerImageView);
        flowerDescriptionTextView.setText(flower.getDescription());
        flowerWidthTextView.setText(String.valueOf(flower.getWidth()));
        flowerHeightTextView.setText(String.valueOf(flower.getHeight()));
        flowerCountTextView.setText(String.valueOf(flower.getCount()));
        flowerPurchaseDateTextView.setText(flower.getDateOfPurchase());
    }

    private void openEditItemActivity() {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("flower", flower);
        startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
    }

    private void deleteFlower() {
        int position = getIntent().getIntExtra("position", -1);
        // Delete the flower from the SQLite database
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.deleteFlower(flower.getId());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("position", position);
        resultIntent.putExtra("deletedFlower", flower);
        resultIntent.putExtra("isDeleted", true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ITEM_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Extract any updated data from the result intent
            Flower updatedFlower = data.getParcelableExtra("updatedFlower");
            if (updatedFlower != null) {
                // Update the flower object or refresh the data as needed
                flower = updatedFlower;
                // Update the UI with the new flower data
                updateUI();
            }
        }
    }


}

