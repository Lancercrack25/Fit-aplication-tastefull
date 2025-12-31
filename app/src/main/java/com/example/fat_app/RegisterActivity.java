package com.example.fat_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    // 👇 CAMBIO 1: Añadir variables para los nuevos campos
    private EditText editFullName, editEmail, editUsername, editPassword, editConfirmPassword, editInitialWeight;
    private EditText editAge, editGender, editHeight; // NUEVOS

    private Button btnRegister;
    private TextView txtGoToLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // --- IDs Antiguos ---
        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editInitialWeight = findViewById(R.id.editInitialWeight);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtGoToLogin = findViewById(R.id.txtGoToLogin);

        // 👇 CAMBIO 2: Encontrar los nuevos EditText (¡DEBES AÑADIRLOS AL XML!)
        editAge = findViewById(R.id.editAge);
        editGender = findViewById(R.id.editGender);
        editHeight = findViewById(R.id.editHeight);

        btnRegister.setOnClickListener(v -> registerUser());
        txtGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {

        // --- Obtener texto de campos antiguos ---
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String initialWeightStr = editInitialWeight.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        // 👇 CAMBIO 3: Obtener texto de los nuevos campos
        String ageStr = editAge.getText().toString().trim();
        String gender = editGender.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();

        // 👇 CAMBIO 4: Añadir los nuevos campos a la validación
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty()
                || initialWeightStr.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || ageStr.isEmpty() || gender.isEmpty() || heightStr.isEmpty()) {

            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Parsear valores ---
        double weight = Double.parseDouble(initialWeightStr);
        // 👇 CAMBIO 5: Parsear los nuevos valores
        int age = Integer.parseInt(ageStr);
        double height = Double.parseDouble(heightStr); // Asumimos estatura en cm (ej: 175.5)

        // 👇 CAMBIO 6: Llamar a insertUser con los 8 argumentos
        boolean inserted = dbHelper.insertUser(
                fullName,
                username,
                email,
                password,
                weight,
                age,      // Argumento 6
                gender,   // Argumento 7
                height    // Argumento 8
        );

        if (inserted) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error: el usuario ya existe", Toast.LENGTH_SHORT).show();
        }
    }
}