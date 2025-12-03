
package com.example.pokemon_login_lgomdom.models

data class Pokemon(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val tipos: List<String>,
    val imagenUrl: String,
    val habilidades: List<String>? = null,
    val wikiUrl: String? = null
)

enum class PokemonType(val color: androidx.compose.ui.graphics.Color) {
    NORMAL(androidx.compose.ui.graphics.Color(0xFFA8A878)),
    FUEGO(androidx.compose.ui.graphics.Color(0xFFF08030)),
    AGUA(androidx.compose.ui.graphics.Color(0xFF6890F0)),
    PLANTA(androidx.compose.ui.graphics.Color(0xFF78C850)),
    ELÉCTRICO(androidx.compose.ui.graphics.Color(0xFFF8D030)),
    HIELO(androidx.compose.ui.graphics.Color(0xFF98D8D8)),
    LUCHA(androidx.compose.ui.graphics.Color(0xFFC03028)),
    VENENO(androidx.compose.ui.graphics.Color(0xFFA040A0)),
    TIERRA(androidx.compose.ui.graphics.Color(0xFFE0C068)),
    VOLADOR(androidx.compose.ui.graphics.Color(0xFFA890F0)),
    PSÍQUICO(androidx.compose.ui.graphics.Color(0xFFF85888)),
    BICHO(androidx.compose.ui.graphics.Color(0xFFA8B820)),
    ROCA(androidx.compose.ui.graphics.Color(0xFFB8A038)),
    FANTASMA(androidx.compose.ui.graphics.Color(0xFF705898)),
    DRAGÓN(androidx.compose.ui.graphics.Color(0xFF7038F8)),
    SINIESTRO(androidx.compose.ui.graphics.Color(0xFF705848)),
    ACERO(androidx.compose.ui.graphics.Color(0xFFB8B8D0)),
    HADA(androidx.compose.ui.graphics.Color(0xFFEE99AC));

    companion object {
        fun fromString(type: String): PokemonType {
            return values().find { it.name.equals(type, ignoreCase = true) } ?: NORMAL
        }
    }
}
