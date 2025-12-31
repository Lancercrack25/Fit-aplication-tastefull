package com.example.fat_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FatApp.db";
    // -----------------------------------------------------------------
    // 👇 CAMBIO 1: Versión aumentada para forzar la actualización
    // -----------------------------------------------------------------
    private static final int DATABASE_VERSION = 8;

    // --- Tabla de Usuarios ---
    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_FULLNAME = "fullname";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_WEIGHT = "initial_weight";

    // -----------------------------------------------------------------
    // 👇 CAMBIO 2: Nuevas columnas añadidas
    // -----------------------------------------------------------------
    public static final String COL_AGE = "age";         // Edad (Estatura)
    public static final String COL_GENDER = "gender";   // Sexo
    public static final String COL_HEIGHT = "height";   // Estatura

    // --- Tabla de sueño ---
    public static final String TABLE_SLEEP = "sleep";
    public static final String COL_SLEEP_ID = "sleep_id";
    public static final String COL_SLEEP_USER_ID = "user_id";
    public static final String COL_SLEEP_DATE = "date";
    public static final String COL_SLEEP_HOURS = "hours";

    // --- Tabla de Comida ---
    public static final String TABLE_FOOD_ENTRIES = "food_entries";
    public static final String COL_ENTRY_ID = "entry_id";
    public static final String COL_ENTRY_USER_ID = "user_id";
    public static final String COL_ENTRY_DATE = "date";
    public static final String COL_ENTRY_FOOD_NAME = "food_name";
    public static final String COL_ENTRY_GRAMS = "grams";
    public static final String COL_ENTRY_CALORIES = "calories";
    public static final String COL_ENTRY_PROTEINS = "proteins";
    public static final String COL_ENTRY_FATS = "fats";
    public static final String COL_ENTRY_CARBS = "carbs";

    // --- Tabla Actividad Física ---
    public static final String TABLE_PHYSICAL_ACTIVITY = "physical_activity";
    public static final String COL_ACTIVITY_ID = "activity_id";
    public static final String COL_ACTIVITY_USER_ID = "user_id";
    public static final String COL_ACTIVITY_DATE = "date";
    public static final String COL_ACTIVITY_TYPE = "type";
    public static final String COL_ACTIVITY_INTENSITY = "intensity";
    public static final String COL_ACTIVITY_DURATION = "duration";
    public static final String COL_ACTIVITY_CALORIES = "calories_burned";
    public static final String COL_ACTIVITY_STEPS = "steps";

    // --- SQL CREATION ---

    // -----------------------------------------------------------------
    // 👇 CAMBIO 3: SQL actualizado para crear la tabla con las nuevas columnas
    // -----------------------------------------------------------------
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_FULLNAME + " TEXT, " +
                    COL_USERNAME + " TEXT UNIQUE, " +
                    COL_EMAIL + " TEXT, " +
                    COL_PASSWORD + " TEXT, " +
                    COL_WEIGHT + " REAL, " +
                    COL_AGE + " INTEGER, " +    // 👈 AÑADIDO
                    COL_GENDER + " TEXT, " +     // 👈 AÑADIDO
                    COL_HEIGHT + " REAL)";       // 👈 AÑADIDO

    private static final String CREATE_TABLE_SLEEP =
            "CREATE TABLE " + TABLE_SLEEP + " (" +
                    COL_SLEEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SLEEP_USER_ID + " INTEGER, " +
                    COL_SLEEP_DATE + " TEXT, " +
                    COL_SLEEP_HOURS + " REAL, " +
                    "FOREIGN KEY(" + COL_SLEEP_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COL_ID + "))";

    private static final String CREATE_TABLE_FOOD_ENTRIES =
            "CREATE TABLE " + TABLE_FOOD_ENTRIES + " (" +
                    COL_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ENTRY_USER_ID + " INTEGER, " +
                    COL_ENTRY_DATE + " TEXT, " +
                    COL_ENTRY_FOOD_NAME + " TEXT, " +
                    COL_ENTRY_GRAMS + " REAL, " +
                    COL_ENTRY_CALORIES + " REAL, " +
                    COL_ENTRY_PROTEINS + " REAL, " +
                    COL_ENTRY_FATS + " REAL, " +
                    COL_ENTRY_CARBS + " REAL, " +
                    "FOREIGN KEY(" + COL_ENTRY_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COL_ID + "))";

    private static final String CREATE_TABLE_PHYSICAL_ACTIVITY =
            "CREATE TABLE " + TABLE_PHYSICAL_ACTIVITY + " (" +
                    COL_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ACTIVITY_USER_ID + " INTEGER, " +
                    COL_ACTIVITY_DATE + " TEXT, " +
                    COL_ACTIVITY_TYPE + " TEXT, " +
                    COL_ACTIVITY_INTENSITY + " TEXT, " +
                    COL_ACTIVITY_DURATION + " REAL, " +
                    COL_ACTIVITY_CALORIES + " REAL, " +
                    COL_ACTIVITY_STEPS + " INTEGER, " +
                    "FOREIGN KEY(" + COL_ACTIVITY_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + COL_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // --- CREACIÓN DE TABLAS ---
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SLEEP);
        db.execSQL(CREATE_TABLE_FOOD_ENTRIES);
        db.execSQL(CREATE_TABLE_PHYSICAL_ACTIVITY);
    }

    // --- ACTUALIZACIÓN ---
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Tu lógica actual de Dropear Tablas (se ejecutará por el cambio de versión)
        // Esto borrará los datos existentes y creará las nuevas tablas.

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ENTRIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHYSICAL_ACTIVITY);

        onCreate(db);
    }

    // -----------------------------------------------------------------
    // 👇 CAMBIO 4: `insertUser` actualizado con los nuevos campos
    // -----------------------------------------------------------------
    public boolean insertUser(String fullName, String username, String email, String password, double weight, int age, String gender, double height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_FULLNAME, fullName);
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_WEIGHT, weight);

        // --- AÑADIDOS ---
        values.put(COL_AGE, age);
        values.put(COL_GENDER, gender);
        values.put(COL_HEIGHT, height);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // --------------------------
    // Obtener datos usuario
    // --------------------------
    public Cursor getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        // (No necesita cambios, SELECT * ya incluye las nuevas columnas)
        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + " = ?",
                new String[]{username}
        );
    }

    // --------------------------
    // Obtener peso del usuario
    // --------------------------
    public double getUserWeight(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_WEIGHT + " FROM " + TABLE_USERS +
                        " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        if (c != null && c.moveToFirst()) {
            double w = c.getDouble(0);
            c.close();
            return w;
        }
        return 0;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ID + " FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{username, password}
        );

        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return exists;
    }
    // --- Sueño ---

    public boolean insertSleep(long userId, String date, double hours) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SLEEP_USER_ID, userId);
        cv.put(COL_SLEEP_DATE, date);
        cv.put(COL_SLEEP_HOURS, hours);
        long result = db.insert(TABLE_SLEEP, null, cv);
        return result != -1;
    }

    public double getTodaySleep(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_SLEEP_HOURS +
                        " FROM " + TABLE_SLEEP +
                        " WHERE " + COL_SLEEP_USER_ID + " = ? " +
                        "AND " + COL_SLEEP_DATE + " = date('now')",
                new String[]{String.valueOf(userId)}
        );

        if (c != null && c.moveToFirst()) {
            double horas = c.getDouble(0);
            c.close();
            return horas;
        }
        return 0;
    }

    // --- Comida ---

    public boolean insertFoodEntry(long userId, String date, String foodName,
                                   double grams, double calories, double proteins,
                                   double fats, double carbs) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ENTRY_USER_ID, userId);
        contentValues.put(COL_ENTRY_DATE, date);
        contentValues.put(COL_ENTRY_FOOD_NAME, foodName);
        contentValues.put(COL_ENTRY_GRAMS, grams);
        contentValues.put(COL_ENTRY_CALORIES, calories);
        contentValues.put(COL_ENTRY_PROTEINS, proteins);
        contentValues.put(COL_ENTRY_FATS, fats);
        contentValues.put(COL_ENTRY_CARBS, carbs);

        long result = db.insert(TABLE_FOOD_ENTRIES, null, contentValues);
        return result != -1;
    }

    public Cursor getDailyEntries(long userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COL_ENTRY_ID + " AS _id, * FROM " + TABLE_FOOD_ENTRIES +
                        " WHERE " + COL_ENTRY_USER_ID + " = ? AND " + COL_ENTRY_DATE + " = ?",
                new String[]{String.valueOf(userId), date}
        );
    }

    // --- Actividad Física ---

    public boolean insertPhysicalActivity(long userId, String date, String type, String intensity,
                                          double duration, double calories, int steps) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVITY_USER_ID, userId);
        values.put(COL_ACTIVITY_DATE, date);
        values.put(COL_ACTIVITY_TYPE, type);
        values.put(COL_ACTIVITY_INTENSITY, intensity);
        values.put(COL_ACTIVITY_DURATION, duration);
        values.put(COL_ACTIVITY_CALORIES, calories);
        values.put(COL_ACTIVITY_STEPS, steps);

        long result = db.insert(TABLE_PHYSICAL_ACTIVITY, null, values);
        return result != -1;
    }

    public Cursor getDailyTotals(long userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query =
                "SELECT " +
                        "SUM(" + COL_ENTRY_CALORIES + ") AS total_calories, " +
                        "SUM(" + COL_ENTRY_PROTEINS + ") AS total_proteins, " +
                        "SUM(" + COL_ENTRY_FATS + ") AS total_fats, " +
                        "SUM(" + COL_ENTRY_CARBS + ") AS total_carbs " +
                        "FROM " + TABLE_FOOD_ENTRIES +
                        " WHERE " + COL_ENTRY_USER_ID + " = ? " +
                        "AND " + COL_ENTRY_DATE + " = ?";

        return db.rawQuery(query, new String[]{String.valueOf(userId), date});
    }

    // -----------------------------------------------------------------
    // 👇 CAMBIO 5: `updateUser` actualizado con los nuevos campos
    // -----------------------------------------------------------------
    public boolean updateUser(String username, String fullName, String email, float weight, String password, int age, String gender, double height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FULLNAME, fullName);
        values.put(COL_EMAIL, email);
        values.put(COL_WEIGHT, weight);
        values.put(COL_PASSWORD, password);

        // --- AÑADIDOS ---
        values.put(COL_AGE, age);
        values.put(COL_GENDER, gender);
        values.put(COL_HEIGHT, height);

        int result = db.update(TABLE_USERS, values, COL_USERNAME + "=?", new String[]{username});
        return result > 0;
    }

}