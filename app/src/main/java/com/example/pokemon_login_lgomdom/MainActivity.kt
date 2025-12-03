// MainActivity.kt
package com.example.pokemon_login_lgomdom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokemon_login_lgomdom.screens.*
import com.example.pokemon_login_lgomdom.ui.theme.Pokemon_login_lgomdomTheme
import com.example.pokemon_login_lgomdom.viewmodels.AuthViewModel
import com.example.pokemon_login_lgomdom.viewmodels.PokemonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pokemon_login_lgomdomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokemonApp()
                }
            }
        }
    }
}

@Composable
fun PokemonApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val pokemonViewModel: PokemonViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination = if (currentUser != null) "pokedex" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("pokedex") {
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

        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("pokedex") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("reset_password") {
            ResetPasswordScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("pokedex") {
            currentUser?.let { user ->
                PokedexScreen(
                    pokemonViewModel = pokemonViewModel,
                    currentUser = user,
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
