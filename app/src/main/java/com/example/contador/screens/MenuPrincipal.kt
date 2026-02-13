package com.example.contador.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.UsuarioData
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.composables.icons.lucide.HousePlus
import com.composables.icons.lucide.Lucide
import com.example.contador.navigation.AppScreens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipal(navController: NavHostController) {

    val context = LocalContext.current

    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDB::class.java,
            Estructura.DB.NAME
        ).allowMainThreadQueries().build()
    }

    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    // ---------------- ESTADOS ----------------

    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }
//    var listaUsuarios by remember { mutableStateOf<List<UsuarioData>>(emptyList()) }

    var showDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var usuarioEditando by remember { mutableStateOf<UsuarioData?>(null) }

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var incorporacion by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("Hombre") }
    val contrasena = rememberTextFieldState()
    val datePickerState = rememberDatePickerState()

    // ---------------- CARGA DE DATOS ----------------

    LaunchedEffect(Unit) {
        usuarioSesion = db.sesionDao()
            .getUsuarioSesionActual()
    }
    var idUsuarioSesionActual by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        idUsuarioSesionActual = db.sesionDao()
            .getUsuarioSesionActual()
            ?.idUsuario ?: ""
    }

    LaunchedEffect(Unit) {
        usuarioSesion = db.sesionDao().getUsuarioSesionActual()
    }

    // ---------------- UI ----------------

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Para la barra lateral de navegación (6)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "Mi Aplicación",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider()

                    // Sección principal
                    Text(
                        "Navegación",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )

                    NavigationDrawerItem(
                        label = { Text("Inicio") },
                        selected = false,
                        icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(AppScreens.Inicio.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Menú Principal") },
                        selected = false,
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(AppScreens.MenuPrincipal.route)
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Amigos") },
                        selected = false,
                        icon = { Icon(Icons.Default.Group, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(AppScreens.Amigos.route)
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Mis Inmuebles") },
                        selected = false,
                        icon = { Icon(Icons.Default.House, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(AppScreens.MisInmuebles.route)
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Todos los Inmuebles") },
                        selected = false,
                        icon = { Icon(Lucide.HousePlus, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(AppScreens.TodosInmuebles.route)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Menú de usuario") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                navController.navigate(AppScreens.Inicio.route) {
                                    popUpTo(0) { inclusive = true }
                                    Toast.makeText(context, "Saliendo al menú", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Salir al menú"
                            )
                        }
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (idUsuarioSesionActual.isNotEmpty()) {
                                        db.sesionDao().eliminarSesionUsuario(idUsuarioSesionActual)
                                    }

                                    navController.navigate(AppScreens.Inicio.route) {
                                        popUpTo(0) { inclusive = true }
                                        Toast.makeText(
                                            context,
                                            "Cerrando sesión",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Logout,
                                contentDescription = "Cerrar sesión"
                            )
                        }
                        usuarioSesion?.let { usuario ->
                            var showCardDialog by remember { mutableStateOf(false) }

                            // 🔹 Icono circular clicable
                            val inicial = usuario.nombreUsuario.firstOrNull()?.uppercase() ?: "U"
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        MaterialTheme.colorScheme.tertiary,
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.secondary,
                                        CircleShape
                                    )
                                    .clickable { showCardDialog = true } // abrir diálogo
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = inicial,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            // 🔹 Dialog con tarjeta de usuario
                            if (showCardDialog) {
                                Dialog(onDismissRequest = { showCardDialog = false }) {
                                    Card(
                                        shape = MaterialTheme.shapes.large,
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        elevation = CardDefaults.cardElevation(8.dp),
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
//                                        verticalArrangement = Arrangement.Center,
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .widthIn(min = 200.dp, max = 300.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(verticalArrangement = Arrangement.Center) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(60.dp)
                                                            .background(
                                                                MaterialTheme.colorScheme.tertiary,
                                                                CircleShape
                                                            )
                                                            .border(
                                                                1.dp,
                                                                MaterialTheme.colorScheme.secondary,
                                                                CircleShape
                                                            )
                                                            .clickable {
                                                                showCardDialog = true
                                                            } // abrir diálogo
                                                    ) {
                                                        Text(
                                                            modifier = Modifier.align(Alignment.Center),
                                                            text = inicial,
                                                            style = MaterialTheme.typography.titleMedium
                                                        )
                                                    }
                                                }
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Text(
                                                        text = "${usuario.nombreUsuario} ${usuario.apellidosUsuario}",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        "Email: ${usuario.email}",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        "Sexo: ${usuario.sexo}",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        "Incorporación: ${usuario.incorporacionUsuario}",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                            Button(
                                                modifier = Modifier.padding(bottom = 16.dp),
                                                onClick = { showCardDialog = false }) {
                                                Text("Cerrar")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->

            // Screen content


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)

            ) {

                var expandido by remember { mutableStateOf(false) }

                usuarioSesion?.let { user ->

                    val iniciales = user.nombreUsuario.first().uppercase()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandido = !expandido }
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        MaterialTheme.colorScheme.tertiary,
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.secondary,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = iniciales,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 10.dp)
                            ) {
                                Text(user.email, fontWeight = FontWeight.Bold)
                                Text("${user.nombreUsuario} ${user.apellidosUsuario}")
                            }

                            // Icono editar, donde se obtienen los datos del usuario actual
                            IconButton(onClick = {
                                usuarioEditando = user
                                nombre = user.nombreUsuario
                                apellidos = user.apellidosUsuario
                                email = user.email
                                incorporacion = user.incorporacionUsuario
                                sexo = user.sexo
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }

                            Icon(
                                imageVector = if (expandido)
                                    Icons.Default.KeyboardArrowUp
                                else
                                    Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }

                        AnimatedVisibility(visible = expandido) {
                            Column(modifier = Modifier.align(CenterHorizontally)) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    Modifier
                                        .padding(10.dp)
                                        .align(CenterHorizontally)
                                ) {
                                    Spacer(modifier = Modifier.width(40.dp))
                                    Button(
                                        onClick = { navController.navigate(AppScreens.Amigos.route) },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.People,
                                            contentDescription = "Amigos"
                                        )
                                        Text(" Amigos")
                                    }

                                    Spacer(modifier = Modifier.width(30.dp))

                                    OutlinedButton(
                                        onClick = { navController.navigate(AppScreens.MisInmuebles.route) },
                                    ) {
                                        Icon(
                                            imageVector = Lucide.HousePlus,
                                            contentDescription = "Inmuebles"
                                        )
                                        Text(" Inmuebles")
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { navController.navigate(AppScreens.MisPublicaciones.route) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Magenta, // Fondo del botón
                                        contentColor = Color.White      // Color del texto / icono
                                    ), modifier = Modifier.align(CenterHorizontally)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Camara"
                                    )
                                    Text(" Instagram")
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }


    // Dialogo de edición de usuario
    if (showDialog && usuarioEditando != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Editar usuario") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(apellidos, { apellidos = it }, label = { Text("Apellidos") })
                    OutlinedTextField(email, { email = it }, label = { Text("Email") })

                    OutlinedSecureTextField(
                        state = contrasena,
                        label = { Text("Contraseña") }
                    )

                    OutlinedTextField(
                        value = incorporacion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Incorporación") },
                        trailingIcon = {
                            IconButton(
                                onClick = { showDatePicker = true }
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Seleccionar fecha"
                                )
                            }

                        }
                    )
                    SexoRadioGroup(sexo) { sexo = it }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {

                        val actualizado = usuarioEditando!!.copy(
                            nombreUsuario = nombre,
                            apellidosUsuario = apellidos,
                            email = email,
                            sexo = sexo,
                            incorporacionUsuario = incorporacion
                        )

                        db.usuarioDao().actualizaUsuario(actualizado)

                        firestore.collection("usuarios")
                            .document(email)
                            .set(
                                mapOf(
                                    "nombre" to nombre,
                                    "apellidos" to apellidos,
                                    "email" to email,
                                    "sexo" to sexo,
                                    "incorporacion" to incorporacion
                                )
                            )


//                        if (idUsuarioSesionActual != 0) {
//                            listaUsuarios = db.usuarioDao()
//                                .getListaUsuariosPorId(idUsuarioSesionActual)
//                        }

                        showDialog = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    incorporacion =
                        datePickerState.selectedDateMillis?.let {
                            convertMillisToDate2(it)
                        } ?: ""
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


fun convertMillisToDate2(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun DatePickerModal2(
    onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(
            onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DatePickerFieldToModal2(
    modifier: Modifier = Modifier
) {
    var selectedDate by remember {
        mutableStateOf<Long?>(null)
    }

    var showModal by remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = selectedDate?.let {
            convertMillisToDate(it)
        } ?: "",
        onValueChange = {},
        label = { Text("DOB") },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(
                Icons.Default.DateRange, contentDescription = "Select date"
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            })

    if (showModal) {
        DatePickerModal2(onDateSelected = { selectedDate = it }, onDismiss = { showModal = false })
    }
}

@Composable
fun UsuarioHeaderDesplegable(
    nombre: String,
    email: String,
    onIrAInmuebles: () -> Unit,
    onOtroClick: () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Fila del usuario (icono + nombre + email)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandido = !expandido }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuario",
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = email, style = MaterialTheme.typography.bodySmall)
            }
        }

        // 🔽 Botones desplegables
        if (expandido) {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onIrAInmuebles,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir a inmuebles")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onOtroClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Otro botón")
            }
        }
    }
}