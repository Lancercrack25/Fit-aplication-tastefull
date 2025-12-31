package com.example.fat_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

// 👇 CAMBIO: Imports necesarios para el nuevo diálogo
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.app.AlertDialog;

public class Perfil extends AppCompatActivity {

    // 👇 CAMBIO 1: Añadir variables para los nuevos TextViews
    private TextView textView, textView6, textView7, textView8, textView9, textView10, textView11, textView12;
    private DatabaseHelper dbHelper;
    private String username;
    private ImageView profileImage;
    private Button btnedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.perfil);

        // Vincular vistas del XML
        textView = findViewById(R.id.textView);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);

        // 👇 CAMBIO 2: Vincular los nuevos TextViews (¡DEBES AÑADIRLOS A TU XML!)
        textView10 = findViewById(R.id.textView10); // Asumiendo ID para Edad
        textView11 = findViewById(R.id.textView11); // Asumiendo ID para Sexo
        textView12 = findViewById(R.id.textView12); // Asumiendo ID para Estatura

        profileImage = findViewById(R.id.imageView2);
        btnedit = findViewById(R.id.buttonedit);

        dbHelper = new DatabaseHelper(this);

        // Obtener username desde el Intent o SharedPreferences
        username = getIntent().getStringExtra("username");
        if (username == null) {
            username = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getString("username", null);
        }

        if (username != null) {
            loadUserData(username);
        } else {
            Toast.makeText(this, "No se recibió el nombre de usuario", Toast.LENGTH_SHORT).show();
        }

        btnedit.setOnClickListener(v -> showEditDialog());
    }

    private void loadUserData(String username) {
        Cursor cursor = dbHelper.getUserData(username);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
                String user = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("initial_weight"));

                // 👇 CAMBIO 3: Leer los nuevos datos de la base de datos
                int age = cursor.getInt(cursor.getColumnIndexOrThrow("age"));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                double height = cursor.getDouble(cursor.getColumnIndexOrThrow("height"));

                // Mostrar datos
                textView.setText("Usuario: " + user);
                textView6.setText("Nombre completo: " + fullName);
                textView7.setText("Correo: " + email);
                textView8.setText("Contraseña: ********");
                textView9.setText("Peso: " + weight + " kg");

                // 👇 CAMBIO 4: Mostrar los nuevos datos en los nuevos TextViews
                textView10.setText("Edad: " + age);
                textView11.setText("Sexo: " + gender);
                textView12.setText("Estatura: " + height + " cm");
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void showEditDialog() {
        Cursor cursor = dbHelper.getUserData(username);
        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(this, "Error cargando datos", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Cargar datos actuales ---
        String currentFullName = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
        String currentEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
        String currentPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
        float currentWeight = cursor.getFloat(cursor.getColumnIndexOrThrow("initial_weight"));

        // 👇 CAMBIO 5: Cargar los nuevos datos actuales
        int currentAge = cursor.getInt(cursor.getColumnIndexOrThrow("age"));
        String currentGender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
        double currentHeight = cursor.getDouble(cursor.getColumnIndexOrThrow("height"));
        cursor.close();

        // --- Crear EditTexts para el diálogo ---
        final EditText inputName = new EditText(this);
        final EditText inputEmail = new EditText(this);
        final EditText inputPassword = new EditText(this);
        final EditText inputWeight = new EditText(this);

        // 👇 CAMBIO 6: Crear EditTexts para los nuevos datos
        final EditText inputAge = new EditText(this);
        final EditText inputGender = new EditText(this);
        final EditText inputHeight = new EditText(this);

        // --- Configurar Hints ---
        inputName.setHint("Nombre completo");
        inputEmail.setHint("Correo");
        inputPassword.setHint("Contraseña");
        inputWeight.setHint("Peso (kg)");
        inputAge.setHint("Edad");
        inputGender.setHint("Sexo");
        inputHeight.setHint("Estatura (cm)");

        // --- Poner texto actual en los inputs ---
        inputName.setText(currentFullName);
        inputEmail.setText(currentEmail);
        inputPassword.setText(currentPassword);
        inputWeight.setText(String.valueOf(currentWeight));
        inputAge.setText(String.valueOf(currentAge));
        inputGender.setText(currentGender);
        inputHeight.setText(String.valueOf(currentHeight));

        // --- Configurar tipos de input ---
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputAge.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // --- Construir el Layout del Diálogo ---
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(inputName);
        layout.addView(inputEmail);
        layout.addView(inputPassword);
        layout.addView(inputWeight);

        // 👇 CAMBIO 7: Añadir los nuevos inputs al layout
        layout.addView(inputAge);
        layout.addView(inputGender);
        layout.addView(inputHeight);

        // --- Construir el Diálogo ---
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Perfil");
        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            // --- Obtener datos de los inputs ---
            String newName = inputName.getText().toString().trim();
            String newEmail = inputEmail.getText().toString().trim();
            String newPassword = inputPassword.getText().toString().trim();
            String weightStr = inputWeight.getText().toString().trim();

            // 👇 CAMBIO 8: Obtener los nuevos datos
            String ageStr = inputAge.getText().toString().trim();
            String newGender = inputGender.getText().toString().trim();
            String heightStr = inputHeight.getText().toString().trim();

            // --- Validación ---
            if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || weightStr.isEmpty() ||
                    ageStr.isEmpty() || newGender.isEmpty() || heightStr.isEmpty()) { // Comprobar nuevos campos

                Toast.makeText(this, "No puede haber campos vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- Parsear valores ---
            float newWeight = Float.parseFloat(weightStr);
            int newAge = Integer.parseInt(ageStr);
            double newHeight = Double.parseDouble(heightStr);

            // 👇 CAMBIO 9: Llamar al método updateUser con los 8 argumentos
            boolean updated = dbHelper.updateUser(
                    username,
                    newName,
                    newEmail,
                    newWeight,
                    newPassword,
                    newAge,      // Argumento 6
                    newGender,   // Argumento 7
                    newHeight    // Argumento 8
            );

            if (updated) {
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                loadUserData(username); // Recargar datos en la UI
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}