package com.example.contador.screens


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.contador.navigation.AppScreens
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.contador.notification.NotificationHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollP1(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(modifier = Modifier.height(60.dp),
                title = {
                    Text(text = "Primera ventana", fontSize = 15.sp)
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White)
            )
        },
        bottomBar = { BottomBar(navController as NavHostController) } // declarado en terceraP
    ){ innerPadding->
        Column(modifier = Modifier
            .fillMaxSize().padding(innerPadding)) {
            val notificationHandler = NotificationHandler(context)
            Button(
                onClick = {
                    scope.launch { // Ejecuta la corrutina
                        //delay(10000) // Espera de 10 segundos. 1 segundo = 1000 milisegundos
                        Toast.makeText(context,"Presionaste ir a la segunda ventana",Toast.LENGTH_SHORT).show()
                        notificationHandler.enviarNotificacionConDestino("¡Hola!","Notificación que irá a la Segunda Ventana","SegundaP") //
                    }
                }
            ) {
                Text(text = "Clic para una notificación avanzada en segunda ventana")
            }
            Button(
                onClick = {
                    scope.launch { // Ejecuta la corrutina
                        //delay(10000) // Espera de 10 segundos. 1 segundo = 1000 milisegundos
                        Toast.makeText(context,"Presionaste ir a la tercera ventana",Toast.LENGTH_SHORT).show()
                        notificationHandler.enviarNotificacionConDestino("¡Hola!","Notificación que irá a la Tercera Ventana","TerceraP")
                    }
                }
            ) {
                Text(text = "Clic para una notificación avanzada en tercera ventana")
            }
        }
    }
}
