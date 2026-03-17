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
    object Productos: AppScreens("Productos")
    object Ajustes: AppScreens("Ajustes")
    object RegistroContactos: AppScreens("RegistroContactos")

    companion object {
        // Para buscar una pantalla por su ruta desde fuera de Compose (ej: desde MainActivity)
        fun fromRoute(route: String): AppScreens? {
            return listOf(
                PrimeraP, SegundaP, TerceraP, Inicio, Formulario,
                MenuPrincipal, Amigos, MisInmuebles, TodosInmuebles,
                MisPublicaciones, TodasPublicaciones, Productos, Ajustes,
                RegistroContactos
            ).find { it.route == route }
        }
    }
}


