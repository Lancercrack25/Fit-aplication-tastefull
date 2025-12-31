package com.example.fat_app;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fat_app.api.model.UsdaFood;
import com.example.fat_app.api.model.UsdaFoodDetails;
import com.example.fat_app.api.model.UsdaNutrient;
import com.example.fat_app.api.model.UsdaSearchResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class Alimentacion extends AppCompatActivity {

    private static final String TAG = "Alimentacion";

    // Vistas
    private AutoCompleteTextView autoCompleteFoodName; // <-- ¡El AutoComplete!
    private EditText editGrams;
    private Button btnAddFood;
    private ListView listDailyLog;
    private TextView txtFoodInfo;

    // Variables
    private DatabaseHelper dbHelper;
    private String username;
    private long currentUserId = -1;

    // API de USDA
    private UsdaApiService apiService;
    private final String API_KEY = BuildConfig.USDA_API_KEY; // <-- ¡La llave segura!
    private List<UsdaFood> apiSearchResults;
    private ArrayAdapter<String> suggestionsAdapter;
    private UsdaFoodDetails selectedFoodDetails; // <-- Aquí guardamos los macros

    // Variables para guardar los macros por 100g
    private double kcal_100g = 0, prot_100g = 0, fat_100g = 0, carb_100g = 0;

    // Para la lista de registros
    private SimpleAdapter listAdapter;
    private ArrayList<HashMap<String, String>> foodEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alimentacion); // Tu XML corregido

        Log.d(TAG, "=== INICIANDO ALIMENTACION (CON API USDA) ===");

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupUser();
        setupApi(); // Configura Retrofit
        setupListeners(); // Configura el Autocomplete
        loadDailyEntries();

        Log.d(TAG, "=== ALIMENTACION INICIALIZADA ===");
    }

    private void initViews() {
        // Asegúrate que tu XML tiene este ID (te lo pasé corregido)
        autoCompleteFoodName = findViewById(R.id.autoCompleteFoodName);
        editGrams = findViewById(R.id.editGrams);
        btnAddFood = findViewById(R.id.btnAddFood);
        listDailyLog = findViewById(R.id.listDailyLog);
        txtFoodInfo = findViewById(R.id.txtFoodInfo);
        Log.d(TAG, "Vistas inicializadas");
    }

    private void setupUser() {
        try {
            Cursor cursor = dbHelper.getUserData(username);
            if (cursor != null && cursor.moveToFirst()) {
                currentUserId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                cursor.close();
                Log.d(TAG, "Usuario ID: " + currentUserId);
            } else {
                Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setupUser: " + e.getMessage());
        }
    }

    private void setupApi() {
        try {
            // Usamos los nuevos archivos que creamos
            apiService = ApiClient.getRetrofitInstance().create(UsdaApiService.class);
            apiSearchResults = new ArrayList<>();

            // Adapter para las sugerencias del dropdown
            suggestionsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line,
                    new ArrayList<String>());

            autoCompleteFoodName.setAdapter(suggestionsAdapter);
            autoCompleteFoodName.setThreshold(3); // Buscar a partir de 3 caracteres

            Log.d(TAG, "API USDA configurada");
        } catch (Exception e) {
            Log.e(TAG, "Error setupApi: " + e.getMessage());
            Toast.makeText(this, "API no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        // --- 1. Listener para buscar mientras escribes ---
        autoCompleteFoodName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                // Reseteamos los macros si el usuario cambia el texto
                selectedFoodDetails = null;
                txtFoodInfo.setVisibility(View.GONE);

                if (query.length() >= 3) {
                    searchFoodInApi(query); // API CALL 1 (Buscar)
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // --- 2. Listener para cuando seleccionas un item ---
        autoCompleteFoodName.setOnItemClickListener((parent, view, position, id) -> {
            try {
                // Obtenemos el alimento de nuestra lista de resultados
                UsdaFood selected = apiSearchResults.get(position);
                int fdcId = selected.getFdcId();
                Log.d(TAG, "Item seleccionado: " + selected.getDescription() + " (ID: " + fdcId + ")");

                // Ocultamos teclado y buscamos los macros
                hideKeyboard(view);
                getFoodDetails(fdcId); // API CALL 2 (Obtener Macros)

            } catch (Exception e) {
                Log.e(TAG, "Error en ItemClick: " + e.getMessage());
            }
        });

        // --- 3. Listener del botón "Registrar" ---
        btnAddFood.setOnClickListener(v -> {
            Log.d(TAG, "Botón 'Registrar' clickeado");
            registerFoodEntry();
        });

        // --- 4. Listener de la lista (copiado de tu código) ---
        listDailyLog.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(TAG, "Item clickeado: " + position);
            if (foodEntries != null && position < foodEntries.size()) {
                HashMap<String, String> entry = foodEntries.get(position);
                String entryId = entry.get("id"); // Tu helper usa "_id"
                String foodName = entry.get("name").split(" \\(")[0]; // Quita los gramos del nombre
                showEditDeleteDialog(Long.parseLong(entryId), foodName);
            }
        });
    }

    /**
     * API CALL 1: Busca alimentos que coincidan con el query.
     */
    private void searchFoodInApi(String query) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, " Sin conexión", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Buscando en API: " + query);
        Call<UsdaSearchResponse> call = apiService.searchFoods(query, API_KEY, 10); // Trae 10 resultados

        call.enqueue(new Callback<UsdaSearchResponse>() {
            @Override
            public void onResponse(Call<UsdaSearchResponse> call, Response<UsdaSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Limpiamos resultados anteriores
                    apiSearchResults.clear();
                    suggestionsAdapter.clear();

                    // Agregamos los nuevos
                    apiSearchResults.addAll(response.body().getFoods());

                    ArrayList<String> descriptions = new ArrayList<>();
                    for (UsdaFood food : apiSearchResults) {
                        descriptions.add(food.getDescription());
                    }

                    // Actualizamos el adapter del dropdown
                    suggestionsAdapter.addAll(descriptions);
                    suggestionsAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Resultados encontrados: " + descriptions.size());

                } else {
                    Log.e(TAG, "Error API (search) - Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UsdaSearchResponse> call, Throwable t) {
                Log.e(TAG, "Fallo API (search): " + t.getMessage());
            }
        });
    }

    /**
     * API CALL 2: Obtiene los macros del alimento seleccionado.
     */
    private void getFoodDetails(int fdcId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "🌐 Sin conexión", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "🔍 Obteniendo macros...", Toast.LENGTH_SHORT).show();
        txtFoodInfo.setVisibility(View.GONE);

        Call<UsdaFoodDetails> call = apiService.getFoodDetails(fdcId, API_KEY, "full");

        call.enqueue(new Callback<UsdaFoodDetails>() {
            @Override
            public void onResponse(Call<UsdaFoodDetails> call, Response<UsdaFoodDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedFoodDetails = response.body(); // ¡Guardamos los detalles!
                    Log.d(TAG, "Macros obtenidos para: " + selectedFoodDetails.getDescription());

                    // Procesamos y mostramos la info
                    parseAndShowFoodInfo(selectedFoodDetails);

                    // Movemos el foco a los gramos
                    editGrams.requestFocus();

                } else {
                    Log.e(TAG, "Error API (details) - Código: " + response.code());
                    Toast.makeText(Alimentacion.this, "❌ No se pudieron obtener macros", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsdaFoodDetails> call, Throwable t) {
                Log.e(TAG, "Fallo API (details): " + t.getMessage());
            }
        });
    }

    /**
     * Parsea la lista de nutrientes y la muestra en txtFoodInfo.
     * También guarda los macros en variables (kcal_100g, prot_100g, etc.)
     */
    private void parseAndShowFoodInfo(UsdaFoodDetails details) {
        // Reseteamos macros
        kcal_100g = 0; prot_100g = 0; fat_100g = 0; carb_100g = 0;

        if (details == null || details.getFoodNutrients() == null) return;

        for (UsdaNutrient nutrientWrapper : details.getFoodNutrients()) {
            UsdaNutrient.Nutrient nutrient = nutrientWrapper.getNutrient();
            if (nutrient == null) continue;

            String name = nutrient.getName();
            int id = nutrient.getId();
            double amount = nutrientWrapper.getAmount(); // Todos son por 100g

            // Identificamos los macros que nos importan
            // Usamos los IDs oficiales de USDA para más seguridad

            if (id == 1008) { // Energy (KCAL)
                kcal_100g = amount;
            } else if (id == 1003) { // Protein
                prot_100g = amount;
            } else if (id == 1004) { // Total lipid (fat)
                fat_100g = amount;
            } else if (id == 1005) { // Carbohydrate, by difference
                carb_100g = amount;
            }
        }

        // Mostramos la info (tal como pediste en tu prompt original)
        String info = String.format(Locale.US,
                "Info (x100g):\n💪 Prot: %.1fg | 🔥 Kcal: %.1f | 🍞 Carbos: %.1fg | 🥑 Grasas: %.1fg",
                prot_100g, kcal_100g, carb_100g, fat_100g);

        txtFoodInfo.setText(info);
        txtFoodInfo.setVisibility(View.VISIBLE);
        Log.d(TAG, "Macros parseados (x100g): Kcal=" + kcal_100g + ", P=" + prot_100g);
    }


    /**
     * Valida los campos y guarda en la BD.
     */
    private void registerFoodEntry() {
        String gramsStr = editGrams.getText().toString().trim();

        // Validaciones
        if (selectedFoodDetails == null) {
            Toast.makeText(this, "❌ Selecciona un alimento de la lista primero", Toast.LENGTH_LONG).show();
            autoCompleteFoodName.requestFocus();
            return;
        }

        if (gramsStr.isEmpty()) {
            Toast.makeText(this, "❌ Ingresa la cantidad de gramos", Toast.LENGTH_SHORT).show();
            editGrams.requestFocus();
            return;
        }

        try {
            double grams = Double.parseDouble(gramsStr);
            if (grams <= 0) {
                Toast.makeText(this, "❌ Los gramos deben ser mayor a 0", Toast.LENGTH_SHORT).show();
                editGrams.requestFocus();
                return;
            }

            // --- ¡AQUÍ ESTÁ EL CÁLCULO! ---
            double factor = grams / 100.0;

            double totalCalories = kcal_100g * factor;
            double totalProteins = prot_100g * factor;
            double totalFats = fat_100g * factor;
            double totalCarbs = carb_100g * factor;

            String foodName = selectedFoodDetails.getDescription();
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Usamos tu método del DatabaseHelper
            boolean success = dbHelper.insertFoodEntry(
                    currentUserId, todayDate, foodName, grams,
                    totalCalories, totalProteins, totalFats, totalCarbs // ¡Guardamos todo!
            );

            if (success) {
                Toast.makeText(this, "✅ " + foodName + " registrado!", Toast.LENGTH_SHORT).show();

                // Limpiar campos
                autoCompleteFoodName.setText("");
                editGrams.setText("");
                selectedFoodDetails = null;
                txtFoodInfo.setVisibility(View.GONE);
                loadDailyEntries(); // Recargar la lista

            } else {
                Toast.makeText(this, "❌ Error al guardar en BD", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "❌ Cantidad de gramos no válida", Toast.LENGTH_SHORT).show();
            editGrams.requestFocus();
        } catch (Exception e) {
            Log.e(TAG, "Error registrando: " + e.getMessage());
            Toast.makeText(this, "❌ Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }

    // ---
    // --- MÉTODOS DE TU CÓDIGO ORIGINAL (SIN CAMBIOS) ---
    // --- (Funcionan perfecto con tu DatabaseHelper) ---
    // ---

    private void loadDailyEntries() {
        try {
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            // Tu método getDailyEntries ya funciona
            Cursor cursor = dbHelper.getDailyEntries(currentUserId, todayDate);

            foodEntries = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, String> entry = new HashMap<>();
                    // El alias "_id" que pusiste en tu helper es clave para esto
                    entry.put("id", cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENTRY_FOOD_NAME));
                    double grams = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENTRY_GRAMS));

                    // Esto se muestra en la lista (Nombre y Calorías)
                    entry.put("name", String.format(Locale.US, "%s (%.0fg)", name, grams));
                    entry.put("calories", String.format(Locale.US, "%.1f Kcal",
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENTRY_CALORIES))));

                    foodEntries.add(entry);
                } while (cursor.moveToNext());

                cursor.close();
            }

            // El adapter solo usa "name" y "calories", justo como querías.
            listAdapter = new SimpleAdapter(this, foodEntries,
                    android.R.layout.simple_list_item_2,
                    new String[]{"name", "calories"},
                    new int[]{android.R.id.text1, android.R.id.text2});

            listDailyLog.setAdapter(listAdapter);
            Log.d(TAG, "Registros cargados: " + foodEntries.size());

        } catch (Exception e) {
            Log.e(TAG, "Error loadDailyEntries: " + e.getMessage());
            Toast.makeText(this, "Error al cargar registros", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDeleteDialog(final long entryId, String foodName) {
        String[] options = {"✏️ Editar Gramos", "🗑️ Eliminar", "❌ Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(foodName);
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Editar
                    showEditGramsDialog(entryId, foodName);
                    break;
                case 1: // Eliminar
                    showDeleteConfirmation(entryId, foodName);
                    break;
            }
        });
        builder.show();
    }

    private void showEditGramsDialog(final long entryId, String foodName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar: " + foodName);

        final EditText input = new EditText(this);
        input.setHint("Nuevos gramos...");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("💾 Guardar", (dialog, which) -> {
            String newGramsStr = input.getText().toString().trim();
            if (!newGramsStr.isEmpty()) {
                try {
                    double newGrams = Double.parseDouble(newGramsStr);
                    if (newGrams > 0) {
                        updateFoodEntry(entryId, newGrams);
                    } else {
                        Toast.makeText(this, "Los gramos deben ser mayor a 0", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Gramos no válidos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("❌ Cancelar", null);
        builder.show();
    }

    // Este método tuyo recalcula proporcionalmente, lo cual es perfecto.
    private void updateFoodEntry(long entryId, double newGrams) {
        try {
            // Usamos un cursor para obtener la fila completa
            Cursor cursor = dbHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_FOOD_ENTRIES,
                    null, // todas las columnas
                    DatabaseHelper.COL_ENTRY_ID + " = ?",
                    new String[]{String.valueOf(entryId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                double oldGrams = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENTRY_GRAMS));
                double oldCalories = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENTRY_CALORIES));
                // Obtenemos los macros que SÍ guardaste
                double oldProteins = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENTRY_PROTEINS));
                double oldFats = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENTRY_FATS));
                double oldCarbs = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ENTRY_CARBS));
                cursor.close();

                double newCalories = 0, newProteins = 0, newFats = 0, newCarbs = 0;

                // Recalcular proporcionalmente
                if (oldGrams > 0) {
                    double factor = newGrams / oldGrams;
                    newCalories = oldCalories * factor;
                    newProteins = oldProteins * factor;
                    newFats = oldFats * factor;
                    newCarbs = oldCarbs * factor;
                } else {
                    // Si los gramos originales eran 0, no podemos recalcular
                    newCalories = oldCalories;
                    newProteins = oldProteins;
                    newFats = oldFats;
                    newCarbs = oldCarbs;
                }

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COL_ENTRY_GRAMS, newGrams);
                values.put(DatabaseHelper.COL_ENTRY_CALORIES, newCalories);
                values.put(DatabaseHelper.COL_ENTRY_PROTEINS, newProteins);
                values.put(DatabaseHelper.COL_ENTRY_FATS, newFats);
                values.put(DatabaseHelper.COL_ENTRY_CARBS, newCarbs);

                int rows = dbHelper.getWritableDatabase().update(
                        DatabaseHelper.TABLE_FOOD_ENTRIES,
                        values,
                        DatabaseHelper.COL_ENTRY_ID + " = ?",
                        new String[]{String.valueOf(entryId)});

                if (rows > 0) {
                    Toast.makeText(this, "✅ Gramos actualizados", Toast.LENGTH_SHORT).show();
                    loadDailyEntries();
                } else {
                    Toast.makeText(this, "❌ Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updateFoodEntry: " + e.getMessage());
            Toast.makeText(this, "❌ Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation(final long entryId, String foodName) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Eliminar '" + foodName + "'?")
                .setPositiveButton("✅ SÍ, ELIMINAR", (dialog, which) -> {
                    deleteFoodEntry(entryId);
                })
                .setNegativeButton("❌ CANCELAR", null)
                .show();
    }

    private void deleteFoodEntry(long entryId) {
        try {
            int rows = dbHelper.getWritableDatabase().delete(
                    DatabaseHelper.TABLE_FOOD_ENTRIES,
                    DatabaseHelper.COL_ENTRY_ID + " = ?",
                    new String[]{String.valueOf(entryId)});

            if (rows > 0) {
                Toast.makeText(this, "🗑️ Registro eliminado", Toast.LENGTH_SHORT).show();
                loadDailyEntries();
            } else {
                Toast.makeText(this, "❌ Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleteFoodEntry: " + e.getMessage());
            Toast.makeText(this, "❌ Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
