package com.grupo3.pokemon;

import retrofit2.http.GET;
import retrofit2.Call;

public interface PokemonAPIService {

    @GET("pokemon/?limit=50")
    Call<PokemonFetchResults> getPokemons();
}
