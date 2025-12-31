package com.example.fat_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    // Duración total del splash en milisegundos
    private static final int DURACION_TOTAL_SPLASH = 5000; // 5 segundos

    // Cuándo empezar la transición (en milisegundos)
    private static final long INICIO_TRANSICION = 1500; // A los 1.5 segundos

    // Duración de la transición de fundido (cross-fade)
    private static final long DURACION_TRANSICION = 1000; // 1 segundo

    private ImageView imagen1;
    private ImageView imagen2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar UI inmersiva
        configurarUIInmersiva();

        setContentView(R.layout.activity_splash);

        // Inicializar vistas
        inicializarVistas();

        // Verificar que las imágenes existen
        verificarImagenes();

        // Iniciar la animación
        iniciarAnimacion();

        // Programar el cambio a LoginActivity
        programarNavegacionALogin();
    }

    private void configurarUIInmersiva() {
        // Ocultar la barra de acción si existe
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Ocultar la UI del sistema para un splash inmersivo
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void inicializarVistas() {
        // Referenciar las imágenes del layout
        imagen1 = findViewById(R.id.splash_imagen_1);
        imagen2 = findViewById(R.id.splash_imagen_2);

        // Verificar que las vistas se encontraron
        if (imagen1 == null) {
            Log.e("SplashActivity", "❌ imagen1 es NULL - Verifica el ID en el XML");
        } else {
            Log.d("SplashActivity", "✅ imagen1 encontrada correctamente");
        }

        if (imagen2 == null) {
            Log.e("SplashActivity", "❌ imagen2 es NULL - Verifica el ID en el XML");
        } else {
            Log.d("SplashActivity", "✅ imagen2 encontrada correctamente");
        }
    }

    private void verificarImagenes() {
        try {
            // Verificar si los recursos existen
            int resId1 = getResources().getIdentifier("logo_fat", "drawable", getPackageName());
            int resId2 = getResources().getIdentifier("logo_fat2", "drawable", getPackageName());

            if (resId1 == 0) {
                Log.e("SplashActivity", "❌ logo_fat NO existe en drawable");
            } else {
                Log.d("SplashActivity", "✅ logo_fat encontrado - ID: " + resId1);
            }

            if (resId2 == 0) {
                Log.e("SplashActivity", "❌ logo_fat2 NO existe en drawable");
            } else {
                Log.d("SplashActivity", "✅ logo_fat2 encontrado - ID: " + resId2);
            }

        } catch (Exception e) {
            Log.e("SplashActivity", "Error verificando imágenes: " + e.getMessage());
        }
    }

    private void programarNavegacionALogin() {
        Log.d("SplashActivity", "🕐 Programando navegación a LoginActivity en " + DURACION_TOTAL_SPLASH + "ms");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d("SplashActivity", "🚀 Navegando a LoginActivity...");

            // ✅ CAMBIO IMPORTANTE: Ahora va a LoginActivity en lugar de MainActivity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);

            // Finalizar esta actividad para que no se pueda volver con "Atrás"
            finish();

            // Transición suave entre actividades
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            Log.d("SplashActivity", "✅ Navegación a LoginActivity completada");
        }, DURACION_TOTAL_SPLASH);
    }

    private void iniciarAnimacion() {
        Log.d("SplashActivity", "🎬 Iniciando animaciones de transición...");

        // Animación para imagen1: Desaparecer (Fade Out)
        imagen1.animate()
                .alpha(0f) // Hacerla transparente
                .setDuration(DURACION_TRANSICION) // Duración de 1 segundo
                .setStartDelay(INICIO_TRANSICION) // Empezar después de 1.5 segundos
                .withEndAction(() -> {
                    Log.d("SplashActivity", "✅ Animación Fade Out de imagen1 completada");
                })
                .start();

        // Animación para imagen2: Aparecer (Fade In)
        imagen2.animate()
                .alpha(1f) // Hacerla visible
                .setDuration(DURACION_TRANSICION) // Duración de 1 segundo
                .setStartDelay(INICIO_TRANSICION) // Empezar al mismo tiempo que la otra
                .withEndAction(() -> {
                    Log.d("SplashActivity", "✅ Animación Fade In de imagen2 completada");
                })
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Limpiar animaciones si la actividad se pausa
        if (imagen1 != null) {
            imagen1.animate().cancel();
        }
        if (imagen2 != null) {
            imagen2.animate().cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SplashActivity", "🔚 SplashActivity destruida");
        // Limpiar referencias
        imagen1 = null;
        imagen2 = null;
    }
}