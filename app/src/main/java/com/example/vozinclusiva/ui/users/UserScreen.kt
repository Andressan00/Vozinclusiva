package com.example.vozinclusiva.ui.users

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
fun UserScreen(
    auth: FirebaseAuth,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
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
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onBackClick() }
            ) {
                Text(text = "◀", fontSize = 18.sp, color = Color(0xFF0080F0))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Volver",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0080F0)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Registrarse",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = msg, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    errorMessage = null
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor, introduce email y contraseña."
                        return@Button
                    }
                    if (password.length < 6) {
                        errorMessage = "La contraseña debe tener al menos 6 caracteres."
                        return@Button
                    }
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            auth.createUserWithEmailAndPassword(email.trim(), password).await()
                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        } catch (e: Exception) {
                            val mensaje = e.message ?: "Error al registrar usuario."
                            errorMessage = mensaje
                            Toast.makeText(context, "Error: $mensaje", Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Registrando..." else "Registrar")
            }
        }
    }
}