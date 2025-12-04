package com.example.pokemon_login_lgomdom.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dominio que representa un usuario autenticado en la aplicación.
 * Este modelo NO incluye la contraseña por seguridad, solo datos públicos del usuario.
 *
 * Se usa en toda la aplicación después de la autenticación.
 * La contraseña se maneja únicamente en la capa de datos (LocalUser).
 *
 * @property id Identificador único del usuario
 * @property email Correo electrónico del usuario (usado para login)
 * @property nombre Nombre completo del usuario
 * @property isAdmin Indica si el usuario tiene privilegios de administrador
 *                   Los admin pueden crear, editar y eliminar pokémon
 */
data class User(
    val id: Int = 0,
    val email: String = "",
    val nombre: String = "",
    @SerializedName("is_admin") val isAdmin: Boolean = false
)