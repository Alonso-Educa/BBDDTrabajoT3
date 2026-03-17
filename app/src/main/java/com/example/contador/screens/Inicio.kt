package com.example.contador.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Room
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.SesionData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.example.contador.notification.NotificationHandler
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inicio(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Firebase
    val dbFirebase = FirebaseFirestore.getInstance()

    // Estados del formulario
    var email by remember { mutableStateOf("") }
    val contrasena = rememberTextFieldState("")
    var passVisible by remember { mutableStateOf(false) }
    val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

    // Room
    val db = remember {
        Room.databaseBuilder(
            context.applicationContext, AppDB::class.java, Estructura.DB.NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    val usuarioDao = db.usuarioDao()
    val sesionDao = db.sesionDao()

    val notificationHandler = NotificationHandler(context)
    var auth: FirebaseAuth = Firebase.auth
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(60.dp),
                title = { Text(text = "Iniciar sesión", fontSize = 20.sp) },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bienvenido", fontSize = 30.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(30.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { if (it.length <= 30) email = it },
                label = { Text("Email") },
                modifier = Modifier.width(300.dp)
            )
            Spacer(Modifier.height(15.dp))

            // Contraseña
            OutlinedSecureTextField(
                state = contrasena,
                label = { Text("Contraseña") },
                modifier = Modifier.width(300.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector = if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                textObfuscationMode = if (passVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped
            )
            Spacer(Modifier.height(15.dp))

            Text(
                "¿Olvidaste tu contraseña?",
                modifier = Modifier.clickable { showDialog = true },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Red
            )

            if (showDialog) {
                DialogRecuperarContrasena(
                    onDismiss = { showDialog = false }
                )
            }

            Spacer(Modifier.height(15.dp))

            // Botón de login
            Button(onClick = {
                when {
                    email.isBlank() -> {
                        Toast.makeText(context, "El email no puede estar vacío", Toast.LENGTH_SHORT)
                            .show()
                    }

                    !email.matches(emailPattern) -> {
                        Toast.makeText(
                            context,
                            "El email no tiene un formato válido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    contrasena.text.isBlank() -> {
                        Toast.makeText(
                            context,
                            "La contraseña no puede estar vacía",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    contrasena.text.length < 4 -> {
                        Toast.makeText(
                            context,
                            "La contraseña debe tener al menos 4 caracteres",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        // Iniciar sesión con Firebase Auth
                        auth.signInWithEmailAndPassword(email, contrasena.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                                    // Obtener datos del usuario desde Firestore
                                    dbFirebase.collection("usuarios").document(uid).get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                val nombreUsuario = document.getString("nombre")
                                                val apellidos = document.getString("apellidos")
                                                val sexo = document.getString("sexo")
                                                val incorporacion =
                                                    document.getString("incorporacion")
                                                val emailUser = document.getString("email")

                                                val usuarioLocal = UsuarioData(
                                                    idUsuario = uid,
                                                    nombreUsuario = nombreUsuario ?: "",
                                                    apellidosUsuario = apellidos ?: "",
                                                    email = emailUser ?: email,
                                                    sexo = sexo ?: "",
                                                    incorporacionUsuario = incorporacion ?: ""
                                                )

                                                // Guardar en Room y sesión
                                                scope.launch {
                                                    usuarioDao.nuevoUsuario(usuarioLocal)

                                                    val fecha = SimpleDateFormat(
                                                        "dd-MM-yyyy",
                                                        Locale.getDefault()
                                                    ).format(Date())
                                                    val sesion = SesionData(
                                                        idUsuario = uid,
                                                        fechaInicio = fecha
                                                    )
                                                    sesionDao.eliminarTodasLasSesiones()
                                                    sesionDao.nuevaSesion(sesion)
                                                }

                                                notificationHandler.enviarNotificacionSimple(
                                                    "Inicio de sesión correcto",
                                                    "Bienvenido $nombreUsuario"
                                                )

                                                Toast.makeText(
                                                    context,
                                                    "Inicio de sesión correcto",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigate(AppScreens.MenuPrincipal.route) {
                                                    popUpTo(0) { inclusive = true }
                                                }

                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Datos del usuario no encontrados",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Error al obtener datos: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                } else {
                                    // Auth maneja automáticamente los casos de email o contraseña incorrectos
                                    val errorMsg = when (task.exception?.message) {
                                        else -> "Email o contraseña incorrectos"
                                    }
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                    Log.e("Login", "Error Auth: ${task.exception?.message}")
                                }
                            }
                    }
                }
            }) {
                Text("Iniciar sesión")
            }

            Spacer(Modifier.height(10.dp))

            Button(onClick = { navController.navigate(AppScreens.Formulario.route) }) {
                Text("Crear usuario")
            }
        }
    }
}

@Composable
fun DialogRecuperarContrasena(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    var emailRecuperar by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Info, contentDescription = null)
        },
        title = { Text("Recuperar contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Introduce tu email y te enviaremos un correo para restablecer tu contraseña.")
                OutlinedTextField(
                    value = emailRecuperar,
                    onValueChange = { emailRecuperar = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    emailRecuperar.isBlank() -> {
                        Toast.makeText(context, "El email no puede estar vacío", Toast.LENGTH_SHORT)
                            .show()
                    }

                    !emailRecuperar.matches(emailPattern) -> {
                        Toast.makeText(
                            context,
                            "El email no tiene un formato válido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        auth.sendPasswordResetEmail(emailRecuperar)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Correo enviado a $emailRecuperar. Revisa también la carpeta de Spam.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismiss()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }) {
                Text("Enviar correo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}