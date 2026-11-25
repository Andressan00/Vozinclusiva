package com.example.vozinclusiva.ui.users

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozinclusiva.data.local.model.UserProfile
import com.example.vozinclusiva.data.local.model.UserType
import com.example.vozinclusiva.data.repository.UserProfileRepository
import com.example.vozinclusiva.ui.components.rememberSpeechController

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, onBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    val repository = remember { UserProfileRepository(context) }
    val speech = rememberSpeechController()

    var saved by remember { mutableStateOf(repository.load()) }
    var form by remember { mutableStateOf(saved ?: UserProfile()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mis datos básicos", style = MaterialTheme.typography.headlineMedium)

        // ---- Formulario ----
        Text("Tipo de usuario", fontSize = 18.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UserType.values().forEach { type ->
                FilterChip(
                    selected = form.userType == type,
                    onClick = { form = form.copy(userType = type) },
                    label = { Text(type.label, fontSize = 16.sp) }
                )
            }
        }

        ProfileField("Nombre completo", form.name) { form = form.copy(name = it) }
        ProfileField("Edad", form.age, KeyboardType.Number) { form = form.copy(age = it) }
        ProfileField("Teléfono", form.phone, KeyboardType.Phone) { form = form.copy(phone = it) }
        ProfileField("Contacto de emergencia (nombre)", form.emergencyName) {
            form = form.copy(emergencyName = it)
        }
        ProfileField("Contacto de emergencia (teléfono)", form.emergencyPhone, KeyboardType.Phone) {
            form = form.copy(emergencyPhone = it)
        }
        ProfileField("Notas importantes (alergias, EPS, etc.)", form.notes, singleLine = false) {
            form = form.copy(notes = it)
        }

        Button(
            onClick = {
                if (form.name.isBlank()) {
                    Toast.makeText(context, "Escribe tu nombre para guardar", Toast.LENGTH_SHORT).show()
                } else {
                    repository.save(form)
                    saved = repository.load()
                    Toast.makeText(context, "Datos guardados en el dispositivo", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
        ) { Text("Guardar datos", fontSize = 18.sp) }

        // ---- Lectura de la información guardada ----
        saved?.let { profile ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Información guardada", style = MaterialTheme.typography.titleLarge)
                    Text(profile.toReadableText(), fontSize = 20.sp, lineHeight = 28.sp)
                }
            }
            Button(
                onClick = { speech.speak(profile.toReadableText()) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
            ) { Text("Leer en voz alta", fontSize = 18.sp) }

            OutlinedButton(
                onClick = { speech.stop() },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
            ) { Text("Detener lectura", fontSize = 18.sp) }

            OutlinedButton(
                onClick = {
                    repository.clear()
                    saved = null
                    form = UserProfile()
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
            ) { Text("Borrar mis datos", fontSize = 18.sp) }
        }

        onBack?.let {
            OutlinedButton(onClick = it, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}