package com.example.contador.navigation

sealed class AppScreens (val route: String) {
    object Inicio: AppScreens("Inicio")
    object PrimeraP: AppScreens("PrimeraP")
    object SegundaP: AppScreens("SegundaP")
    object TerceraP: AppScreens("TerceraP")
    object inicio: AppScreens("Inicio")
    object Formulario: AppScreens("Formulario")
    object Resultados: AppScreens("Resultados")
    object Amigos: AppScreens("Amigos")
}


