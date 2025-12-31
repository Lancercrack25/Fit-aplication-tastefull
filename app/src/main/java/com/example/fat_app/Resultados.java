package com.example.fat_app;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fat_app.api.ApiClient;
import com.example.fat_app.api.UsdaApiService;
import com.example.fat_app.api.model.UsdaFood;
import com.example.fat_app.api.model.UsdaSearchResponse;

import java.io.OutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Resultados extends AppCompatActivity {

    private TextView tvResumen, tvDieta, tvRutina;
    private String objetivo;
    private double pesoActual, pesoDeseado;

    private static final String USDA_API_KEY = "55ozlH3yMfKsqS480BAe0zrA5eWXzUJcLpBJUW3X";

    private ActivityResultLauncher<Intent> crearPdfLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dieta_results);

        // Vistas
        tvResumen = findViewById(R.id.tvResumen);
        tvDieta = findViewById(R.id.tvDieta);
        tvRutina = findViewById(R.id.tvRutina);
        Button btnDescargar = findViewById(R.id.btnDescargar);

        // Recuperar datos del Intent
        Intent intent = getIntent();
        objetivo = intent.getStringExtra("objetivo");
        pesoActual = intent.getDoubleExtra("pesoActual", 0);
        pesoDeseado = intent.getDoubleExtra("pesoDeseado", 0);

        tvResumen.setText(
                "Objetivo: " + objetivo + "\n" +
                        "Peso actual: " + pesoActual + " kg\n" +
                        "Peso deseado: " + pesoDeseado + " kg"
        );

        // Generar dieta con USDA
        generarRecomendacionUSDA();

        // Inicializar launcher para guardar PDF
        crearPdfLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        guardarPdfEnUri(uri);
                    }
                }
        );

        btnDescargar.setOnClickListener(v -> crearArchivoPdf());
    }

    // -------------------------------------------------------------------
    // 1) Llamada a la API USDA
    private void generarRecomendacionUSDA() {

        String query;

        if (objetivo.contains("subir")) {
            query = "high calorie food";
        } else if (objetivo.contains("bajar")) {
            query = "low calorie food";
        } else {
            query = "healthy food";
        }

        UsdaApiService api = ApiClient.getRetrofitInstance().create(UsdaApiService.class);

        Call<UsdaSearchResponse> call = api.searchFoods(query, USDA_API_KEY, 10);

        call.enqueue(new Callback<UsdaSearchResponse>() {
            @Override
            public void onResponse(Call<UsdaSearchResponse> call, Response<UsdaSearchResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    mostrarFallback("Error con USDA");
                    return;
                }

                List<UsdaFood> lista = response.body().getFoods();

                if (lista == null || lista.isEmpty()) {
                    mostrarFallback("No se encontraron alimentos");
                    return;
                }

                generarDietaConLista(lista);
            }

            @Override
            public void onFailure(Call<UsdaSearchResponse> call, Throwable t) {
                mostrarFallback("Error de red");
            }
        });
    }

    // -------------------------------------------------------------------
    // 2) Crear texto de dieta
    private void generarDietaConLista(List<UsdaFood> lista) {

        StringBuilder dieta = new StringBuilder();
        dieta.append("DIETA RECOMENDADA (USDA)\n\n");

        for (UsdaFood item : lista) {
            dieta.append("• ").append(item.getDescription()).append("\n");
        }

        tvDieta.setText(dieta.toString());

        generarRutinaLocal();
    }

    // -------------------------------------------------------------------
    // 3) Rutina según objetivo
    private void generarRutinaLocal() {

        String rutina;

        if (objetivo.contains("subir")) {
            rutina = "- Fuerza 4-5 días por semana\n" +
                    "- Sentadilla, press banca, peso muerto\n" +
                    "- Cardio ligero 1-2 días";
        }
        else if (objetivo.contains("bajar")) {
            rutina = "- Cardio 30-45 min (4 veces/semana)\n" +
                    "- HIIT 2 veces/semana\n" +
                    "- Pesas ligeras (3 días)";
        }
        else {
            rutina = "- Rutina balanceada: 3 días fuerza + 2 cardio\n" +
                    "- Estiramientos diarios";
        }

        tvRutina.setText(rutina);
    }

    // -------------------------------------------------------------------
    // 4) Mensaje fallback
    private void mostrarFallback(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        tvDieta.setText("No se pudo obtener la dieta de USDA.");
        generarRutinaLocal();
    }

    // -------------------------------------------------------------------
    // 5) Abrir menú para crear PDF
    private void crearArchivoPdf() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "plan_personal.pdf");
        crearPdfLauncher.launch(intent);
    }

    // -------------------------------------------------------------------
    // 6) Generar PDF y guardarlo en la URI elegida
    private void guardarPdfEnUri(Uri uri) {

        String contenido =
                tvResumen.getText() + "\n\n" +
                        "DIETA:\n" + tvDieta.getText() + "\n\n" +
                        "RUTINA:\n" + tvRutina.getText();

        PdfDocument pdf = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(14);

        int x = 40;
        int y = 50;

        for (String linea : contenido.split("\n")) {
            canvas.drawText(linea, x, y, paint);
            y += 20;
        }

        pdf.finishPage(page);

        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            pdf.writeTo(out);
            Toast.makeText(this, "PDF guardado correctamente", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar PDF", Toast.LENGTH_SHORT).show();
        }

        pdf.close();
    }
}
