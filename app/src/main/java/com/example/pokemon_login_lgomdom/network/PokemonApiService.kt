package com.example.pokemon_login_lgomdom.network

import com.example.pokemon_login_lgomdom.models.Pokemon
import com.example.pokemon_login_lgomdom.models.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PokemonApiService {
    @GET("usuarios/{email}")
    suspend fun getUser(@Path("email") email: String): User

    @POST("usuarios")
    suspend fun createUser(@Body user: User): User

    @GET("pokemons")
    suspend fun getPokemons(): List<Pokemon>

    @POST("pokemons")
    suspend fun createPokemon(@Body pokemon: Pokemon): Pokemon

    @PUT("pokemons/{id}")
    suspend fun updatePokemon(@Path("id") id: Int, @Body pokemon: Pokemon): Pokemon

    @DELETE("pokemons/{id}")
    suspend fun deletePokemon(@Path("id") id: Int)
}

object RetrofitClient {
    private const val BASE_URL = "https://pokemon-login-backend.onrender.com/"

    val apiService: PokemonApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApiService::class.java)
    }
}
