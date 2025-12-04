package com.example.pokemon_login_lgomdom.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon_login_lgomdom.data.LocalPokemonRepository
import com.example.pokemon_login_lgomdom.models.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona todas las operaciones relacionadas con la Pokédex.
 * Proporciona funcionalidad CRUD completa (Crear, Leer, Actualizar, Eliminar) para pokémon.
 *
 * Mantiene un estado observable de la lista de pokémon que se sincroniza automáticamente
 * con el almacenamiento local mediante SharedPreferences.
 */
class PokemonViewModel(context: Context) : ViewModel() {
    // Repositorio que maneja la persistencia local de pokémon
    private val repository = LocalPokemonRepository(context)

    // Estado privado de la lista de pokémon
    private val _pokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    // Exposición pública inmutable de la lista
    val pokemons: StateFlow<List<Pokemon>> = _pokemons

    // Indica si hay una operación en curso (para mostrar indicadores de carga)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Mensajes de error para operaciones fallidas
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        // Carga inicial de pokémon al crear el ViewModel
        loadPokemons()
    }

    /**
     * Carga todos los pokémon desde el repositorio local.
     * Se ejecuta de forma asíncrona en el scope del ViewModel.
     */
    fun loadPokemons() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _pokemons.value = repository.getPokemons()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar Pokémon: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Agrega un nuevo pokémon a la Pokédex.
     * El ID se asigna automáticamente en el repositorio.
     *
     * @param pokemon Objeto pokémon a agregar (el ID será generado automáticamente)
     */
    fun addPokemon(pokemon: Pokemon) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.addPokemon(pokemon)
                // Recarga la lista completa para reflejar los cambios
                loadPokemons()
            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar Pokémon: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza un pokémon existente en la Pokédex.
     *
     * @param pokemon Objeto pokémon con los datos actualizados (debe incluir un ID válido)
     */
    fun updatePokemon(pokemon: Pokemon) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.updatePokemon(pokemon)
                loadPokemons()
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar Pokémon: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina un pokémon de la Pokédex.
     *
     * @param pokemonId ID único del pokémon a eliminar
     */
    fun deletePokemon(pokemonId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.deletePokemon(pokemonId)
                loadPokemons()
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar Pokémon: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia el mensaje de error actual.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
