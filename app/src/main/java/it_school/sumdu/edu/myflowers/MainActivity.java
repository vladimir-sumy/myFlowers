package it_school.sumdu.edu.myflowers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView flowerRecyclerView;
    private Button addButton;
    private ItemAdapter itemAdapter;
    private List<Flower> flowerList;

    private static final int ADD_ITEM_REQUEST_CODE = 1;
    public static final int VIEW_ITEM_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchEditText);
        flowerRecyclerView = findViewById(R.id.flowerRecyclerView);
        addButton = findViewById(R.id.addButton);

        flowerList = new ArrayList<>(); // Initialize with your flower data from SQLite database
        populateFlowerList(); // Populate the flowerList with data from the database

        itemAdapter = new ItemAdapter(this, flowerList);
        flowerRecyclerView.setAdapter(itemAdapter);
        flowerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                itemAdapter.filterList(query);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddItemActivity();
            }
        });
    }

    private void populateFlowerList() {
        // Fetch flower data from the SQLite database and add it to the flowerList
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "id",
                "name",
                "description",
                "image",
                "width",
                "height",
                "count",
                "date_of_purchase"
        };

        Cursor cursor = db.query(
                "flowers",
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
            float width = cursor.getFloat(cursor.getColumnIndexOrThrow("width"));
            float height = cursor.getFloat(cursor.getColumnIndexOrThrow("height"));
            int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
            String dateOfPurchase = cursor.getString(cursor.getColumnIndexOrThrow("date_of_purchase"));

            Flower flower = new Flower(id, name, description, image, width, height, count, dateOfPurchase);
            flowerList.add(flower);
        }

        cursor.close();
    }


    private void openAddItemActivity() {
        Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
        startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
    }

    private void openViewItemActivity(Flower flower) {
        Intent intent = new Intent(MainActivity.this, ViewItemActivity.class);
        intent.putExtra("flower", flower);
        startActivityForResult(intent, VIEW_ITEM_REQUEST_CODE);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Flower newFlower = data.getParcelableExtra("newFlower");
            if (newFlower != null) {
                flowerList.add(newFlower);
                itemAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == VIEW_ITEM_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Flower deletedFlower = data.getParcelableExtra("deletedFlower");
            if (deletedFlower != null) {
                flowerList.remove(deletedFlower);
                itemAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Flower deleted", Toast.LENGTH_SHORT).show();
                itemAdapter.filterList("");
            }
        }
        itemAdapter.filterList("");

    }

}

