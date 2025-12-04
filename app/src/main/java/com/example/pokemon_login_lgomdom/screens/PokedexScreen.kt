package com.example.pokemon_login_lgomdom.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.pokemon_login_lgomdom.models.Pokemon
import com.example.pokemon_login_lgomdom.models.PokemonType
import com.example.pokemon_login_lgomdom.models.User
import com.example.pokemon_login_lgomdom.viewmodels.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(
    pokemonViewModel: PokemonViewModel,
    currentUser: User,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val pokemons by pokemonViewModel.pokemons.collectAsState()
    val isLoading by pokemonViewModel.isLoading.collectAsState()
    var showUserMenu by rememberSaveable { mutableStateOf(false) }
    var selectedPokemon by rememberSaveable { mutableStateOf<Pokemon?>(null) }
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var pokemonToEdit by remember { mutableStateOf<Pokemon?>(null) }
    var pokemonToDelete by remember { mutableStateOf<Pokemon?>(null) }

    LaunchedEffect(Unit) {
        if (pokemons.isEmpty()) {
            pokemonViewModel.loadPokemons()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokédex") },
                actions = {
                    IconButton(onClick = onNavigateToAdmin) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (currentUser.isAdmin) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Pokémon")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Muestra indicador de carga mientras se obtienen los datos iniciales
            if (isLoading && pokemons.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Grid de 2 columnas con todos los pokémon
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pokemons, key = { it.id }) { pokemon ->
                        PokemonCard(
                            pokemon = pokemon,
                            isAdmin = currentUser.isAdmin,
                            onCardClick = { selectedPokemon = it },
                            onEditClick = {
                                pokemonToEdit = it
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                pokemonToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo modal para ver detalles completos del pokémon seleccionado
    selectedPokemon?.let { pokemon ->
        PokemonDetailDialog(
            pokemon = pokemon,
            onDismiss = { selectedPokemon = null }
        )
    }

    // Diálogo para crear un nuevo pokémon (solo admin)
    if (showCreateDialog) {
        PokemonFormDialog(
            pokemon = null,
            onDismiss = { showCreateDialog = false },
            onSave = { pokemon ->
                pokemonViewModel.addPokemon(pokemon)
                showCreateDialog = false
            }
        )
    }

    // Diálogo para editar un pokémon existente (solo admin)
    if (showEditDialog && pokemonToEdit != null) {
        PokemonFormDialog(
            pokemon = pokemonToEdit,
            onDismiss = {
                showEditDialog = false
                pokemonToEdit = null
            },
            onSave = { pokemon ->
                pokemonViewModel.updatePokemon(pokemon)
                showEditDialog = false
                pokemonToEdit = null
            }
        )
    }

    // Diálogo de confirmación para eliminar un pokémon (solo admin)
    if (showDeleteDialog && pokemonToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                pokemonToDelete = null
            },
            title = { Text("Eliminar Pokémon") },
            text = { Text("¿Estás seguro de que quieres eliminar a ${pokemonToDelete?.nombre}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pokemonViewModel.deletePokemon(pokemonToDelete!!.id)
                        showDeleteDialog = false
                        pokemonToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        pokemonToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PokemonCard(
    pokemon: Pokemon,
    isAdmin: Boolean,
    onCardClick: (Pokemon) -> Unit,
    onEditClick: (Pokemon) -> Unit,
    onDeleteClick: (Pokemon) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onCardClick(pokemon) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AsyncImage(
                    model = pokemon.imagenUrl,
                    contentDescription = pokemon.nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentScale = ContentScale.Fit
                )

                if (isAdmin) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { onEditClick(pokemon) },
                            modifier = Modifier.size(40.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FloatingActionButton(
                            onClick = { onDeleteClick(pokemon) },
                            modifier = Modifier.size(40.dp),
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = pokemon.nombre.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    pokemon.tipos.forEach { tipo ->
                        TypeChip(tipo)
                    }
                }
            }
        }
    }
}


@Composable
fun TypeChip(tipo: String) {
    val pokemonType = PokemonType.fromString(tipo)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = pokemonType.color,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = tipo.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PokemonDetailDialog(
    pokemon: Pokemon,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = pokemon.imagenUrl,
                    contentDescription = pokemon.nombre,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pokemon.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pokemon.tipos.forEach { tipo ->
                        TypeChip(tipo)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pokemon.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                pokemon.habilidades?.let { habilidades ->
                    if (habilidades.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Habilidades:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = habilidades.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cerrar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    pokemon.wikiUrl?.let { url ->
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Abrir Wiki")
                        }
                    }
                }
            }
        }
    }
}
