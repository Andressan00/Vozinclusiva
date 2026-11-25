package com.example.vozinclusiva


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vozinclusiva.data.repository.MessageHistoryRepository
import com.example.vozinclusiva.data.repository.UserProfileRepository
import com.example.vozinclusiva.ui.components.rememberSpeechController
import com.example.vozinclusiva.ui.settings.DetailScreen
import com.example.vozinclusiva.ui.settings.MenuScreen
import com.example.vozinclusiva.ui.settings.WelcomeScreen
import com.example.vozinclusiva.ui.users.ProfileScreen // NUEVO
import com.example.vozinclusiva.ui.users.UserScreen
import com.example.vozinclusiva.ui.users.WriteScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(auth = auth)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(auth: FirebaseAuth) {
    val navController = rememberNavController()
    val startDestination = if (auth.currentUser != null) "menuScreen" else "WelcomeScreen"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Pantalla de Inicio de Sesión con Firebase
        composable("WelcomeScreen") {
            WelcomeScreen(
                auth = auth,
                onLoginSuccess = {
                    navController.navigate("menuScreen") {
                        popUpTo("WelcomeScreen") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("userScreen")
                }
            )
        }

        // 2. Pantalla de Registro con Firebase
        composable("userScreen") {
            UserScreen(
                auth = auth,
                onRegisterSuccess = {
                    navController.navigate("menuScreen") {
                        popUpTo("WelcomeScreen") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 3. Menú Principal
        composable("menuScreen") {
            MenuScreen(
                auth = auth,
                onCategorySelected = { categoryTitle ->
                    if (categoryTitle == "ESCRIBIR") {
                        navController.navigate("writeScreen") // NUEVO
                    } else {
                        navController.navigate("detailScreen/$categoryTitle")
                    }
                },
                onProfile = { navController.navigate("profileScreen") }, // NUEVO
                onLogout = {
                    navController.navigate("WelcomeScreen") {
                        popUpTo("menuScreen") { inclusive = true }
                    }
                }
            )
        }

        // 4. Detalle
        composable("detailScreen/{categoryTitle}") { backStackEntry ->
            val categoryTitle = backStackEntry.arguments?.getString("categoryTitle") ?: "SALUD"
            val options = when (categoryTitle) {
                "SALUD" -> listOf("Me siento mal", "Necesito un médico", "Pastillas para el dolor", "Emergencia")
                "ESTUDIO" -> listOf("No entiendo la tarea", "¿Dónde queda el salón?", "Necesito un libro", "Profesor, una duda")
                "TRÁMITE" -> listOf("Quiero sacar un turno", "¿Dónde formo fila?", "Necesito ayuda con este documento", "Pagar servicio")
                "COMPRA" -> listOf("¿Cuánto cuesta?", "Quiero comprar esto", "¿Aceptan tarjeta?", "Bolsa por favor")
                "ESCRIBIR" -> listOf("Escribir mensaje personalizado...")
                else -> listOf("Hola", "Gracias", "Por favor", "Adiós")
            }

            // NUEVO: lee en voz alta, por el parlante del teléfono, la frase que se toque
            val context = LocalContext.current
            val speech = rememberSpeechController()

            DetailScreen(
                categoryTitle = categoryTitle,
                options = options,
                onBackClick = {
                    speech.stop()
                    navController.popBackStack()
                },
                onOptionClick = { option ->
                    val textToSpeak: String? = when {
                        // Esta opción es para escribir; no tiene sentido leerla tal cual
                        option.startsWith("Escribir mensaje") -> null
                        // En emergencia, además de la frase, lee los datos guardados del perfil
                        option.equals("Emergencia", ignoreCase = true) -> {
                            val profile = UserProfileRepository(context).load()
                            "Emergencia. Necesito ayuda urgente." +
                                    (profile?.let { " " + it.toReadableText() } ?: "")
                        }
                        else -> option
                    }
                    textToSpeak?.let {
                        speech.speak(it)
                        MessageHistoryRepository(context).add(option) // NUEVO: historial
                    }
                }
            )
        }

        // 6. Escribir mensaje y reproducirlo en voz alta (NUEVO)
        composable("writeScreen") {
            WriteScreen(onBack = { navController.popBackStack() })
        }

        // 5. Mi perfil (datos básicos guardados localmente + lectura en voz alta) NUEVO
        composable("profileScreen") {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }
}