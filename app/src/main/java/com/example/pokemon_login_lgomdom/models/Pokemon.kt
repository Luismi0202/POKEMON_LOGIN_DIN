
package com.example.pokemon_login_lgomdom.models

/**
 * Modelo de datos que representa un Pokémon en la aplicación.
 * Contiene toda la información necesaria para mostrar y gestionar un pokémon.
 *
 * @property id Identificador único del pokémon (generado automáticamente al crear)
 * @property nombre Nombre del pokémon
 * @property descripcion Descripción textual del pokémon
 * @property tipos Lista de tipos del pokémon (ej: ["FUEGO", "VOLADOR"])
 * @property imagenUrl URL de la imagen del pokémon
 * @property habilidades Lista opcional de habilidades especiales
 * @property wikiUrl URL opcional a la página wiki del pokémon para más información
 */
data class Pokemon(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val tipos: List<String>,
    val imagenUrl: String,
    val habilidades: List<String>? = null,
    val wikiUrl: String? = null
)

/**
 * Enum que representa los tipos de pokémon disponibles en el juego.
 * Cada tipo tiene un color asociado para visualización en la interfaz.
 * Los colores están basados en los tipos oficiales de Pokémon.
 */
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
        /**
         * Convierte un string a su enum correspondiente.
         * La conversión es case-insensitive.
         *
         * @param type String con el nombre del tipo
         * @return PokemonType correspondiente o NORMAL si no se encuentra
         */
        fun fromString(type: String): PokemonType {
            return values().find { it.name.equals(type, ignoreCase = true) } ?: NORMAL
        }
    }
}
