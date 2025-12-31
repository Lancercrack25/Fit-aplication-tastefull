package com.example.fat_app.api.model;

import java.util.List;

// Esto envuelve la lista de resultados de búsqueda
public class UsdaSearchResponse {
    private List<UsdaFood> foods;

    public List<UsdaFood> getFoods() {
        return foods;
    }
}