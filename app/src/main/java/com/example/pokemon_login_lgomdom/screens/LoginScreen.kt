
package com.example.pokemon_login_lgomdom.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pokemon_login_lgomdom.viewmodels.AuthViewModel
import kotlin.compareTo

/**
 * Pantalla de inicio de sesión de la aplicación.
 *
 * Proporciona un formulario para que los usuarios ingresen sus credenciales y accedan al sistema.
 * Incluye validación de campos, manejo de estados de carga y enlaces a otras funcionalidades
 * como registro y recuperación de contraseña.
 *
 * @param authViewModel ViewModel que gestiona la lógica de autenticación
 * @param onLoginSuccess Callback ejecutado cuando el login es exitoso
 * @param onNavigateToRegister Callback para navegar a la pantalla de registro
 * @param onNavigateToResetPassword Callback para navegar a la recuperación de contraseña
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToResetPassword: () -> Unit
) {
    // Estados locales del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados observables del ViewModel
    val authState by authViewModel.authState.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    // Efecto que navega cuando el login es exitoso
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) onLoginSuccess()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Campo de email con validación de tipo
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                authViewModel.clearError()  // Limpia errores al editar
            },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(16.dp))

        // Campo de contraseña con opción de visibilidad
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                authViewModel.clearError()
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        // Enlace a recuperación de contraseña
        TextButton(
            onClick = onNavigateToResetPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("¿Olvidaste tu contraseña?")
        }

        Spacer(Modifier.height(24.dp))

        // Botón principal de login con validación de campos
        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = authState !is AuthViewModel.AuthState.Loading &&
                     email.isNotBlank() &&
                     password.isNotBlank()
        ) {
            if (authState is AuthViewModel.AuthState.Loading) {
                CircularProgressIndicator(
                    Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Iniciar Sesión")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botón secundario para ir a registro
        OutlinedButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Registrarse")
        }

        // Mensaje de error si existe
        errorMessage?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}