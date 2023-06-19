package it_school.sumdu.edu.myflowers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "flower_catalog.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_FLOWERS = "flowers";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_WIDTH = "width";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_DATE_OF_PURCHASE = "date_of_purchase";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FLOWERS_TABLE = "CREATE TABLE " + TABLE_FLOWERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_IMAGE + " TEXT,"
                + COLUMN_WIDTH + " INTEGER,"
                + COLUMN_HEIGHT + " INTEGER,"
                + COLUMN_COUNT + " INTEGER,"
                + COLUMN_DATE_OF_PURCHASE + " TEXT" + ")";
        db.execSQL(CREATE_FLOWERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOWERS);
        onCreate(db);
    }

    public void addFlower(Flower flower) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, flower.getName());
        values.put(COLUMN_DESCRIPTION, flower.getDescription());
        values.put(COLUMN_IMAGE, flower.getImage());
        values.put(COLUMN_WIDTH, flower.getWidth());
        values.put(COLUMN_HEIGHT, flower.getHeight());
        values.put(COLUMN_COUNT, flower.getCount());
        values.put(COLUMN_DATE_OF_PURCHASE, flower.getDateOfPurchase());
        db.insert(TABLE_FLOWERS, null, values);
        db.close();
    }


    public List<Flower> getAllFlowers() {
        List<Flower> flowerList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FLOWERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE));
                int width = cursor.getInt(cursor.getColumnIndex(COLUMN_WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(COLUMN_HEIGHT));
                int count = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT));
                String dateOfPurchase = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_OF_PURCHASE));
                Flower flower = new Flower(id, name, description, image, width, height, count, dateOfPurchase);
                flowerList.add(flower);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return flowerList;
    }

    public void deleteFlower(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLOWERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
