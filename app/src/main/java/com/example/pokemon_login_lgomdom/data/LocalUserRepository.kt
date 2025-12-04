package com.example.pokemon_login_lgomdom.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * Modelo de datos para usuarios almacenados localmente.
 * Incluye la contraseña para validación de credenciales (no se expone al resto de la app).
 */
data class LocalUser(
    val id: Int,
    val email: String,
    val nombre: String,
    val password: String,      // Solo se usa en la capa de datos
    val isAdmin: Boolean
)

/**
 * Repositorio que gestiona el acceso a usuarios desde múltiples fuentes.
 * Combina usuarios predefinidos (desde archivo assets) con usuarios registrados
 * dinámicamente (almacenados en archivos internos).
 *
 * Esta arquitectura permite:
 * - Tener usuarios de prueba/admin predefinidos en el código
 * - Permitir que usuarios nuevos se registren y persistan entre sesiones
 * - Mantener una única interfaz de acceso para toda la aplicación
 */
class LocalUserRepository(private val context: Context) {
    private val gson = Gson()
    // Gestor de persistencia para usuarios registrados dinámicamente
    private val fileStorage = FileStorageManager(context)

    /**
     * Obtiene los usuarios predefinidos desde el archivo assets/usuarios.json.
     * Estos usuarios son parte del APK y no pueden modificarse en runtime.
     * Útil para tener usuarios de prueba o cuentas administrativas iniciales.
     *
     * @return Lista de usuarios predefinidos
     */
    private fun getPredefinedUsers(): List<LocalUser> {
        return try {
            val inputStream = context.assets.open("usuarios.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<LocalUser>>() {}.type
            val users: List<LocalUser> = gson.fromJson(reader, type)
            reader.close()
            users
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Obtiene todos los usuarios del sistema (predefinidos + registrados).
     *
     * @return Lista combinada de todos los usuarios
     */
    fun getUsers(): List<LocalUser> {
        val predefinedUsers = getPredefinedUsers()
        val registeredUsers = fileStorage.getRegisteredUsers()
        return predefinedUsers + registeredUsers
    }

    /**
     * Busca un usuario por su email en todas las fuentes de datos.
     * Prioriza usuarios predefinidos sobre registrados.
     *
     * @param email Email del usuario a buscar (case-insensitive)
     * @return Usuario encontrado o null si no existe
     */
    fun findUserByEmail(email: String): LocalUser? {
        // Buscar primero en usuarios predefinidos
        val predefinedUser = getPredefinedUsers().find { it.email.equals(email, ignoreCase = true) }
        if (predefinedUser != null) {
            return predefinedUser
        }

        // Luego en usuarios registrados
        return fileStorage.getRegisteredUserByEmail(email)
    }

    /**
     * Valida las credenciales de un usuario contra todas las fuentes.
     * Compara email y contraseña de forma segura.
     *
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return Usuario si las credenciales son válidas, null en caso contrario
     */
    fun validateCredentials(email: String, password: String): LocalUser? {
        // Validar contra usuarios predefinidos
        val predefinedUser = getPredefinedUsers().find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
        if (predefinedUser != null) {
            return predefinedUser
        }

        // Validar contra usuarios registrados
        return fileStorage.validateRegisteredUserCredentials(email, password)
    }

    /**
     * Verifica si un email ya está en uso en cualquiera de las fuentes.
     *
     * @param email Email a verificar
     * @return true si el email ya existe, false en caso contrario
     */
    fun emailExists(email: String): Boolean {
        return findUserByEmail(email) != null
    }

    /**
     * Registra un nuevo usuario en el sistema con persistencia local.
     * El usuario se guarda en un archivo JSON para mantener sus datos entre sesiones.
     * Los nuevos usuarios siempre se crean sin privilegios de administrador.
     *
     * @param email Email del nuevo usuario
     * @param password Contraseña del nuevo usuario
     * @param nombre Nombre completo del nuevo usuario
     * @return true si el registro fue exitoso, false si el email ya existe
     */
    fun registerNewUser(email: String, password: String, nombre: String): Boolean {
        // Verificar que el email no exista en ninguna fuente
        if (emailExists(email)) {
            return false
        }

        val newUser = LocalUser(
            id = fileStorage.getNextUserId(),
            email = email,
            nombre = nombre,
            password = password,
            isAdmin = false  // Los usuarios registrados nunca son admin por defecto
        )

        return fileStorage.addRegisteredUser(newUser)
    }
}
