package com.example.pokemon_login_lgomdom.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pokemon_login_lgomdom.models.Pokemon
import com.example.pokemon_login_lgomdom.models.PokemonType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonFormDialog(
    pokemon: Pokemon?,
    onDismiss: () -> Unit,
    onSave: (Pokemon) -> Unit
) {
    var nombre by remember { mutableStateOf(pokemon?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(pokemon?.descripcion ?: "") }
    var imagenUrl by remember { mutableStateOf(pokemon?.imagenUrl ?: "") }
    var wikiUrl by remember { mutableStateOf(pokemon?.wikiUrl ?: "") }
    var habilidades by remember { mutableStateOf(pokemon?.habilidades?.joinToString(", ") ?: "") }
    var selectedTypes by remember { mutableStateOf(pokemon?.tipos?.toSet() ?: emptySet()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = if (pokemon == null) "Añadir Pokémon" else "Editar Pokémon",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tipos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PokemonType.values().forEach { type ->
                        TypeFilterChip(
                            type = type.name,
                            isSelected = selectedTypes.contains(type.name),
                            onToggle = {
                                selectedTypes = if (selectedTypes.contains(type.name)) {
                                    selectedTypes - type.name
                                } else {
                                    selectedTypes + type.name
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = imagenUrl,
                    onValueChange = { imagenUrl = it },
                    label = { Text("URL de la imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = wikiUrl,
                    onValueChange = { wikiUrl = it },
                    label = { Text("URL de la wiki (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = habilidades,
                    onValueChange = { habilidades = it },
                    label = { Text("Habilidades (separadas por comas)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (nombre.isNotBlank() && descripcion.isNotBlank() &&
                                imagenUrl.isNotBlank() && selectedTypes.isNotEmpty()) {
                                val newPokemon = Pokemon(
                                    id = pokemon?.id ?: 0,
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    tipos = selectedTypes.toList(),
                                    imagenUrl = imagenUrl,
                                    habilidades = habilidades.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                    wikiUrl = wikiUrl.ifBlank { null }
                                )
                                onSave(newPokemon)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = nombre.isNotBlank() && descripcion.isNotBlank() &&
                                imagenUrl.isNotBlank() && selectedTypes.isNotEmpty()
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun TypeFilterChip(
    type: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val pokemonType = PokemonType.fromString(type)

    FilterChip(
        selected = isSelected,
        onClick = onToggle,
        label = {
            Text(
                text = type,
                fontWeight = FontWeight.Bold
            )
        },
        leadingIcon = if (isSelected) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = pokemonType.color,
            selectedContainerColor = pokemonType.color,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = pokemonType.color,
            selectedBorderColor = pokemonType.color,
            borderWidth = 2.dp
        )
    )
}
