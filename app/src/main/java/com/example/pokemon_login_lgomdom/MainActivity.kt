package com.example.pokemon_login_lgomdom

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokemon_login_lgomdom.screens.*
import com.example.pokemon_login_lgomdom.ui.theme.Pokemon_login_lgomdomTheme
import com.example.pokemon_login_lgomdom.viewmodels.AuthViewModel
import com.example.pokemon_login_lgomdom.viewmodels.AuthViewModelFactory
import com.example.pokemon_login_lgomdom.viewmodels.PokemonViewModel
import com.example.pokemon_login_lgomdom.viewmodels.PokemonViewModelFactory

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(applicationContext)
    }
    private val pokemonViewModel: PokemonViewModel by viewModels {
        PokemonViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pokemon_login_lgomdomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokemonApp(authViewModel, pokemonViewModel)
                }
            }
        }
    }

    @Composable
    fun PokemonApp(
        authViewModel: AuthViewModel,
        pokemonViewModel: PokemonViewModel
    ) {
        val navController = rememberNavController()
        val currentUser by authViewModel.currentUser.collectAsState()
        val context = LocalContext.current

        val startDestination = when {
            currentUser == null -> "login"
            currentUser?.isAdmin == true -> "admin"
            else -> "pokedex"
        }

        // Configuración del grafo de navegación con todas las rutas de la aplicación
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // Ruta: Pantalla de inicio de sesión
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        // Redirige según el rol del usuario: admin al panel de administración, otros a la pokédex
                        val destination = if (currentUser?.isAdmin == true) "admin" else "pokedex"
                        navController.navigate(destination) {
                            // Limpia el back stack para evitar volver al login con el botón atrás
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onNavigateToResetPassword = {
                        navController.navigate("reset_password")
                    }
                )
            }

            // Ruta: Pantalla de registro de nuevos usuarios
            composable("register") {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        // Tras registro exitoso, lleva directamente a la pokédex
                        navController.navigate("pokedex") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Ruta: Pantalla de recuperación de contraseña
            composable("reset_password") {
                ResetPasswordScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Ruta: Panel de administración (solo accesible para usuarios admin)
            composable("admin") {
                currentUser?.let { user ->
                    if (user.isAdmin) {
                        AdminScreen(
                            currentUser = user,
                            onNavigateToPokedex = {
                                navController.navigate("pokedex")
                            },
                            onLogout = {
                                authViewModel.logout()
                                // Al hacer logout, vuelve al login y limpia todo el back stack
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    } else {
                        // Si un usuario sin permisos intenta acceder, se le redirige
                        LaunchedEffect(Unit) {
                            Toast.makeText(context, "no tienes permisos", Toast.LENGTH_SHORT)
                                .show()
                            navController.navigate("pokedex") {
                                popUpTo("admin") { inclusive = true }
                            }
                        }
                    }
                }
            }

            // Ruta: Pantalla principal de la pokédex (disponible para todos los usuarios autenticados)
            composable("pokedex") {
                currentUser?.let { user ->
                    PokedexScreen(
                        pokemonViewModel = pokemonViewModel,
                        currentUser = user,
                        onNavigateToAdmin = {
                            // Solo permite navegar al panel admin si el usuario tiene permisos
                            if (user.isAdmin) {
                                navController.navigate("admin")
                            } else {
                                Toast.makeText(
                                    context,
                                    "no tienes permisos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
