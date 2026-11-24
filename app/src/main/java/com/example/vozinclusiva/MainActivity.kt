package com.example.vozinclusiva


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vozinclusiva.ui.settings.DetailScreen
import com.example.vozinclusiva.ui.settings.MenuScreen
import com.example.vozinclusiva.ui.settings.WelcomeScreen
import com.example.vozinclusiva.ui.users.UserScreen
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
                    navController.navigate("detailScreen/$categoryTitle")
                },
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

            DetailScreen(
                categoryTitle = categoryTitle,
                options = options,
                onBackClick = { navController.popBackStack() },
                onOptionClick = { _ -> }
            )
        }
    }
}