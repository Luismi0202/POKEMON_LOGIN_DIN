package com.example.pokemon_login_lgomdom.data

import android.content.Context
import com.example.pokemon_login_lgomdom.models.Pokemon
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class LocalPokemonRepository(private val context: Context) {
    private val gson = Gson()
    private var pokemonsList = mutableListOf<Pokemon>()

    init {
        loadPokemons()
    }

    private fun loadPokemons() {
        try {
            val inputStream = context.assets.open("pokemons.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Pokemon>>() {}.type
            pokemonsList = gson.fromJson<List<Pokemon>>(reader, type).toMutableList()
            reader.close()
        } catch (e: Exception) {
            pokemonsList = mutableListOf()
        }
    }

    fun getAllPokemons(): List<Pokemon> = pokemonsList.toList()

    fun getPokemonById(id: Int): Pokemon? = pokemonsList.find { it.id == id }

    fun addPokemon(pokemon: Pokemon): Pokemon {
        val newId = (pokemonsList.maxOfOrNull { it.id } ?: 0) + 1
        val newPokemon = pokemon.copy(id = newId)
        pokemonsList.add(newPokemon)
        return newPokemon
    }

    fun updatePokemon(id: Int, pokemon: Pokemon): Pokemon? {
        val index = pokemonsList.indexOfFirst { it.id == id }
        return if (index != -1) {
            val updatedPokemon = pokemon.copy(id = id)
            pokemonsList[index] = updatedPokemon
            updatedPokemon
        } else null
    }

    fun deletePokemon(id: Int): Boolean {
        return pokemonsList.removeIf { it.id == id }
    }
}
