package com.example.contador.navigation

sealed class AppScreens (val route: String) {
    object PrimeraP: AppScreens("PrimeraP")
    object SegundaP: AppScreens("SegundaP")
    object TerceraP: AppScreens("TerceraP")
    object Inicio: AppScreens("Inicio")
    object Formulario: AppScreens("Formulario")
    object Resultados: AppScreens("Resultados")
    object Amigos: AppScreens("Amigos")
    object MisInmuebles: AppScreens("MisInmuebles")
    object TodosInmuebles: AppScreens("TodosInmuebles")
}


