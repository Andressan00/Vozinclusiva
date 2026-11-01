package com.example.vozinclusiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vozinclusiva.DetailScreen
import com.example.vozinclusiva.MenuScreen
import com.example.vozinclusiva.data.local.AppDatabase
import com.example.vozinclusiva.data.local.dao.UserDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(userDao)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(userDao: UserDao) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "WelcomeScreen"
    ) {
        // 1. Pantalla de Inicio de Sesión
        composable("WelcomeScreen") {
            WelcomeScreen(
                userDao = userDao,
                onLoginSuccess = {
                    navController.navigate("menuScreen") {
                        popUpTo("WelcomeScreen"){
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate("userScreen")
                }
            )
        }

        // 2. Pantalla de Registro
        composable("userScreen") {
            UserScreen(
                userDao = userDao,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 3. Menú Principal
        composable("menuScreen") {
            MenuScreen(
                onCategorySelected = { categoryTitle ->
                    navController.navigate("detailScreen/$categoryTitle")
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