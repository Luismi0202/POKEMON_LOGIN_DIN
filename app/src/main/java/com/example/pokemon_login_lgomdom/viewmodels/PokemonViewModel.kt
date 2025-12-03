
package com.example.pokemon_login_lgomdom.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon_login_lgomdom.models.Pokemon
import com.example.pokemon_login_lgomdom.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {
    private val _pokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemons: StateFlow<List<Pokemon>> = _pokemons

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadPokemons()
    }

    fun loadPokemons() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _pokemons.value = RetrofitClient.apiService.getPokemons()
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
                RetrofitClient.apiService.createPokemon(pokemon)
                loadPokemons()
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    fun updatePokemon(id: Int, pokemon: Pokemon) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.updatePokemon(id, pokemon)
                loadPokemons()
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    fun deletePokemon(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deletePokemon(id)
                loadPokemons()
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }
}
