//package com.example.contador.screens
//
//import android.content.Context
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.selection.selectable
//import androidx.compose.foundation.selection.selectableGroup
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.text.input.TextObfuscationMode
//import androidx.compose.foundation.text.input.rememberTextFieldState
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
//import androidx.compose.material3.Button
//import androidx.compose.material3.DatePicker
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedSecureTextField
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.RadioButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
//import androidx.compose.material3.rememberDatePickerState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.semantics.Role
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Popup
//import androidx.navigation.NavController
//import androidx.room.Room
//import com.example.contador.localdb.AppDB
//import com.example.contador.localdb.Estructura
//import com.example.contador.localdb.SesionData
//import com.example.contador.localdb.UsuarioDao
//import com.example.contador.localdb.UsuarioData
//import com.example.contador.navigation.AppScreens
//import com.google.firebase.Firebase
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.firestore
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Formulario(
//    navController: NavController, modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    // Room
//    val dbLocal = remember {
//        Room.databaseBuilder(context, AppDB::class.java, Estructura.DB.NAME)
//            .allowMainThreadQueries().build()
//    }
//    val usuarioDao = dbLocal.usuarioDao()
//    val sesionDao = dbLocal.sesionDao()
//
//    //  ESTADOS DE FORMULARIO
//    var nombre by remember { mutableStateOf("") }
//    var apellidos by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var sexo by remember { mutableStateOf("Hombre") }
//    var incorporacion by remember { mutableStateOf("") }
//    val contrasena = rememberTextFieldState("")
//    var passVisible by remember { mutableStateOf(false) }
//    var showDatePicker by remember { mutableStateOf(false) }
//
//    val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
//
//    //  ROOM + FIREBASE
//    val db = remember {
//        Room.databaseBuilder(context, AppDB::class.java, Estructura.DB.NAME)
//            .allowMainThreadQueries().build()
//    }
//    val dbfire = FirebaseFirestore.getInstance()
//
//    // Firebase
//    val dbFirebase = Firebase.firestore
//
//    //  DATE PICKER STATE
//    val datePickerState = rememberDatePickerState()
//
//    LaunchedEffect(datePickerState.selectedDateMillis) {
//        datePickerState.selectedDateMillis?.let {
//            incorporacion = convertMillisToDate(it)
//            showDatePicker = false
//        }
//    }
//
//    //  UI
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                modifier = Modifier.height(60.dp),
//                title = { Text("Registrar un usuario", fontSize = 15.sp) },
//                colors = topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = Color.White
//                ),
//                navigationIcon = {
//                    IconButton(onClick = { navController.navigate(AppScreens.Inicio.route) }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
//                    }
//                })
//        }) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            //  CAMPOS
//            OutlinedTextField(
//                value = nombre,
//                onValueChange = { if (it.length <= 40) nombre = it },
//                label = { Text("Nombre del usuario") },
//                modifier = Modifier.width(300.dp)
//            )
//
//            OutlinedTextField(
//                value = apellidos,
//                onValueChange = { if (it.length <= 50) apellidos = it },
//                label = { Text("Apellidos del usuario") },
//                modifier = Modifier.width(300.dp)
//            )
//
//            OutlinedTextField(
//                value = email,
//                onValueChange = { if (it.length <= 40) email = it },
//                label = { Text("Email") },
//                modifier = Modifier.width(300.dp)
//            )
//
//            // Contraseña
//            OutlinedSecureTextField(
//                state = contrasena,
//                label = { Text("Contraseña") },
//                modifier = Modifier.width(300.dp),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                trailingIcon = {
//                    IconButton(onClick = { passVisible = !passVisible }) {
//                        Icon(
//                            imageVector = if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
//                            contentDescription = if (passVisible) "Ocultar contraseña" else "Mostrar contraseña"
//                        )
//                    }
//                },
//                textObfuscationMode = if (passVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped
//            )
//
//            Spacer(Modifier.height(10.dp))
//
//            // Fecha de incorporación
//            Box(modifier = Modifier.width(300.dp)) {
//                OutlinedTextField(
//                    value = incorporacion,
//                    onValueChange = {},
//                    label = { Text("Fecha de incorporación") },
//                    readOnly = true,
//                    trailingIcon = {
//                        IconButton(onClick = { showDatePicker = true }) {
//                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(64.dp)
//                )
//
//                if (showDatePicker) {
//                    Popup(
//                        onDismissRequest = { showDatePicker = false },
//                        alignment = Alignment.TopStart
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .offset(y = 64.dp)
//                                .shadow(4.dp)
//                                .background(MaterialTheme.colorScheme.surface)
//                                .padding(16.dp)
//                        ) {
//                            DatePicker(state = datePickerState, showModeToggle = false)
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(10.dp))
//
//            // Sexo
//            SexoRadioGroup(selected = sexo, onSelected = { sexo = it })
//
//            Spacer(Modifier.height(20.dp))
//
//            //  BOTÓN
//            Button(
//                onClick = {
//                    scope.launch {
//                        val usuarioExistente = usuarioDao.getUnUser(email)
//
//                        when {
//                            nombre.isBlank() -> showToast(context, "El nombre no puede estar vacío")
//                            apellidos.isBlank() -> showToast(
//                                context, "Los apellidos no pueden estar vacíos"
//                            )
//
//                            email.isBlank() -> showToast(context, "El email no puede estar vacío")
//                            !email.matches(emailPattern) -> showToast(
//                                context, "El email no tiene formato válido"
//                            )
//
//                            contrasena.text.length < 3 -> showToast(
//                                context, "La contraseña debe tener al menos 3 caracteres"
//                            )
//
//                            incorporacion.isBlank() -> showToast(
//                                context, "La fecha de incorporación no puede estar vacía"
//                            )
//
//                            usuarioExistente != null -> showToast(
//                                context, "Ya existe un usuario con ese email"
//                            )
//
//                            else -> {
//                                val nuevoUsuario = UsuarioData(
//                                    nombreUsuario = nombre,
//                                    apellidosUsuario = apellidos,
//                                    incorporacionUsuario = incorporacion,
//                                    email = email,
//                                    sexo = sexo
//                                )
//                                usuarioDao.nuevoUsuario(nuevoUsuario)
//
//                                val dataFirebase = mapOf(
//                                    "nombre" to nombre,
//                                    "apellidos" to apellidos,
//                                    "email" to email,
//                                    "password" to contrasena.text,
//                                    "incorporacion" to incorporacion,
//                                    "sexo" to sexo
//                                )
//
//                                dbfire.collection("usuarios").document(email).set(dataFirebase)
//                                    .addOnSuccessListener { println("Guardado en Firebase") }
//                                    .addOnFailureListener { e -> println("Error Firebase: ${e.message}") }
//
//                                showToast(context, "Usuario registrado correctamente. Inicie sesión")
//                                navController.navigate(AppScreens.Inicio.route)
//                            }
//                        }
//                    }
//                }) {
//                Text("Agregar usuario")
//            }
//        }
//    }
//}
//
//// ----------- FUNCIONES AUXILIARES -----------
//fun showToast(context: Context, mensaje: String) {
//    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
//}
//
//fun convertMillisToDate(millis: Long): String {
//    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
//    return formatter.format(Date(millis))
//}
