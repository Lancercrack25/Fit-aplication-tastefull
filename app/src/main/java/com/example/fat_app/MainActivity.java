package com.example.fat_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageButton imageButton0;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private ImageButton imageButton6;

    private Button btnSuscripcion;
    private Button btnActividadFisica;

    private TextView txtSueno;
    private TextView txtPeso;   // ← NUEVO: Tarjeta peso

    private long currentUserId = -1;
    private DatabaseHelper dbHelper;

    private double initialWeight = 0; // ← NUEVO: peso inicial del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Botones inferiores
        imageButton0 = findViewById(R.id.imgbutton0);
        imageButton1 = findViewById(R.id.imgbutton1);
        imageButton2 = findViewById(R.id.imgbutton2);
        imageButton3 = findViewById(R.id.imgbutton3);
        imageButton4 = findViewById(R.id.imgbutton4);
        imageButton5 = findViewById(R.id.imgbutton5);
        imageButton6 = findViewById(R.id.imgbutton6);

        // Botones superiores
        btnSuscripcion = findViewById(R.id.btnSuscribirse);
        btnActividadFisica = findViewById(R.id.btnActividadFisica);

        txtSueno = findViewById(R.id.txtSueno);
        txtPeso = findViewById(R.id.txtPeso);     // ← NUEVO

        String username = getIntent().getStringExtra("username");

        obtenerUserId(username);
        cargarSuenoDeHoy();
        cargarPesoInicial();        // ← NUEVO

        // Perfil
        imageButton0.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Perfil.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Inicio
        imageButton1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Alimentación
        imageButton2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Alimentacion.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Progreso
        imageButton3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Progress.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Rachas
        imageButton4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Racha.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Salir
        imageButton5.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        // Dieta
        imageButton6.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Dieta.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // SUBSCRIPCIÓN
        btnSuscripcion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // ACTIVIDAD FÍSICA
        btnActividadFisica.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActividadFisica.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        if (username != null) {
            Toast.makeText(this, "Bienvenido, " + username, Toast.LENGTH_SHORT).show();
        }
    }

    // OBTENER ID DEL USUARIO
    private void obtenerUserId(String username) {
        Cursor cursor = dbHelper.getUserData(username);
        if (cursor != null && cursor.moveToFirst()) {
            currentUserId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            initialWeight = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WEIGHT)); // ← NUEVO
            cursor.close();
        }
    }

    // CARGAR HORAS DE SUEÑO
    private void cargarSuenoDeHoy() {
        if (currentUserId == -1) return;

        double horas = dbHelper.getTodaySleep(currentUserId);

        if (horas <= 0) {
            txtSueno.setText("0 hr");
        } else {
            txtSueno.setText(horas + " hr");
        }
    }

    // CARGAR PESO INICIAL EN TARJETA
    private void cargarPesoInicial() {
        if (initialWeight <= 0) {
            txtPeso.setText("0 kg");
        } else {
            txtPeso.setText(initialWeight + " kg");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSuenoDeHoy();
        cargarPesoInicial(); // ← NUEVO
    }
}




