package com.example.fat_app;

import com.example.fat_app.api.model.UsdaFoodDetails;
import com.example.fat_app.api.model.UsdaSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsdaApiService {

    // 1. Para buscar alimentos (para el autocompletar)
    @GET("foods/search")
    Call<UsdaSearchResponse> searchFoods(
            @Query("query") String query,
            @Query("api_key") String apiKey,
            @Query("pageSize") int pageSize // Para limitar el número de resultados
    );

    // 2. Para obtener los detalles (macros) de UN solo alimento
    @GET("food/{fdcId}")
    Call<UsdaFoodDetails> getFoodDetails(
            @Path("fdcId") int fdcId,
            @Query("api_key") String apiKey,
            @Query("format") String format // Pedimos el formato "full"
    );
}