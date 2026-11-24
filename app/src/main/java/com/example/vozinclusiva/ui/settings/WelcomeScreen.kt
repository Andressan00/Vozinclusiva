package com.example.vozinclusiva.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun WelcomeScreen(
    auth: FirebaseAuth,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SEÑA",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "COMUNICACIÓN SIN BARRERAS",
                fontSize = 12.sp,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    errorMessage = null
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor, ingresa tu correo y contraseña."
                        return@Button
                    }
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            auth.signInWithEmailAndPassword(email.trim(), password).await()
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } catch (e: Exception) {
                            val mensaje = e.message ?: "Error al iniciar sesión."
                            errorMessage = mensaje
                            Toast.makeText(context, "Error: $mensaje", Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009900))
            ) {
                Text(
                    text = if (isLoading) "CARGANDO..." else "INICIAR SESIÓN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onRegisterClick) {
                Text(
                    text = "¿No tienes cuenta? Regístrate aquí",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}