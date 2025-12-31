package com.example.fat_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActividadFisica extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String username;
    private long currentUserId = -1;

    private Spinner spinnerTipo, spinnerIntensidad;
    private EditText editDuracion, editSueno; // 🔹 Nuevo campo
    private TextView txtCalorias, txtUbicacion;
    private Button btnRegistrarManual, btnRegistrarGPS;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_LOCATION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_fisica);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        spinnerTipo = findViewById(R.id.spinnerTipo);
        spinnerIntensidad = findViewById(R.id.spinnerIntensidad);
        editDuracion = findViewById(R.id.editDuracion);
        editSueno = findViewById(R.id.editSueno); // 🔹 Nuevo

        txtCalorias = findViewById(R.id.txtCalorias);
        txtUbicacion = findViewById(R.id.txtUbicacion);

        btnRegistrarManual = findViewById(R.id.btnRegistrarManual);
        btnRegistrarGPS = findViewById(R.id.btnRegistrarGPS);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupUser();
        setupSpinners();

        btnRegistrarManual.setOnClickListener(v -> registrarManual());
        btnRegistrarGPS.setOnClickListener(v -> obtenerUbicacion());
    }

    private void setupUser() {
        Cursor cursor = dbHelper.getUserData(username);
        if (cursor != null && cursor.moveToFirst()) {
            currentUserId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            cursor.close();
        }
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(this,
                R.array.tipo_actividad, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterIntensidad = ArrayAdapter.createFromResource(this,
                R.array.intensidad_actividad, android.R.layout.simple_spinner_item);
        adapterIntensidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIntensidad.setAdapter(adapterIntensidad);
    }
    //sueño
    private void registrarManual() {
        String tipo = spinnerTipo.getSelectedItem().toString();
        String intensidad = spinnerIntensidad.getSelectedItem().toString();
        String duracionStr = editDuracion.getText().toString();
        String suenoStr = editSueno.getText().toString(); // 🔹 Nuevo

        if (duracionStr.isEmpty()) {
            Toast.makeText(this, "Ingresa la duración en minutos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (suenoStr.isEmpty()) {
            Toast.makeText(this, "Ingresa tus horas de sueño", Toast.LENGTH_SHORT).show();
            return;
        }

        double duracion = Double.parseDouble(duracionStr);
        double horasSueno = Double.parseDouble(suenoStr); // 🔹 Nuevo

        double factor;
        switch (intensidad) {
            case "Alta": factor = 10.0; break;
            case "Media": factor = 7.0; break;
            default: factor = 4.0; break;
        }

        double calorias = duracion * factor;

        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        boolean inserted = dbHelper.insertPhysicalActivity(currentUserId, fecha, tipo, intensidad, duracion, calorias, 0);

        dbHelper.insertSleep(currentUserId, fecha, horasSueno); //  Guardar sueño

        if (inserted) {
            txtCalorias.setText("Calorías quemadas: " + calorias);
            Toast.makeText(this, "Actividad registrada", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) registrarConUbicacion(location);
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permiso concedido, obteniendo ubicación...", Toast.LENGTH_SHORT).show();
                obtenerUbicacion(); //  Se vuelve a ejecutar ahora que si se tiene permiso

            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registrarConUbicacion(Location location) {
        String tipo = "Caminata (GPS)";
        String intensidad = "Media";
        double duracion = 30;
        double calorias = duracion * 5.5;

        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        txtUbicacion.setText(
                String.format(Locale.getDefault(), "Lat: %.4f, Lon: %.4f",
                        location.getLatitude(), location.getLongitude())
        );

        dbHelper.insertPhysicalActivity(currentUserId, fecha, tipo, intensidad, duracion, calorias, 2000);

        Toast.makeText(this, "Actividad con GPS registrada", Toast.LENGTH_SHORT).show();
    }
}


