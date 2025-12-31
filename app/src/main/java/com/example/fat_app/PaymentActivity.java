package com.example.fat_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Cambiado de RadioGroup
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.PaymentConfiguration;

public class PaymentActivity extends AppCompatActivity {

    // Ya no necesitamos RadioGroup, paymentSheet, etc. aquí
    private Button btnPremium, btnBusiness;

    // AÚN NECESITAMOS LA CLAVE PÚBLICA para inicializar
    private static final String STRIPE_PUBLISHABLE_KEY = "pk_test_TU_CLAVE_AQUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Inicializar Stripe (esto está bien hacerlo aquí)
        PaymentConfiguration.init(getApplicationContext(), STRIPE_PUBLISHABLE_KEY);

        // Encontrar los nuevos botones
        btnPremium = findViewById(R.id.btnPremium);
        btnBusiness = findViewById(R.id.btnBusiness);

        btnPremium.setOnClickListener(v -> {
            // Preparamos la información para la siguiente pantalla
            String planName = "Premium";
            double planPrice = 129.00;
            String planDescription = "Acceso completo a todas las rutinas, seguimiento de progreso avanzado y planes de nutrición personalizados.";

            // Llamamos a la nueva actividad de checkout
            launchCheckoutActivity(planName, planPrice, planDescription);
        });

        btnBusiness.setOnClickListener(v -> {
            // Preparamos la información para la siguiente pantalla
            String planName = "Business";
            double planPrice = 199.00;
            String planDescription = "Todos los beneficios Premium, más soporte prioritario 24/7 y acceso a coaches personales vía chat.";

            // Llamamos a la nueva actividad de checkout
            launchCheckoutActivity(planName, planPrice, planDescription);
        });
    }

    private void launchCheckoutActivity(String name, double price, String description) {
        // Creamos un Intent para abrir la nueva CheckoutActivity
        Intent intent = new Intent(PaymentActivity.this, CheckoutActivity.class);

        // Añadimos los "extras" para que la otra pantalla sepa qué plan elegimos
        intent.putExtra("PLAN_NAME", name);
        intent.putExtra("PLAN_PRICE", price);
        intent.putExtra("PLAN_DESCRIPTION", description);

        startActivity(intent);
    }

    // El resto del código (onPaymentResult, simulatePaymentSuccess, etc.)
    // se moverá a la nueva CheckoutActivity.
}