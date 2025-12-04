package com.example.pokemon_login_lgomdom.data

import android.content.Context
import com.example.pokemon_login_lgomdom.models.Pokemon
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Repositorio que gestiona el almacenamiento y recuperación de pokémon.
 * Utiliza SharedPreferences para persistencia simple y eficaz de los datos.
 *
 * La estrategia de inicialización carga pokémon predefinidos desde assets
 * en la primera ejecución, y luego mantiene los cambios del usuario entre sesiones.
 */
class LocalPokemonRepository(private val context: Context) {
    // SharedPreferences para almacenar la lista de pokémon en formato JSON
    private val prefs = context.getSharedPreferences("pokemon_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        // Inicialización: carga datos predefinidos solo si no hay datos guardados
        if (getPokemons().isEmpty()) {
            val initialPokemons = loadPokemonsFromAssets()
            savePokemons(initialPokemons)
        }
    }

    /**
     * Carga la lista inicial de pokémon desde el archivo assets/pokemons.json.
     * Este archivo contiene los datos base que se copian a SharedPreferences
     * la primera vez que se ejecuta la aplicación.
     *
     * @return Lista de pokémon predefinidos o lista vacía si hay error
     */
    private fun loadPokemonsFromAssets(): List<Pokemon> {
        return try {
            val jsonString = context.assets.open("pokemons.json")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<List<Pokemon>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Obtiene todos los pokémon almacenados en SharedPreferences.
     *
     * @return Lista actual de pokémon
     */
    fun getPokemons(): List<Pokemon> {
        val json = prefs.getString("pokemons", null) ?: return emptyList()
        val type = object : TypeToken<List<Pokemon>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    /**
     * Agrega un nuevo pokémon a la colección.
     * Genera automáticamente un ID único basado en el máximo ID existente.
     *
     * @param pokemon Pokémon a agregar (el ID se sobrescribirá)
     * @return true si la operación fue exitosa
     */
    fun addPokemon(pokemon: Pokemon): Boolean {
        val pokemons = getPokemons().toMutableList()
        // Genera un ID único incrementando el máximo existente
        val newId = (pokemons.maxOfOrNull { it.id } ?: 0) + 1
        pokemons.add(pokemon.copy(id = newId))
        savePokemons(pokemons)
        return true
    }

    /**
     * Actualiza los datos de un pokémon existente.
     * Busca el pokémon por ID y reemplaza sus datos.
     *
     * @param pokemon Pokémon con los datos actualizados (debe tener un ID válido)
     * @return true si se encontró y actualizó, false si no existe el ID
     */
    fun updatePokemon(pokemon: Pokemon): Boolean {
        val pokemons = getPokemons().toMutableList()
        val index = pokemons.indexOfFirst { it.id == pokemon.id }
        if (index != -1) {
            pokemons[index] = pokemon
            savePokemons(pokemons)
            return true
        }
        return false
    }

    /**
     * Elimina un pokémon de la colección por su ID.
     *
     * @param pokemonId ID del pokémon a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     */
    fun deletePokemon(pokemonId: Int): Boolean {
        val pokemons = getPokemons().toMutableList()
        val removed = pokemons.removeIf { it.id == pokemonId }
        if (removed) {
            savePokemons(pokemons)
        }
        return removed
    }

    /**
     * Guarda la lista completa de pokémon en SharedPreferences.
     * Serializa la lista a JSON antes de guardarla.
     *
     * @param pokemons Lista de pokémon a guardar
     */
    private fun savePokemons(pokemons: List<Pokemon>) {
        val json = gson.toJson(pokemons)
        prefs.edit().putString("pokemons", json).apply()
    }
}
