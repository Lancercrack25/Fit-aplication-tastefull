package com.example.fat_app.api.model;

// Esto representa UN solo nutriente (ej: "Proteína", "10.5")
public class UsdaNutrient {

    private Nutrient nutrient;
    private double amount;

    public Nutrient getNutrient() {
        return nutrient;
    }

    public double getAmount() {
        return amount;
    }

    // Clase interna porque el JSON está anidado
    public static class Nutrient {
        private String name;
        private int id; // El ID del nutriente (ej: 1008 para Kcal)

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}