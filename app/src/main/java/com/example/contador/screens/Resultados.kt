package com.example.contador.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.example.contador.R
import com.example.contador.navigation.AppScreens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Resultados(navController: NavController) {

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de usuarios") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
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
                            imageVector = Icons.Filled.Menu,
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
                                    Toast.makeText(context, "Cerrando sesión", Toast.LENGTH_SHORT)
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
                    usuarioSesion?.let {
                        val inicial = buildString {
                            append(it.nombreUsuario.first().uppercase())
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                                .clickable { /*TODO*/ },

                            ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = inicial,
                                style = MaterialTheme.typography.titleMedium,

                                )
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
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
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
                            Text(user.nombreUsuario)
                        }

                        // 🔹 ICONO EDITAR (se mantiene)
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

                    if (expandido) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(Modifier.padding(10.dp)){
                            Button(
                                onClick = {
                                    navController.navigate(AppScreens.MisInmuebles.route)
                                },
                            ) {
                                Text("Mis Amistades")
                            }
                            Text("   ")

                            OutlinedButton (
                                onClick = {
                                    navController.navigate(AppScreens.MisInmuebles.route)
                                },
                            ) {
                                Text("Mis Inmuebles")
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }



            Spacer(modifier = Modifier.width(12.dp))

//            LazyColumn {
//                items(listaUsuarios) { user ->
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//
//                        Text(
//                            text = user.nombreUsuario,
//                            modifier = Modifier.weight(1f)
//                        )
//
//                        Text(
//                            text = user.email,
//                            modifier = Modifier.weight(2f)
//                        )
//
//                        usuarioSesion?.let { user ->
//                            IconButton(onClick = {
//                                usuarioEditando = user
//                                nombre = user.nombreUsuario
//                                apellidos = user.apellidosUsuario
//                                email = user.email
//                                incorporacion = user.incorporacionUsuario
//                                sexo = user.sexo
//                                showDialog = true
//                            }) {
//                                Icon(Icons.Default.Edit, contentDescription = "Editar")
//                            }
//                        }
//                    }
//                }
//            }

//            Button(
//                modifier = Modifier.padding(16.dp),
//                onClick = {
//                    navController.navigate(AppScreens.Amigos.route)
//                }
//            ) {
//                Text("Mis Amistades")
//            }

//            Button(
//                modifier = Modifier.padding(16.dp),
//                onClick = {
//                    navController.navigate(AppScreens.MisInmuebles.route)
//                }
//            ) {
//                Text("Mis Inmuebles")
//            }
        }
    }

    // ---------------- DIALOGO ----------------

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
