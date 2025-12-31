package com.example.fat_app;

import android.content.Intent;
import android.database.Cursor;
// import android.net.Uri; // Ya no lo usas
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// import androidx.activity.EdgeToEdge; // Lo quitamos, ya no es necesario con el layout nuevo
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Progress extends AppCompatActivity {

    private static final String TAG = "ProgressActivity";

    // Variables de BD y Usuario
    private DatabaseHelper dbHelper;
    private long currentUserId = -1;
    private String username;

    // Variables para guardar los datos del usuario
    private int userAge = 0;
    private double userHeight = 0.0;
    private double userWeight = 0.0;
    private String userGender = "";

    // Variables para las metas calculadas
    private double goalCalories = 2000; // Default por si falla el cálculo
    private double goalProteins = 150;
    private double goalCarbs = 250;
    private double goalFats = 70;

    // Vistas de Progreso (NUEVAS)
    private TextView txtProgressCalories, txtProgressProtein, txtProgressCarbs, txtProgressFats;
    private ProgressBar progressBarCalories, progressBarProtein, progressBarCarbs, progressBarFats;

    // Vistas de Navegación (LAS TUYAS)
    // === CAMBIO: Se eliminó imageButton0 ===
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private ImageButton imageButton6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Lo quitamos
        setContentView(R.layout.progreso); // Carga el NUEVO XML

        // --- 1. Inicializar la Base de Datos ---
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        if (username == null) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_LONG).show();
            finish(); // Si no hay usuario, no podemos mostrar nada
            return;
        }

        // --- 2. Encontrar TODAS las Vistas (Progreso y Navegación) ---
        initProgressViews(); // Método para las vistas de progreso
        initNavigationViews(); // Método para tus botones

        // --- 3. Configurar la Lógica de Progreso ---
        setupUser(); // Busca el ID y DATOS del usuario (¡Modificado!)

        if (currentUserId != -1) {
            setupGoals(); // Configura los máximos de las barras (¡Modificado!)
            loadDailyProgress(); // Carga los datos de la BD (¡Modificado!)
        }

        // --- 4. Configurar la Lógica de Navegación ---
        setupNavigationListeners(); // Método para tus botones
    }

    // --- MÉTODOS DE PROGRESO (NUEVOS) ---

    private void initProgressViews() {
        // Textos (Ej: "80g / 150g")
        txtProgressCalories = findViewById(R.id.txtProgressCalories);
        txtProgressProtein = findViewById(R.id.txtProgressProtein);
        txtProgressCarbs = findViewById(R.id.txtProgressCarbs);
        txtProgressFats = findViewById(R.id.txtProgressFats);

        // Barras de progreso
        progressBarCalories = findViewById(R.id.progressBarCalories);
        progressBarProtein = findViewById(R.id.progressBarProtein);
        progressBarCarbs = findViewById(R.id.progressBarCarbs);
        progressBarFats = findViewById(R.id.progressBarFats);
    }

    private void setupUser() {
        try {
            Cursor cursor = dbHelper.getUserData(username);
            if (cursor != null && cursor.moveToFirst()) {
                currentUserId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));

                // Leemos los datos para calcular metas
                userWeight = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WEIGHT));
                userHeight = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HEIGHT));
                userAge = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AGE));
                userGender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENDER));

                cursor.close();
                Log.d(TAG, "Usuario ID: " + currentUserId + " | Peso: " + userWeight + " | Sexo: " + userGender);

                // Una vez tenemos los datos, calculamos las metas
                calculateUserGoals();

            } else {
                Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setupUser: " + e.getMessage());
        }
    }

    /**
     * Calcula TDEE (calorías de mantenimiento) y macros
     * usando la fórmula Mifflin-St Jeor.
     */
    private void calculateUserGoals() {
        if (userWeight <= 0 || userHeight <= 0 || userAge <= 0 || userGender.isEmpty()) {
            Log.e(TAG, "Datos de usuario incompletos. Usando metas por defecto.");
            // Si faltan datos, dejamos las metas por defecto (2000, 150, 250, 70)
            return;
        }

        double bmr; // Tasa Metabólica Basal

        // Fórmula Mifflin-St Jeor
        if (userGender.equalsIgnoreCase("Masculino")) {
            bmr = (10 * userWeight) + (6.25 * userHeight) - (5 * userAge) + 5;
        } else { // Femenino u otro
            bmr = (10 * userWeight) + (6.25 * userHeight) - (5 * userAge) - 161;
        }

        // TDEE (Mantenimiento) = BMR * Factor de Actividad
        // Usamos 1.375 (Actividad Ligera: 1-3 días/semana) como un buen promedio
        double tdee = bmr * 1.375;
        goalCalories = tdee;

        // Macros (40% Carbos, 30% Proteína, 30% Grasa)
        goalProteins = (tdee * 0.30) / 4.0;
        goalCarbs = (tdee * 0.40) / 4.0;
        goalFats = (tdee * 0.30) / 9.0;

        Log.d(TAG, String.format(Locale.US, "Metas calculadas (Mantenimiento): %.0f Kcal, %.0fg Prot, %.0fg Carbs, %.0fg Grasa",
                goalCalories, goalProteins, goalCarbs, goalFats));
    }


    private void setupGoals() {
        progressBarCalories.setMax((int) goalCalories);
        progressBarProtein.setMax((int) goalProteins);
        progressBarCarbs.setMax((int) goalCarbs);
        progressBarFats.setMax((int) goalFats);

        txtProgressCalories.setText(String.format(Locale.US, "0 / %.0f Kcal", goalCalories));
        txtProgressProtein.setText(String.format(Locale.US, "0g / %.0fg", goalProteins));
        txtProgressCarbs.setText(String.format(Locale.US, "0g / %.0fg", goalCarbs));
        txtProgressFats.setText(String.format(Locale.US, "0g / %.0fg", goalFats));
    }

    private void loadDailyProgress() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Cursor cursor = dbHelper.getDailyTotals(currentUserId, todayDate);

        if (cursor != null && cursor.moveToFirst()) {

            double totalKcal = cursor.getDouble(cursor.getColumnIndexOrThrow("total_calories"));
            double totalProt = cursor.getDouble(cursor.getColumnIndexOrThrow("total_proteins"));
            double totalCarb = cursor.getDouble(cursor.getColumnIndexOrThrow("total_carbs"));
            double totalFat = cursor.getDouble(cursor.getColumnIndexOrThrow("total_fats"));

            cursor.close();

            Log.d(TAG, String.format("Datos cargados: Kcal=%.1f, P=%.1f, C=%.1f, F=%.1f", totalKcal, totalProt, totalCarb, totalFat));

            // --- ¡AQUÍ ACTUALIZAMOS LA INTERFAZ! ---
            progressBarCalories.setProgress((int) totalKcal);
            txtProgressCalories.setText(String.format(Locale.US, "%.0f / %.0f Kcal", totalKcal, goalCalories));

            progressBarProtein.setProgress((int) totalProt);
            txtProgressProtein.setText(String.format(Locale.US, "%.1fg / %.0fg", totalProt, goalProteins));

            progressBarCarbs.setProgress((int) totalCarb);
            txtProgressCarbs.setText(String.format(Locale.US, "%.1fg / %.0fg", totalCarb, goalCarbs));

            progressBarFats.setProgress((int) totalFat);
            txtProgressFats.setText(String.format(Locale.US, "%.1fg / %.0fg", totalFat, goalFats));

        } else {
            Log.d(TAG, "No se encontraron registros para hoy.");
        }
    }

    // --- MÉTODOS DE NAVEGACIÓN (LOS TUYOS, PERO ORGANIZADOS) ---

    private void initNavigationViews() {
        // === CAMBIO: Se eliminó la búsqueda de imgbutton0 ===
        imageButton1 = findViewById(R.id.imgbutton1);
        imageButton2 = findViewById(R.id.imgbutton2);
        imageButton3 = findViewById(R.id.imgbutton3);
        imageButton4 = findViewById(R.id.imgbutton4);
        imageButton5 = findViewById(R.id.imgbutton5);
        imageButton6 = findViewById(R.id.imgbutton6);
    }

    private void setupNavigationListeners() {

        // === CAMBIO: Se eliminó el listener de imgbutton0 ===

        imageButton1.setOnClickListener(v -> {
            Intent intent = new Intent(Progress.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        imageButton2.setOnClickListener(v -> {
            Intent intent = new Intent(Progress.this, Alimentacion.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        imageButton3.setOnClickListener(v -> {
            // Ya estás en Progress, pero recargamos por si acaso
            Intent intent = new Intent(Progress.this, Progress.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish(); // Cierra la actual
        });

        imageButton4.setOnClickListener(v -> {
            Intent intent = new Intent(Progress.this, Racha.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        imageButton5.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        imageButton6.setOnClickListener(v -> {
            Intent intent = new Intent(Progress.this, Dieta.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }
}