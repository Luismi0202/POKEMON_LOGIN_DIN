package com.example.pokemon_login_lgomdom.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon_login_lgomdom.models.User
import com.example.pokemon_login_lgomdom.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

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

    fun register(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                _errorMessage.value = null

                auth.createUserWithEmailAndPassword(email, password).await()

                val createdUser = RetrofitClient.apiService.createUser(
                    User(email = email, nombre = nombre, isAdmin = false)
                )
                _currentUser.value = createdUser
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                // Rollback: eliminar usuario de Firebase
                try {
                    auth.currentUser?.delete()?.await()
                } catch (_: Exception) { }

                val msg = when (e) {
                    is HttpException -> when (e.code()) {
                        400 -> "Error 400: Verifica el formato del body (${e.response()?.errorBody()?.string()})"
                        409 -> "El email ya existe en la base de datos."
                        else -> "Error del servidor: ${e.code()}"
                    }
                    else -> translateError(e)
                }
                _errorMessage.value = msg
                _authState.value = AuthState.Error(msg)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                _errorMessage.value = null

                auth.signInWithEmailAndPassword(email, password).await()

                val user = RetrofitClient.apiService.getUser(email)
                _currentUser.value = user
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                val msg = when (e) {
                    is HttpException -> {
                        if (e.code() == 404) "Usuario no encontrado en la base de datos."
                        else "Error del servidor: ${e.code()}"
                    }
                    else -> translateError(e)
                }
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

                auth.sendPasswordResetEmail(email).await()

                _authState.value = AuthState.PasswordResetSent
            } catch (e: Exception) {
                val msg = translateError(e)
                _errorMessage.value = msg
                _authState.value = AuthState.Error(msg)
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Idle
        _errorMessage.value = null
    }

    private fun translateError(e: Exception): String {
        val msg = e.message ?: "Error desconocido"
        return when {
            msg.contains("timeout", true) -> "Error de conexión. Verifica tu internet o el backend."
            msg.contains("email-already-in-use", true) -> "Este email ya está registrado en Firebase."
            msg.contains("weak-password", true) -> "La contraseña debe tener al menos 6 caracteres."
            msg.contains("invalid-email", true) -> "Email inválido."
            msg.contains("user-not-found", true) -> "Usuario no encontrado."
            msg.contains("wrong-password", true) -> "Contraseña incorrecta."
            else -> "Error: $msg"
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        object PasswordResetSent : AuthState()
        data class Error(val message: String) : AuthState()
    }
}