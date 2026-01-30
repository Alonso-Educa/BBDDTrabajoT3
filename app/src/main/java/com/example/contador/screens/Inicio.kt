//package com.example.contador.screens
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.text.input.TextObfuscationMode
//import androidx.compose.foundation.text.input.rememberTextFieldState
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedSecureTextField
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.room.Room
//import com.example.contador.localdb.AppDB
//import com.example.contador.localdb.Estructura
//import com.example.contador.localdb.SesionData
//import com.example.contador.localdb.UsuarioData
//import com.example.contador.navigation.AppScreens
//import com.google.firebase.Firebase
//import com.google.firebase.firestore.firestore
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
////Contraseña: ***331033***
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Inicio(navController: NavController) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    // Firebase
//    val dbFirebase = Firebase.firestore
//
//    // Estados del formulario
//    var email by remember { mutableStateOf("") }
//    val contrasena = rememberTextFieldState("")
//    var passVisible by remember { mutableStateOf(false) }
//
//    // Room
//    val dbLocal = remember {
//        Room.databaseBuilder(context, AppDB::class.java, Estructura.DB.NAME)
//            .allowMainThreadQueries().build()
//    }
//    val usuarioDao = dbLocal.usuarioDao()
//    val sesionDao = dbLocal.sesionDao()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                modifier = Modifier.height(60.dp),
//                title = { Text(text = "Iniciar sesión", fontSize = 15.sp) },
//                colors = topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = Color.White
//                )
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text("Bienvenido", fontSize = 30.sp, fontWeight = FontWeight.Bold)
//
//            Spacer(Modifier.height(30.dp))
//
//            // Email
//            OutlinedTextField(
//                value = email,
//                onValueChange = { if (it.length <= 30) email = it },
//                label = { Text("Email") },
//                modifier = Modifier.width(300.dp)
//            )
//            Spacer(Modifier.height(15.dp))
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
//            Spacer(Modifier.height(15.dp))
//
//            // Botón de login
//            Button(onClick = {
//                if (email.isBlank() || contrasena.text.isBlank()) {
//                    Toast.makeText(context, "No puede haber campos en blanco", Toast.LENGTH_SHORT).show()
//                    return@Button
//                }
//
//                // Coroutines para Room
//                scope.launch {
//                    // Consulta Firebase
//                    dbFirebase.collection("usuarios").document(email).get()
//                        .addOnSuccessListener { usuario ->
//                            val data = usuario.data
//                            if (data != null) {
//                                val pwd = data["password"] as? String
//                                val emailUser = data["email"] as? String
//
//                                if (pwd != null && emailUser != null) {
//                                    // Consulta Room
//                                    scope.launch {
//                                        val localUser = usuarioDao.getUnUser(emailUser)
//                                        if (pwd == contrasena.text && localUser != null) {
//                                            val fecha = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
//                                            val sesion = SesionData(
//                                                idUsuario = localUser.idUsuario,
//                                                fechaInicio = fecha
//                                            )
//                                            sesionDao.nuevaSesion(sesion)
//                                            navController.navigate(AppScreens.Resultados.route)
//                                            Toast.makeText(context, "Usuario inicia sesión", Toast.LENGTH_SHORT).show()
//                                        } else {
//                                            Toast.makeText(context, "Credenciales inválidas o usuario no registrado localmente", Toast.LENGTH_SHORT).show()
//                                        }
//                                    }
//                                } else {
//                                    Toast.makeText(context, "Datos incompletos en la nube", Toast.LENGTH_SHORT).show()
//                                }
//                            } else {
//                                Toast.makeText(context, "Usuario no existe en la nube", Toast.LENGTH_SHORT).show()
//                            }
//                        }.addOnFailureListener {
//                            Toast.makeText(context, "Error en la consulta de Firebase", Toast.LENGTH_SHORT).show()
//                        }
//                }
//
//            }) {
//                Text("Iniciar sesión")
//            }
//
//            Spacer(Modifier.height(10.dp))
//
//            Button(onClick = { navController.navigate(AppScreens.Formulario.route) }) {
//                Text("Crear usuario")
//            }
//
//            Spacer(Modifier.height(10.dp))
//
//            Button(onClick = { navController.navigate(AppScreens.Resultados.route) }) {
//                Text("Ir a resultados")
//            }
//        }
//    }
//}
