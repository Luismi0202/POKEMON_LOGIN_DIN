package com.example.pokemon_login_lgomdom.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon_login_lgomdom.data.LocalUserRepository
import com.example.pokemon_login_lgomdom.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de gestionar toda la lógica de autenticación de la aplicación.
 * Maneja login, registro, recuperación de contraseña y el estado del usuario actual.
 *
 * Utiliza StateFlow para mantener un estado observable que las pantallas pueden consumir.
 * Las operaciones asíncronas se ejecutan en viewModelScope para mantener el ciclo de vida adecuado.
 */
class AuthViewModel(context: Context) : ViewModel() {
    // Repositorio que maneja la persistencia y validación de usuarios
    private val userRepository = LocalUserRepository(context)

    // Estado privado mutable del usuario actual (null cuando no hay sesión activa)
    private val _currentUser = MutableStateFlow<User?>(null)
    // Exposición pública inmutable del estado del usuario
    val currentUser: StateFlow<User?> = _currentUser

    // Estado privado del proceso de autenticación (Loading, Success, Error, etc.)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Mensajes de error para mostrar al usuario
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /**
     * Limpia el mensaje de error actual.
     * Útil cuando el usuario comienza a corregir un formulario.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Establece un error de validación de formulario.
     * @param msg Mensaje descriptivo del error
     */
    fun setFormError(msg: String) {
        _errorMessage.value = msg
        _authState.value = AuthState.Error(msg)
    }

    /**
     * Inicia sesión con las credenciales proporcionadas.
     * Valida contra usuarios predefinidos y registrados localmente.
     *
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                _errorMessage.value = null

                // Valida credenciales contra el repositorio local
                val localUser = userRepository.validateCredentials(email, password)

                if (localUser != null) {
                    // Convierte LocalUser a User (modelo de dominio)
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

    /**
     * Registra un nuevo usuario en el sistema.
     * Guarda los datos en persistencia local para que el usuario pueda iniciar sesión posteriormente.
     *
     * @param email Correo electrónico del nuevo usuario
     * @param password Contraseña del nuevo usuario
     * @param nombre Nombre completo del nuevo usuario
     */
    fun register(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                _errorMessage.value = null

                // Verifica que el email no esté ya registrado
                if (userRepository.emailExists(email)) {
                    val msg = "Este email ya está registrado"
                    _errorMessage.value = msg
                    _authState.value = AuthState.Error(msg)
                    return@launch
                }

                // Registra el nuevo usuario en persistencia local (archivo JSON)
                val registered = userRepository.registerNewUser(email, password, nombre)

                if (registered) {
                    // Recupera el usuario recién creado para iniciar sesión automáticamente
                    val newUser = userRepository.findUserByEmail(email)
                    if (newUser != null) {
                        _currentUser.value = User(
                            id = newUser.id,
                            email = newUser.email,
                            nombre = newUser.nombre,
                            isAdmin = newUser.isAdmin
                        )
                        _authState.value = AuthState.Success
                    } else {
                        val msg = "Error al registrar usuario"
                        _errorMessage.value = msg
                        _authState.value = AuthState.Error(msg)
                    }
                } else {
                    val msg = "Error al registrar usuario"
                    _errorMessage.value = msg
                    _authState.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                val msg = "Error al registrarse: ${e.message}"
                _errorMessage.value = msg
                _authState.value = AuthState.Error(msg)
            }
        }
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     * Valida que el email exista en el sistema.
     *
     * @param email Correo electrónico del usuario que olvidó su contraseña
     */
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

    /**
     * Cierra la sesión del usuario actual.
     * Limpia el estado del usuario y restablece el estado de autenticación.
     */
    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
        _errorMessage.value = null
    }

    /**
     * Clase sealed que representa los diferentes estados del proceso de autenticación.
     * Permite manejar de forma segura todas las transiciones de estado posibles.
     */
    sealed class AuthState {
        object Idle : AuthState()              // Estado inicial, sin operación en curso
        object Loading : AuthState()           // Operación en progreso
        object Success : AuthState()           // Operación completada exitosamente
        object PasswordResetSent : AuthState() // Email de recuperación enviado
        data class Error(val message: String) : AuthState() // Error con mensaje descriptivo
    }
}
