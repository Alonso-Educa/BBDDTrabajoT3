package com.example.contador

import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contador.notification.NotificationHandler
import com.example.contador.ui.theme.ContadorTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import android.Manifest
import android.R.attr.label
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.contador.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val destino =
                intent?.getStringExtra("destino") // Lee la información extra de destino del Intent que recibió el MainActivity
//            AppNavigation(destino) // Pasa el destino a AppNavigation para indicar la ventana que abrir
            AppNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pantalla() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(60.dp),
                title = {
                    Text("Contador", fontWeight = FontWeight.Bold)
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            var contador by remember { mutableStateOf(0) }
            VistaContador(
                valor = contador,
                onIncrementar = {
                    // Este bloque es el callback que se ejecuta desde el hijo
                    contador++
                }
            )
        }
    }
}

@Composable
fun VistaContador(
    valor: Int,
    onIncrementar: () -> Unit // Callback sin parámetros
) {
    Column {
        Text(text = "Contador: $valor")
        Button(onClick = {
            onIncrementar()
        }) {
            Text("Sumar 1")
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Pantalla1() {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                modifier = Modifier.height(60.dp),
//                title = {
//                    Text("Formulario", fontWeight = FontWeight.Bold)
//                },
//                colors = topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = Color.White
//                )
//            )
//        }
//    ) { innerPadding ->
//        Column(modifier = Modifier.padding(innerPadding)) {
//            var nombre by remember { mutableStateOf("") }
//            CampoNombre(
//                nombre = nombre,
//                onNombreCambiado = { nuevoNombre ->
//                    nombre = nuevoNombre,
//                    label = "nombre"
//                }
//            )
//        }
//    }
//}

@Composable
fun CampoNombre(
    txtlabel: String,
    nombre: String,
    onNombreCambiado: (String) -> Unit // Callback con parámetro
) {
    OutlinedTextField(
        value = nombre, // Valor del campo de texto
        onValueChange = { nombre ->
            onNombreCambiado(nombre)
        }, // se limita la longitud
        label = {
            Text(
                text = txtlabel, // Texto del label
            )
        }
    )
//    Text(text = "Nombre ingresado: $nombre")
}

@Composable
fun Prueba() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Mostrar barra de notificación") },
                icon = { Icon(Icons.Filled.Image, contentDescription = "Icono de imagen") },
                onClick = {
                    scope.launch {
                        val result = snackbarHostState
                            .showSnackbar(
                                message = "Ejemplo de mensaje",
                                actionLabel = "Hacer algo",
                                withDismissAction = true, // Para visualizar la X de cierre
                                // También puede ser SnackbarDuration.Short (4s) o SnackbarDuration.Long (10s)
                                duration = SnackbarDuration.Short
                            )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                // Acción pulsada, qué realizar (deshacer, …)
                                expanded = true
                            }

                            SnackbarResult.Dismissed -> {
                                // Cerrado normal dando a la x o se acabó el tiempo, qué realizar (normalmente nada)
                                expanded = false
                            }
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(state = rememberScrollState())
        ) {
            Text("Texto de prueba")
            AnimatedVisibility(visible = expanded) {
                Text("Texto expandido al pulsar el BAF")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU) // Sólo Android 13 o superior (API 33)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Notificacion() {
    var titulo by remember { mutableStateOf("") }
    var cuerpo by remember { mutableStateOf("") }
    val context = LocalContext.current // Para acceder al sistema
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) // Control de permisos
    val notificationHandler = NotificationHandler(context) // La clase de notificaciones
    LaunchedEffect(key1 = true) { // Al cargar la ventana pide permiso POST_NOTIFICATIONS si no se pidió. Sólo
        // la primera vez en la primera recomposición. Pide el permiso automáticamente.
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest() // Popup de permiso si no está concedido para
            // que el usuario acepte o rechace
        }
    }
    Column {
        CampoNombre(
            nombre = titulo,
            onNombreCambiado = { nuevoNombre ->
                titulo = nuevoNombre
            },
            txtlabel = "Título de la notificación"
        )
        CampoNombre(
            nombre = cuerpo,
            onNombreCambiado = { nuevoNombre ->
                cuerpo = nuevoNombre
            },
            txtlabel = "Cuerpo de la notificación"
        )
        Button(onClick = {
            notificationHandler.enviarNotificacionConDestino(titulo, cuerpo, "PrimeraP") // Luego
            // notifica el mensaje creado
        }) { Text(text = "Clic para una notificación personalizada ") }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ContadorTheme {
        Greeting("Android")
    }
}