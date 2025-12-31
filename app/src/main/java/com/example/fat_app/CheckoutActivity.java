package com.example.fat_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";

    private TextView txtPlanName, txtPlanDescription, txtPlanPrice;
    private Button btnFinalizarCompra;

    // Traemos la lógica de PaymentSheet aquí
    private PaymentSheet paymentSheet;
    // ... aquí irían las variables de Stripe si las usaras:
    // private String paymentIntentClientSecret;
    // private String customerEphemeralKeySecret;
    // private String customerId;

    private String selectedPlanName = ""; // Para el toast

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Inicializar vistas
        txtPlanName = findViewById(R.id.txtPlanName);
        txtPlanDescription = findViewById(R.id.txtPlanDescription);
        txtPlanPrice = findViewById(R.id.txtPlanPrice);
        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);

        // Inicializar PaymentSheet (como estaba en tu original)
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        // --- Recibir datos del Intent ---
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String planName = extras.getString("PLAN_NAME");
            double planPrice = extras.getDouble("PLAN_PRICE");
            String planDescription = extras.getString("PLAN_DESCRIPTION");

            selectedPlanName = planName; // Guardar para el toast

            // --- Poblar la UI con los datos ---
            txtPlanName.setText("Has seleccionado: " + planName);
            txtPlanDescription.setText(planDescription);

            // Formatear el precio
            String priceText = String.format(Locale.getDefault(), "$%.2f MXN / mes", planPrice);
            txtPlanPrice.setText(priceText);

            // Formatear el botón
            String buttonText = String.format(Locale.getDefault(), "Finalizar Compra - $%.2f", planPrice);
            btnFinalizarCompra.setText(buttonText);
        }
        // --- Fin de recibir datos ---


        // Asignar el click al botón de finalizar
        btnFinalizarCompra.setOnClickListener(v -> {
            // Aquí es donde llamarías a la lógica de Stripe:
            // presentPaymentSheet();

            // Por ahora, usamos tu simulación:
            simulatePaymentSuccess(selectedPlanName);
        });
    }

    // --- Lógica de pago (movida de PaymentActivity) ---

    // Tu simulación original
    private void simulatePaymentSuccess(String planName) {
        Toast.makeText(this, "Suscripción exitosa a " + planName, Toast.LENGTH_LONG).show();
        // Cierra esta actividad y regresa a la anterior
        finish();
    }

    // El callback de Stripe (movido de PaymentActivity)
    private void onPaymentResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Pago completado exitosamente", Toast.LENGTH_LONG).show();
            finish(); // Cierra al completar
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Error al procesar el pago", Toast.LENGTH_SHORT).show();
        }
    }


}