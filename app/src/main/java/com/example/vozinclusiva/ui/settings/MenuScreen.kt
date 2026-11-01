package com.example.vozinclusiva

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CategoryItem(val title: String, val color: Color)

@Composable
fun MenuScreen(onCategorySelected: (String) -> Unit) {
    val categories = listOf(
        CategoryItem("SALUD", Color(0xFFFF9501)),
        CategoryItem("ESTUDIO", Color(0xFFFF3B2F)),
        CategoryItem("TRÁMITE", Color(0xFF4CDA64)),
        CategoryItem("COMPRA", Color(0xFF007AFF)),
        CategoryItem("ESCRIBIR", Color(0xFF5855D6)),
        CategoryItem("FRASES RÁPIDAS", Color(0xFF8F8E94))
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "SEÑA",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "COMUNICACIÓN SIN BARRERAS",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿Qué necesitas?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clickable { onCategorySelected(item.title) },
                        colors = CardDefaults.cardColors(containerColor = item.color)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(text = "▶", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}