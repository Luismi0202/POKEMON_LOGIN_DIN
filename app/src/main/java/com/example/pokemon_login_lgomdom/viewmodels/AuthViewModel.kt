package com.example.pokemon_login_lgomdom.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon_login_lgomdom.data.LocalUserRepository
import com.example.pokemon_login_lgomdom.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(context: Context) : ViewModel() {
    private val userRepository = LocalUserRepository(context)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() { _errorMessage.value = null }

    fun setFormError(msg: String) {
        _errorMessage.value = msg
        _authState.value = AuthState.Error(msg)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                _errorMessage.value = null

                val localUser = userRepository.validateCredentials(email, password)

                if (localUser != null) {
                    _currentUser.value = User(
                        id = localUser.id,
                        email = localUser.email,
                        nombre = localUser.nombre,
                        isAdmin = localUser.isAdmin
                    )
                    _authState.value = AuthState.Success
                } else {
                    val msg = "Email o contraseña incorrectos"
                    _errorMessage.value = msg
                    _authState.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                val msg = "Error al iniciar sesión: ${e.message}"
                _errorMessage.value = msg
                _authState.value = AuthState.Error(msg)
            }
        }
    }

    fun register(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                _errorMessage.value = null

                if (userRepository.emailExists(email)) {
                    val msg = "Este email ya está registrado"
                    _errorMessage.value = msg
                    _authState.value = AuthState.Error(msg)
                    return@launch
                }

                // Crear usuario sin privilegios de admin
                _currentUser.value = User(
                    id = (userRepository.getUsers().maxOfOrNull { it.id } ?: 0) + 1,
                    email = email,
                    nombre = nombre,
                    isAdmin = false
                )
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                val msg = "Error al registrarse: ${e.message}"
                _errorMessage.value = msg
                _authState.value = AuthState.Error(msg)
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                _errorMessage.value = null

                if (userRepository.emailExists(email)) {
                    _authState.value = AuthState.PasswordResetSent
                } else {
                    val msg = "Usuario no encontrado"
                    _errorMessage.value = msg
                    _authState.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                val msg = "Error: ${e.message}"
                _errorMessage.value = msg
                _authState.value = AuthState.Error(msg)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
        _errorMessage.value = null
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        object PasswordResetSent : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
