package com.example.fat_app.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Esto es para la respuesta de detalles (la que trae los macros)
public class UsdaFoodDetails {
    private String description;

    @SerializedName("foodNutrients")
    private List<UsdaNutrient> foodNutrients;

    public String getDescription() {
        return description;
    }

    public List<UsdaNutrient> getFoodNutrients() {
        return foodNutrients;
    }
}