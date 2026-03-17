package com.example.contador.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contador.screens.ScrollP1
import com.example.contador.screens.ScrollP2
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.room.Room
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.SesionData
import com.example.contador.screens.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
// var estadoSesion by remember { mutableStateOf<SesionData?>(null) }

@Composable
fun AppNavigation(destinoNotificacion: String? = null) {
    val context = LocalContext.current
    val auth = Firebase.auth

    // currentUser reemplaza la consulta a Room para saber si hay sesión activa
    val startDestination = if (auth.currentUser != null) {
        AppScreens.MenuPrincipal.route
    } else {
        AppScreens.Inicio.route
    }

    val navController = rememberNavController()

    LaunchedEffect(destinoNotificacion) {
        if (destinoNotificacion != null && AppScreens.fromRoute(destinoNotificacion) != null) {
            navController.navigate(destinoNotificacion) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppScreens.PrimeraP.route) {
            ScrollP1(navController)
        }
        composable(AppScreens.SegundaP.route) {
            ScrollP2(navController)
        }
        composable(AppScreens.TerceraP.route) {
            ScrollP3(navController)
        }
        composable(route = AppScreens.Inicio.route) {
            Inicio(navController)
        }
        composable(route = AppScreens.Formulario.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            } // Sirve para interceptar y manejar el evento del botón físico Atrás del dispositivo. El parámetro true indica que  el sistema no ejecutará su comportamiento predeterminado (cerrar la app o regresar a la pantalla anterior).
            Formulario(navController)
        }
        composable(route = AppScreens.MenuPrincipal.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            MenuPrincipal(navController)
        }
        composable(route = AppScreens.Amigos.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Amigos(navController)
        }
        composable(route = AppScreens.MisInmuebles.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            MisInmuebles(navController)
        }
        composable(route = AppScreens.TodosInmuebles.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            TodosInmuebles(navController)
        }
        composable(route = AppScreens.MisPublicaciones.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            MisPublicaciones(navController)
        }
        composable(route = AppScreens.TodasPublicaciones.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            TodasPublicaciones(navController)
        }
        composable(route = AppScreens.Productos.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Productos(navController)
        }
        composable(route = AppScreens.Ajustes.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Ajustes(navController)
        }
        composable(route = AppScreens.RegistroContactos.route) {
            BackHandler(true) {
                Toast.makeText(
                    context,
                    "Presionaste atrás, pero está restringido volver atrás",
                    Toast.LENGTH_SHORT
                ).show()
            }
            RegistroContactos(navController)
        }
    }
}