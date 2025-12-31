package com.example.fat_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Dieta extends AppCompatActivity {

    private Button btnSubir, btnBajar, btnComenzar;
    private EditText etPesoActual, etPesoDeseado;

    private String objetivo = "";
    private double pesoUsuarioActual = 0.0;

    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dieta_recomendation);

        // Vincular vistas
        btnSubir = findViewById(R.id.btnSubir);
        btnBajar = findViewById(R.id.btnBajar);
        btnComenzar = findViewById(R.id.btnComenzar);
        etPesoActual = findViewById(R.id.etPesoActual);
        etPesoDeseado = findViewById(R.id.etPesoDeseado);

        etPesoActual.setEnabled(false); // No permitir modificar peso actual

        // Inicializar DB
        dbHelper = new DatabaseHelper(this);

        // Obtener username desde Intent o SharedPreferences
        username = getIntent().getStringExtra("username");
        if (username == null) {
            username = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getString("username", null);
        }

        if (username != null) {
            cargarPesoUsuario(username);
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

        // Botones de objetivo
        btnSubir.setOnClickListener(v -> {
            objetivo = "subir";
            btnSubir.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
            btnBajar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        });

        btnBajar.setOnClickListener(v -> {
            objetivo = "bajar";
            btnBajar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            btnSubir.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        });

        btnComenzar.setOnClickListener(v -> {
            String pesoDeseadoStr = etPesoDeseado.getText().toString().trim();

            if (pesoDeseadoStr.isEmpty() || objetivo.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double pesoDeseado;
            try {
                pesoDeseado = Double.parseDouble(pesoDeseadoStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingresa un peso válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (objetivo.equals("subir") && pesoDeseado <= pesoUsuarioActual) {
                Toast.makeText(this, "El peso deseado debe ser mayor que el actual para subir de peso", Toast.LENGTH_SHORT).show();
                return;
            }

            if (objetivo.equals("bajar") && pesoDeseado >= pesoUsuarioActual) {
                Toast.makeText(this, "El peso deseado debe ser menor que el actual para bajar de peso", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Dieta.this, Resultados.class);
            intent.putExtra("objetivo", objetivo);
            intent.putExtra("pesoActual", pesoUsuarioActual);
            intent.putExtra("pesoDeseado", pesoDeseado);
            startActivity(intent);
        });
    }

    private void cargarPesoUsuario(String username) {
        Cursor cursor = dbHelper.getUserData(username);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int colIndex = cursor.getColumnIndex(DatabaseHelper.COL_WEIGHT);

                if (colIndex != -1) {
                    // Revisar si el valor no es NULL
                    if (!cursor.isNull(colIndex)) {
                        pesoUsuarioActual = cursor.getDouble(colIndex);
                        etPesoActual.setText(String.valueOf(pesoUsuarioActual));
                    } else {
                        etPesoActual.setText(""); // No hay peso registrado
                        pesoUsuarioActual = 0.0;
                    }
                }
            }
            cursor.close();
        }
    }
}
