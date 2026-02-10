package com.example.contador.navigation

sealed class AppScreens (val route: String) {
    object PrimeraP: AppScreens("PrimeraP")
    object SegundaP: AppScreens("SegundaP")
    object TerceraP: AppScreens("TerceraP")
    object Inicio: AppScreens("Inicio")
    object Formulario: AppScreens("Formulario")
    object MenuPrincipal: AppScreens("MenuPrincipal")
    object Amigos: AppScreens("Amigos")
    object MisInmuebles: AppScreens("MisInmuebles")
    object TodosInmuebles: AppScreens("TodosInmuebles")
    object MisPublicaciones: AppScreens("MisPublicaciones")
    object TodasPublicaciones: AppScreens("TodasPublicaciones")
}


