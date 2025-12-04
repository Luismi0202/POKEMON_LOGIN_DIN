package com.example.pokemon_login_lgomdom.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Gestor centralizado de almacenamiento en archivos para persistencia local de datos.
 * Proporciona una solución simple y eficaz de persistencia sin necesidad de bases de datos
 * complejas o frameworks de inyección de dependencias.
 *
 * Los datos se almacenan en formato JSON en el directorio interno de la aplicación,
 * garantizando que son privados y no accesibles por otras aplicaciones.
 *
 * Ubicación de almacenamiento: /data/data/{package_name}/files/
 */
class FileStorageManager(private val context: Context) {
    // Gson para serialización/deserialización JSON
    private val gson = Gson()

    // Directorio raíz de archivos internos de la aplicación
    private val filesDir: File = context.filesDir

    // Archivo donde se guardan los usuarios registrados dinámicamente
    private val registeredUsersFile: File = File(filesDir, "registered_users.json")

    /**
     * Obtiene la lista completa de usuarios que se han registrado en la aplicación.
     * Los usuarios predefinidos (desde assets) no se incluyen aquí.
     *
     * @return Lista de usuarios registrados, o lista vacía si no hay ninguno
     */
    fun getRegisteredUsers(): List<LocalUser> {
        return try {
            if (!registeredUsersFile.exists()) {
                return emptyList()
            }

            val reader = FileReader(registeredUsersFile)
            val type = object : TypeToken<List<LocalUser>>() {}.type
            val users: List<LocalUser> = gson.fromJson(reader, type) ?: emptyList()
            reader.close()
            users
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Guarda la lista completa de usuarios registrados en el archivo JSON.
     * Sobrescribe el contenido anterior del archivo.
     *
     * @param users Lista de usuarios a guardar
     */
    fun saveRegisteredUsers(users: List<LocalUser>) {
        try {
            val json = gson.toJson(users)
            val writer = FileWriter(registeredUsersFile)
            writer.write(json)
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Agrega un nuevo usuario a la lista de usuarios registrados.
     * Valida que el email no esté duplicado antes de agregar.
     *
     * @param user Usuario a registrar
     * @return true si se agregó correctamente, false si el email ya existe
     */
    fun addRegisteredUser(user: LocalUser): Boolean {
        return try {
            val currentUsers = getRegisteredUsers().toMutableList()

            // Validación de email único
            if (currentUsers.any { it.email.equals(user.email, ignoreCase = true) }) {
                return false
            }

            currentUsers.add(user)
            saveRegisteredUsers(currentUsers)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Busca un usuario registrado por su dirección de email.
     * La búsqueda es case-insensitive.
     *
     * @param email Email del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    fun getRegisteredUserByEmail(email: String): LocalUser? {
        return getRegisteredUsers().find { it.email.equals(email, ignoreCase = true) }
    }

    /**
     * Valida las credenciales de un usuario registrado.
     * Compara email (case-insensitive) y contraseña (case-sensitive).
     *
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Usuario si las credenciales son válidas, null en caso contrario
     */
    fun validateRegisteredUserCredentials(email: String, password: String): LocalUser? {
        return getRegisteredUsers().find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    /**
     * Verifica si un email ya está registrado en el sistema.
     *
     * @param email Email a verificar
     * @return true si el email ya existe, false en caso contrario
     */
    fun isEmailRegistered(email: String): Boolean {
        return getRegisteredUsers().any { it.email.equals(email, ignoreCase = true) }
    }

    /**
     * Calcula y retorna el próximo ID disponible para un nuevo usuario.
     * Los IDs comienzan en 101 para usuarios registrados (los predefinidos usan 1-100).
     *
     * @return Próximo ID único disponible
     */
    fun getNextUserId(): Int {
        val allUsers = getRegisteredUsers()
        return (allUsers.maxOfOrNull { it.id } ?: 100) + 1
    }
}

