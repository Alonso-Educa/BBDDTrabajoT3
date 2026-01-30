package com.example.contador.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val screen: AppScreens,
    val label: String,
    val icon: ImageVector
)