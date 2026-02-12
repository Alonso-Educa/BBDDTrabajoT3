package com.example.contador.navigation

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.screens.Amigos
import com.example.contador.screens.Formulario
import com.example.contador.screens.Inicio
import com.example.contador.screens.MisInmuebles
import com.example.contador.screens.MenuPrincipal
import com.example.contador.screens.MisPublicaciones
import com.example.contador.screens.ScrollP1
import com.example.contador.screens.ScrollP2
import com.example.contador.screens.ScrollP3
import com.example.contador.screens.TodasPublicaciones
import com.example.contador.screens.TodosInmuebles

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigationNotificaciones(destino: String?) { // Recibe la información del destino
    val context = LocalContext.current

//    val db = Room.databaseBuilder(
//        com.example.contador.navigation.context,
//        AppDB::class.java,
//        Estructura.DB.NAME
//    )
//        .allowMainThreadQueries().build()
    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDB::class.java,
            Estructura.DB.NAME
        ).allowMainThreadQueries()
            .build()
    }

    var estadoSesion = db.sesionDao().getEstadoSesion()
    LaunchedEffect(Unit) {
        estadoSesion = db.sesionDao().getEstadoSesion()
    }

    val navController = rememberNavController()

    val startDestination = when (destino) { // Verifica con un when la información leída para
        // determinar la ventana que se abrirá
        "SegundaP" -> AppScreens.SegundaP.route
        "TerceraP" -> AppScreens.TerceraP.route
        "Inicio" -> AppScreens.Inicio.route
        "Resultados" -> AppScreens.MenuPrincipal.route
        "Amigos" -> AppScreens.Amigos.route
        "Formulario" -> AppScreens.Formulario.route
        "MisInmuebles" -> AppScreens.MisInmuebles.route
        "TodosInmuebles" -> AppScreens.TodosInmuebles.route
        "MisPublicaciones" -> AppScreens.MisPublicaciones.route
        "TodasPublicaciones" -> AppScreens.TodasPublicaciones.route
        else -> AppScreens.PrimeraP.route // Sólo tenemos las ventanas PrimerP y SegundaP en AppScreens
    }
    NavHost(navController = navController, startDestination = startDestination) {
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
            } // Sirve para interceptar y manejar el evento del botón físico Atrás del dispositivo. El parámetro true indica que
            // el sistema no ejecutará su comportamiento predeterminado (cerrar la app o regresar a la pantalla anterior).
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
    }
}
