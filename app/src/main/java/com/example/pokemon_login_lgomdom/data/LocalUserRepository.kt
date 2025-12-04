package com.example.pokemon_login_lgomdom.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

data class LocalUser(
    val id: Int,
    val email: String,
    val nombre: String,
    val password: String,
    val isAdmin: Boolean
)

class LocalUserRepository(private val context: Context) {
    private val gson = Gson()

    fun getUsers(): List<LocalUser> {
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

    fun findUserByEmail(email: String): LocalUser? {
        return getUsers().find { it.email.equals(email, ignoreCase = true) }
    }

    fun validateCredentials(email: String, password: String): LocalUser? {
        return getUsers().find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    fun emailExists(email: String): Boolean {
        return findUserByEmail(email) != null
    }
}
