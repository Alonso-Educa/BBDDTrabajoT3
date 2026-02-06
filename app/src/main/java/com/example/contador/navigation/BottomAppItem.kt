package com.example.contador.navigation

import androidx.compose.ui.graphics.vector.ImageVector

// clase para la barra de navegación inferior
data class BottomNavItem(
    val screen: AppScreens,
    val label: String,
    val icon: ImageVector
)