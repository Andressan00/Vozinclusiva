package com.example.vozinclusiva

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozinclusiva.data.local.dao.UserDao
import com.example.vozinclusiva.data.local.model.User
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    userDao: UserDao,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val userList by userDao.getAllUsers()
        .collectAsState(initial = emptyList())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onBackClick() }
            ) {
                Text(
                    text = "◀",
                    fontSize = 18.sp,
                    color = Color(0xFF0080F0)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Volver",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0080F0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Registro de Nuevo Usuario",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (
                        name.isBlank() ||
                        email.isBlank() ||
                        password.isBlank()
                    ) {
                        isError = true
                        message = "Completa todos los campos"
                    } else {
                        coroutineScope.launch {
                            try {
                                userDao.insertUser(
                                    User(
                                        name = name.trim(),
                                        email = email.trim(),
                                        password = password
                                    )
                                )

                                name = ""
                                email = ""
                                password = ""
                                isError = false
                                message = "Usuario registrado correctamente"
                            } catch (e: Exception) {
                                isError = true
                                message = "No se pudo guardar el usuario: ${e.localizedMessage}"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Usuario")
            }

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color(0xFF16803D)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Usuarios Registrados:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(userList) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Nombre: ${user.name}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Correo: ${user.email}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
