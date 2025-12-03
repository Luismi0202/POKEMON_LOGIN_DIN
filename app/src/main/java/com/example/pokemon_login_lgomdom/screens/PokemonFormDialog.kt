// screens/PokemonFormDialog.kt
package com.example.pokemon_login_lgomdom.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pokemon_login_lgomdom.models.Pokemon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonFormDialog(
    pokemon: Pokemon?,
    onDismiss: () -> Unit,
    onSave: (Pokemon) -> Unit
) {
    var nombre by remember { mutableStateOf(pokemon?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(pokemon?.descripcion ?: "") }
    var tipos by remember { mutableStateOf(pokemon?.tipos?.joinToString(", ") ?: "") }
    var imagenUrl by remember { mutableStateOf(pokemon?.imagenUrl ?: "") }
    var habilidades by remember { mutableStateOf(pokemon?.habilidades?.joinToString(", ") ?: "") }
    var wikiUrl by remember { mutableStateOf(pokemon?.wikiUrl ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (pokemon == null) "Nuevo Pokémon" else "Editar Pokémon",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tipos,
                    onValueChange = { tipos = it },
                    label = { Text("Tipos (separados por coma)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Fuego, Volador") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = imagenUrl,
                    onValueChange = { imagenUrl = it },
                    label = { Text("URL de la imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = habilidades,
                    onValueChange = { habilidades = it },
                    label = { Text("Habilidades (separadas por coma)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Mar Llamas, Impulso") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = wikiUrl,
                    onValueChange = { wikiUrl = it },
                    label = { Text("URL de la wiki (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val newPokemon = Pokemon(
                                id = pokemon?.id ?: 0,
                                nombre = nombre,
                                descripcion = descripcion,
                                tipos = tipos.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                imagenUrl = imagenUrl,
                                habilidades = habilidades.split(",").map { it.trim() }
                                    .filter { it.isNotEmpty() }.ifEmpty { null },
                                wikiUrl = wikiUrl.ifBlank { null }
                            )
                            onSave(newPokemon)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = nombre.isNotBlank() && descripcion.isNotBlank() &&
                                tipos.isNotBlank() && imagenUrl.isNotBlank()
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
