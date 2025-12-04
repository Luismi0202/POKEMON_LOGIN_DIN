package com.example.pokemon_login_lgomdom.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon_login_lgomdom.data.LocalPokemonRepository
import com.example.pokemon_login_lgomdom.models.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel(context: Context) : ViewModel() {
    private val pokemonRepository = LocalPokemonRepository(context)

    private val _pokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemons: StateFlow<List<Pokemon>> = _pokemons

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadPokemons()
    }

    fun loadPokemons(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _pokemons.value = pokemonRepository.getAllPokemons()
            } catch (e: Exception) {
                // Manejo de error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPokemon(pokemon: Pokemon) {
        viewModelScope.launch {
            try {
                pokemonRepository.addPokemon(pokemon)
                loadPokemons(forceRefresh = true)
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    fun updatePokemon(id: Int, pokemon: Pokemon) {
        viewModelScope.launch {
            try {
                pokemonRepository.updatePokemon(id, pokemon)
                loadPokemons(forceRefresh = true)
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    fun deletePokemon(id: Int) {
        viewModelScope.launch {
            try {
                pokemonRepository.deletePokemon(id)
                loadPokemons(forceRefresh = true)
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }
}
