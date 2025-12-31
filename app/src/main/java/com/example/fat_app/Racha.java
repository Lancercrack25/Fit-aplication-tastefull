package com.example.fat_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

// --- HE BORRADO LOS IMPORTS DE KONFETTI ---
// Ya no los necesitamos por ahora.

// IMPORTS DE JAVA
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Racha extends AppCompatActivity {

    // --- Variables de Vistas ---
    private ImageButton imageButton0, imageButton1, imageButton2, imageButton3, imageButton4, imageButton5, imageButton6;
    private TextView tvRacha;
    private Button btnCumpliMeta;

    // --- LÍNEA COMENTADA ---
    // private nl.dionsegijn.konfetti.xml.KonfettiView konfettiView;

    private List<TextView> circleList;

    // --- VARIABLES NUEVAS PARA ESTADÍSTICAS ---
    private TextView tvSemanasNumero;
    private TextView tvMesesNumero;
    private TextView tvAnosNumero;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.racha);

        // --- Inicialización de Vistas ---
        inicializarVistas();
        inicializarListenersNavegacion();

        // Obtener username
        username = getIntent().getStringExtra("username");

        // Botón para marcar la meta cumplida
        btnCumpliMeta.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updateStreak(username);
                updateUI(); // Llamamos a la nueva función de UI
            }
        });

        // Mostrar racha inicial al abrir la pantalla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updateUI(); // Llamamos a la nueva función de UI
        }
    }

    private void inicializarVistas() {
        // Botones de navegación
        imageButton0 = findViewById(R.id.imgbutton0);
        imageButton1 = findViewById(R.id.imgbutton1);
        imageButton2 = findViewById(R.id.imgbutton2);
        imageButton3 = findViewById(R.id.imgbutton3);
        imageButton4 = findViewById(R.id.imgbutton4);
        imageButton5 = findViewById(R.id.imgbutton5);
        imageButton6 = findViewById(R.id.imgbutton6);

        // Vistas de racha
        tvRacha = findViewById(R.id.tvRacha);
        btnCumpliMeta = findViewById(R.id.btnCumpliMeta);

        // --- LÍNEA COMENTADA ---
        // konfettiView = findViewById(R.id.konfettiView);

        circleList = new ArrayList<>();
        circleList.add(findViewById(R.id.circle1));
        circleList.add(findViewById(R.id.circle2));
        circleList.add(findViewById(R.id.circle3));
        circleList.add(findViewById(R.id.circle4));
        circleList.add(findViewById(R.id.circle5));
        circleList.add(findViewById(R.id.circle6));
        circleList.add(findViewById(R.id.circle7));

        // --- CONEXIÓN DE LAS NUEVAS VISTAS DE ESTADÍSTICAS ---
        tvSemanasNumero = findViewById(R.id.tvSemanasNumero);
        tvMesesNumero = findViewById(R.id.tvMesesNumero);
        tvAnosNumero = findViewById(R.id.tvAnosNumero);
    }

    private void inicializarListenersNavegacion() {
        imageButton0.setOnClickListener(v -> {
            Intent intent = new Intent(Racha.this, Perfil.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
        imageButton1.setOnClickListener(v -> {
            Intent intent = new Intent(Racha.this, MainActivity.class);
            startActivity(intent);
        });
        imageButton2.setOnClickListener(v -> {
            Intent intent = new Intent(Racha.this, Alimentacion.class);
            startActivity(intent);
        });
        imageButton3.setOnClickListener(v -> {
            Intent intent = new Intent(Racha.this, Progress.class);
            startActivity(intent);
        });
        imageButton4.setOnClickListener(v -> {
            Intent intent = new Intent(Racha.this, Racha.class);
            startActivity(intent);
        });
        imageButton5.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });
        imageButton6.setOnClickListener(v -> {
            Intent intent = new Intent(Racha.this, Dieta.class);
            startActivity(intent);
        });
    }

    // Función para actualizar la racha
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateStreak(String username) {
        if (username == null || username.isEmpty()) return;

        SharedPreferences prefs = getSharedPreferences("user_data_" + username, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        LocalDate today = LocalDate.now();
        String lastDateStr = prefs.getString("last_streak_date", null);
        int streakCount = prefs.getInt("streak_count", 0);

        // --- Lógica de Racha ---
        if (lastDateStr != null) {
            try {
                LocalDate lastDate = LocalDate.parse(lastDateStr);
                long daysBetween = ChronoUnit.DAYS.between(lastDate, today);

                if (daysBetween == 1) {
                    streakCount++; // Racha continúa
                } else if (daysBetween > 1) {
                    streakCount = 1; // Racha se rompió y reinicia
                }
            } catch (Exception e) {
                streakCount = 1; // Error parseando, reiniciar
            }
        } else {
            streakCount = 1; // Primera vez
        }

        // --- LÓGICA DE CONFETI (Desactivada temporalmente) ---
        boolean launchConfetti = false;
        if (streakCount > 0 && streakCount % 7 == 0) {
            if (lastDateStr == null || !lastDateStr.equals(today.toString())) {
                launchConfetti = true;
            }
        }

        // Guardar racha y fecha
        editor.putInt("streak_count", streakCount);
        editor.putString("last_streak_date", today.toString());

        java.util.Set<String> historial = prefs.getStringSet("streak_history", new java.util.HashSet<>());
        historial.add(today.toString());
        editor.putStringSet("streak_history", historial);

        editor.apply();

        // --- LLAMADA A CONFETI COMENTADA ---
        // if (launchConfetti) {
        //     showConfetti();
        // }

        Toast.makeText(this, "Racha actual: " + streakCount + " días", Toast.LENGTH_SHORT).show();
    }


    // --- FUNCIÓN DE CONFETI DESACTIVADA ---
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showConfetti() {
        // TODO: Arreglar la librería Konfetti en el build.gradle.kts
        // Esta función está desactivada temporalmente para que el proyecto compile.

        // nl.dionsegijn.konfetti.core.emitter.EmitterConfig emitterConfig =
        //     new nl.dionsegijn.konfetti.core.emitter.Emitter(5L, TimeUnit.SECONDS).perSecond(100);
        //
        // nl.dionsegijn.konfetti.core.Party party =
        //     new nl.dionsegijn.konfetti.core.Party.Builder()
        //         .setSpeedBetween(0f, 30f)
        //         .position(new nl.dionsegijn.konfetti.core.Position.Relative(0.5, 0.3))
        //         .spread(360)
        //         .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
        //         .shapes(Arrays.asList(
        //             nl.dionsegijn.konfetti.core.models.Shape.Square.INSTANCE,
        //             nl.dionsegijn.konfetti.core.models.Shape.Circle.INSTANCE
        //         ))
        //         .emitter(emitterConfig)
        //         .build();
        //
        // konfettiView.start(party);
    }


    // Función para actualizar la UI (Círculos y texto de Racha)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUI() {
        if (username == null || username.isEmpty()) return;

        SharedPreferences prefs = getSharedPreferences("user_data_" + username, Context.MODE_PRIVATE);
        int streakCount = prefs.getInt("streak_count", 0);

        tvRacha.setText("Racha: " + streakCount + " días");

        // --- LÓGICA DE CÍRCULOS ---
        int activeCircles = streakCount % 7;

        if (activeCircles == 0 && streakCount > 0) {
            activeCircles = 7;
        }

        for (int i = 0; i < circleList.size(); i++) {
            TextView circle = circleList.get(i);
            if (i < activeCircles) {
                circle.setBackground(getDrawable(R.drawable.circle_background_active));
            } else {
                circle.setBackground(getDrawable(R.drawable.circle_background_inactive));
            }
        }

        // --- ¡NUEVA LÓGICA DE ESTADÍSTICAS! ---

        // Calculamos las estadísticas basándonos en el total de días (streakCount)
        int semanasCompletadas = streakCount / 7;
        int mesesCompletados = streakCount / 30; // Usamos 30 como promedio
        int anosCompletados = streakCount / 365;

        // Asignamos los números a las tarjetas
        tvSemanasNumero.setText(String.valueOf(semanasCompletadas));
        tvMesesNumero.setText(String.valueOf(mesesCompletados));
        tvAnosNumero.setText(String.valueOf(anosCompletados));
    }
}