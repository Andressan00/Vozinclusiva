package com.example.vozinclusiva.ui.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vozinclusiva.data.repository.MessageHistoryRepository
import com.example.vozinclusiva.ui.components.rememberSpeechController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Pantalla para que la persona sorda escriba un mensaje y el teléfono lo diga en voz alta. */
@Composable
fun WriteScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current
    val speech = rememberSpeechController()
    val history = remember { MessageHistoryRepository(context) }

    var message by remember { mutableStateOf("") }
    var entries by remember { mutableStateOf(history.getAll()) }
    val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale("es", "CO")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Escribir mensaje", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = { speech.stop(); onBack() }) { Text("Volver") }
        }

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Escribe lo que quieres decir") },
            textStyle = TextStyle(fontSize = 24.sp, lineHeight = 32.sp),
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (message.isNotBlank()) {
                    speech.speak(message)
                    history.add(message)
                    entries = history.getAll()
                }
            },
            enabled = message.isNotBlank(),
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp)
        ) { Text("🔊  Hablar", fontSize = 20.sp) }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { speech.stop() },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
            ) { Text("Detener") }
            OutlinedButton(
                onClick = { message = "" },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
            ) { Text("Borrar texto") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Historial", style = MaterialTheme.typography.titleMedium)
            if (entries.isNotEmpty()) {
                TextButton(onClick = { history.clear(); entries = emptyList() }) {
                    Text("Vaciar historial")
                }
            }
        }

        if (entries.isEmpty()) {
            Text("Aún no hay mensajes. Lo que digas aparecerá aquí.", fontSize = 16.sp)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { speech.speak(entry.text) } // tocar = repetir en voz alta
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(entry.text, fontSize = 18.sp)
                            Text(
                                dateFormat.format(Date(entry.timestamp)) + "  ·  toca para repetir",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}