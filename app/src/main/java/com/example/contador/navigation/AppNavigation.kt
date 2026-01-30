package com.example.contador.navigation

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.example.contador.CampoNombre
import com.example.contador.notification.NotificationHandler
import com.example.contador.screens.ScrollP1
import com.example.contador.screens.ScrollP2
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.screens.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigation(destino: String?) { // Recibe la información del destino
    val context = LocalContext.current

//    val db = Room.databaseBuilder(
//        com.example.contador.navigation.context,
//        AppDB::class.java,
//        Estructura.DB.NAME
//    )
//        .allowMainThreadQueries().build()
//    val db = remember {
//        Room.databaseBuilder(
//            context.applicationContext,
//            AppDB::class.java,
//            Estructura.DB.NAME
//        ).allowMainThreadQueries()
//            .build()
//    }
//
//    var estadoSesion = db.sesionDao().getEstadoSesion()
//    LaunchedEffect(Unit) {
//        estadoSesion = db.sesionDao().getEstadoSesion()
//    }


    val navController = rememberNavController()

    val startDestination = when (destino) { // Verifica con un when la información leída para
        // determinar la ventana que se abrirá
        "SegundaP" -> AppScreens.SegundaP.route
        "TerceraP" -> AppScreens.TerceraP.route

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
    }
//        composable(route = AppScreens.Inicio.route) {
//            Inicio(navController)
//        }
//        composable(route = AppScreens.Formulario.route) {
//            BackHandler(true) {
//                Toast.makeText(
//                    context,
//                    "Presionaste atrás, pero está restringido volver atrás",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } // Sirve para interceptar y manejar el evento del botón físico Atrás del dispositivo. El parámetro true indica que
//            // el sistema no ejecutará su comportamiento predeterminado (cerrar la app o regresar a la pantalla anterior).
//            Formulario(navController)
//        }
//        composable(route = AppScreens.Resultados.route) {
//            BackHandler(true) {
//                Toast.makeText(
//                    context,
//                    "Presionaste atrás, pero está restringido volver atrás",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            Resultados(navController)
//        }
//        composable(route = AppScreens.Amigos.route) {
//            BackHandler(true) {
//                Toast.makeText(
//                    context,
//                    "Presionaste atrás, pero está restringido volver atrás",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            Amigos(navController)
//        }
    }

//NavHost(navController = navController, startDestination = startDestination) {
//    composable(AppScreens.PrimeraP.route) {
//        ScrollP1(navController)
//    }
//    composable(AppScreens.SegundaP.route) {
//        ScrollP2(navController)
//    }
//}
