package com.example.assignment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.assignment.logic.IngredientNormalizer;
import com.example.assignment.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the SQLite database and provides CRUD methods for pantry items.
 *
 * A single shared instance is used (getInstance) so the whole app works with
 * one database connection instead of opening a new one on every screen.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry_db.db";
    // Increase this number whenever the table structure changes
    private static final int DATABASE_VERSION = 1;

    // Table and column names (kept as constants to avoid typos)
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_NORMALIZED_NAME = "normalized_name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY_DATE = "expiry_date";
    public static final String COL_STEPS = "steps";
    public static final String COL_RECIPE_ID = "recipe_id";

    //CREATE TABLE statements:
    private static final String SQL_CREATE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL, "
                    + COL_NORMALIZED_NAME + " TEXT NOT NULL, "
                    + COL_QUANTITY + " REAL NOT NULL CHECK (" + COL_QUANTITY + " > 0), "
                    + COL_UNIT + " TEXT NOT NULL, "
                    + COL_EXPIRY_DATE + " TEXT)";

    private static final String SQL_CREATE_RECIPES =
            "CREATE TABLE " + TABLE_RECIPES + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL UNIQUE, "
                    + COL_STEPS + " TEXT NOT NULL)";

    private static final String SQL_CREATE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RECIPE_ID + " INTEGER NOT NULL, "
                    + COL_NAME + " TEXT NOT NULL, "
                    + COL_NORMALIZED_NAME + " TEXT NOT NULL, "
                    + COL_QUANTITY + " REAL NOT NULL CHECK (" + COL_QUANTITY + " > 0), "
                    + COL_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + COL_RECIPE_ID + ") REFERENCES "
                    + TABLE_RECIPES + "(" + COL_ID + ") ON DELETE CASCADE)";

    private static DatabaseHelper instance;

    // Returns the single shared DatabaseHelper for the whole app.
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            // Application context avoids leaking an Activity
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // SQLite ignores FOREIGN KEY rules unless they are switched on
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Runs only once: the first time the app opens the database on a device.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PANTRY);
        db.execSQL(SQL_CREATE_RECIPES);
        db.execSQL(SQL_CREATE_RECIPE_INGREDIENTS);
        db.execSQL("CREATE INDEX idx_pantry_normalized_name ON "
                + TABLE_PANTRY + "(" + COL_NORMALIZED_NAME + ")");
        db.execSQL("CREATE INDEX idx_recipe_ingredients_recipe_id ON "
                + TABLE_RECIPE_INGREDIENTS + "(" + COL_RECIPE_ID + ")");

        // Load the built-in recipe collection on first run
        RecipeSeeder.seed(db);
    }

    /**
     * Runs when DATABASE_VERSION is increased. During development the simplest
     * approach is to drop and rebuild the tables. Note: this also deletes pantry data.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }


    //  PANTRY CRUD:

    // CREATE: saves a new pantry item. Returns the new row id, or -1 on failure.
    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        long newId = db.insert(TABLE_PANTRY, null, toContentValues(item));
        item.setId(newId);
        return newId;
    }

    // READ: returns every pantry item, sorted alphabetically.
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // try-with-resources closes the cursor automatically
        try (Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                items.add(cursorToPantryItem(cursor));
            }
        }
        return items;
    }

    //READ: returns one pantry item by id, or null if it doesn't exist.
    public PantryItem getPantryItemById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PANTRY, null,
                COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursorToPantryItem(cursor);
            }
        }
        return null;
    }

    // UPDATE: saves changes to an existing item. Returns true if a row was changed.
    public boolean updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsChanged = db.update(TABLE_PANTRY, toContentValues(item),
                COL_ID + " = ?", new String[]{String.valueOf(item.getId())});
        return rowsChanged > 0;
    }

    // DELETE: removes an item by id. Returns true if a row was deleted.
    public boolean deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PANTRY,
                COL_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsDeleted > 0;
    }


    //HELPERS:


    /**
     * Converts a PantryItem into column/value pairs. The normalized name is
     * calculated here so it can never be forgotten by the screens that save items.
     */
    private ContentValues toContentValues(PantryItem item) {
        String cleanName = item.getName().trim();
        item.setNormalizedName(IngredientNormalizer.normalize(cleanName));

        ContentValues values = new ContentValues();
        values.put(COL_NAME, cleanName);
        values.put(COL_NORMALIZED_NAME, item.getNormalizedName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        if (item.getExpiryDate() == null) {
            values.putNull(COL_EXPIRY_DATE);
        } else {
            values.put(COL_EXPIRY_DATE, item.getExpiryDate());
        }
        return values;
    }

    // Builds a PantryItem from the row the cursor is currently on.
    private PantryItem cursorToPantryItem(Cursor cursor) {
        int expiryIndex = cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE);
        return new PantryItem(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NORMALIZED_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)),
                cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex));
    }
}
