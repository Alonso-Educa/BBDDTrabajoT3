package com.example.contador.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.AsyncImage
import com.composables.icons.lucide.HousePlus
import com.composables.icons.lucide.Lucide
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.InmueblesData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisInmuebles(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    val db = remember {
        Room.databaseBuilder(
            context.applicationContext, AppDB::class.java, Estructura.DB.NAME
        ).allowMainThreadQueries().build()
    }

    val sesionDao = db.sesionDao()
    val usuarioDao = db.usuarioDao()
    val inmuebleDao = db.inmueblesDao()

    // Estados
    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }
    var listaInmuebles by remember { mutableStateOf<List<InmueblesData>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogEliminar by remember { mutableStateOf(false) }
    var inmuebleEditando by remember { mutableStateOf<InmueblesData?>(null) }

    var idUsuarioSesionActual by remember { mutableStateOf("") }

    // Campos para formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var urlImagen by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Alquiler") }

    // Cargar sesión y lista de inmuebles
    LaunchedEffect(Unit) {
        val sesion = sesionDao.getUsuarioSesionActual()
        idUsuarioSesionActual = sesion?.idUsuario ?: ""
        usuarioSesion = sesion?.let { usuarioDao.getUsuarioPorId(it.idUsuario) }
        listaInmuebles = inmuebleDao.getInmueblesDeUsuario(idUsuarioSesionActual)
    }

    // UI
    Scaffold(topBar = {
        TopAppBar(title = { Text("Mis Inmuebles") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
            }
        }, actions = {
            // Ir a TodosInmuebles
            IconButton(onClick = {
                navController.navigate(AppScreens.TodosInmuebles.route)
            }) {
                Icon(Lucide.HousePlus, contentDescription = "Todos los inmuebles")
            }

            // Cerrar sesión
            IconButton(onClick = {
                scope.launch {
                    if (idUsuarioSesionActual.isNotEmpty()) {
                        sesionDao.eliminarSesionUsuario(idUsuarioSesionActual)
                    }
                    navController.navigate(AppScreens.Inicio.route) {
                        popUpTo(0) { inclusive = true }
                        Toast.makeText(context, "Cerrando sesión", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
            }

            // Inicial usuario
            usuarioSesion?.let {
                val inicial = it.nombreUsuario.firstOrNull()?.uppercase() ?: "U"
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer, CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    Text(text = inicial, style = MaterialTheme.typography.titleMedium)
                }
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            inmuebleEditando = null
            titulo = ""
            descripcion = ""
            urlImagen = ""
            precio = ""
            tipo = "Alquiler"
            showDialog = true
        }) {
            Icon(Icons.Default.Add, contentDescription = "Añadir inmueble")
        }
    }, bottomBar = { BottomBarInmuebles(navController as NavHostController) }) { padding ->
        // Lista de inmuebles
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(listaInmuebles) { inmueble ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = inmueble.urlImagen,
                        contentDescription = inmueble.descripcion,
                        modifier = Modifier.size(80.dp)
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    ) {
                        Text(inmueble.titulo, fontWeight = FontWeight.Bold)
                        Text(
                            inmueble.descripcion,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.animateContentSize()
                        )
                        Text("${inmueble.precio} €")
                        Text("Tipo: ${inmueble.tipo}")
                    }

                    // Editar / Eliminar
                    if (inmueble.idUsuario == idUsuarioSesionActual) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                inmuebleEditando = inmueble
                                titulo = inmueble.titulo
                                descripcion = inmueble.descripcion
                                urlImagen = inmueble.urlImagen
                                precio = inmueble.precio.toString()
                                tipo = inmueble.tipo
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = {
                                inmuebleEditando = inmueble
                                showDialogEliminar = true
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }

                        }
                    }
                }
                Divider()
            }
        }
    }
    // Para la eliminación del inmueble
    if (showDialogEliminar && inmuebleEditando != null) {
        DialogInmueble(
            onDismiss = {
                showDialogEliminar = false
                inmuebleEditando = null
            }, onConfirm = {
                scope.launch {
                    val id = inmuebleEditando!!.idInmueble

                    inmuebleDao.eliminarInmueble(id)

                    firestore.collection("inmuebles")
                        .whereEqualTo("idInmuebleRoom", id)
                        .get()
                        .addOnSuccessListener { result ->
                            result.documents.forEach { it.reference.delete() }
                        }

                    listaInmuebles = inmuebleDao.getInmueblesDeUsuario(idUsuarioSesionActual)

                    Toast.makeText(context, "Inmueble eliminado", Toast.LENGTH_SHORT).show()

                    showDialogEliminar = false
                    inmuebleEditando = null
                }
            }, titulo = inmuebleEditando!!.titulo
        )
    }


    // Dialogo para añadir / editar inmueble
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (inmuebleEditando == null) "Nuevo Inmueble" else "Editar Inmueble") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") })
                    OutlinedTextField(
                        descripcion,
                        { descripcion = it },
                        label = { Text("Descripción") })
                    OutlinedTextField(urlImagen, { urlImagen = it }, label = { Text("URL Imagen") })
                    OutlinedTextField(precio, { precio = it }, label = { Text("Precio (€)") })
                    OutlinedTextField(
                        tipo,
                        { tipo = it },
                        label = { Text("Tipo (Alquiler/Venta)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val inmueble = InmueblesData(
                            idInmueble = 0, // Room lo autogenera
                            idUsuario = idUsuarioSesionActual
                                ?: "",      // idUsuario como cadena
                            titulo = titulo,
                            descripcion = descripcion,
                            urlImagen = urlImagen,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            tipo = tipo
                        )

                        if (inmuebleEditando == null) {
                            // Guardar en Room
                            inmuebleDao.nuevoInmueble(inmueble)

                            // Crear documento con ID único en Firebase
                            val docRef = firestore.collection("inmuebles").document()
                            val dataFirebase = mapOf(
                                "idInmueble" to docRef.id,
                                "idUsuario" to idUsuarioSesionActual,
                                "titulo" to titulo,
                                "descripcion" to descripcion,
                                "urlImagen" to urlImagen,
                                "precio" to precio.toDoubleOrNull(),
                                "tipo" to tipo
                            )

                            docRef.set(dataFirebase).addOnSuccessListener {
                                Toast.makeText(
                                    context, "Inmueble subido a Firebase", Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    context, "Error Firebase: ${e.message}", Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            // Actualizar Room
                            inmuebleDao.actualizaInmueble(inmueble)

                            // Actualizar Firebase con el mismo documento
                            firestore.collection("inmuebles")
                                .whereEqualTo("idInmuebleRoom", inmuebleEditando!!.idInmueble)
                                .get()
                                .addOnSuccessListener { result ->
                                    result.documents.forEach { doc ->
                                        doc.reference.set(
                                            mapOf(
                                                "idInmuebleRoom" to inmuebleEditando!!.idInmueble,
                                                "idUsuario" to idUsuarioSesionActual,
                                                "titulo" to titulo,
                                                "descripcion" to descripcion,
                                                "urlImagen" to urlImagen,
                                                "precio" to precio.toDoubleOrNull(),
                                                "tipo" to tipo
                                            )
                                        )
                                    }
                                    Toast.makeText(
                                        context,
                                        "Inmueble actualizado en Firebase",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Error Firebase: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                        // Recargar lista usando el idUsuario como cadena
                        listaInmuebles =
                            inmuebleDao.getInmueblesDeUsuario(idUsuarioSesionActual)
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
            })
    }
}

// Para Barra de navegación inferior (5)
@Composable
fun BottomBarInmuebles(navController: NavHostController) {
    val items = listOf(AppScreens.MisInmuebles, AppScreens.TodosInmuebles)
    val labels = listOf("Mis Inmuebles", "Todos los Inmuebles")
    val icons = listOf(Icons.Default.AccountCircle, Lucide.HousePlus)

    NavigationBar {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                selected = navController.currentDestination?.route == screen.route,
                onClick = { navController.navigate(screen.route) },
                icon = { Icon(icons[index], contentDescription = labels[index]) },
                label = { Text(labels[index]) })
        }
    }
}

// Para cuadro de diálogo (3)
@Composable
fun DialogInmueble(
    onDismiss: () -> Unit, onConfirm: () -> Unit, titulo: String
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Icono de ejemplo")
        },
        title = { Text("Confirmar la eliminación del inmueble") },
        text = { Text("¿Desea eliminar el inmueble?: $titulo") },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Para Tarjeta (1)
@Composable
fun TarjetaInmuebles(
    titulo: String,
    descripcion: String,
    urlImagen: String,
    precio: Double,
    tipo: String
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Row() {
            // URL de la imagen
//        Icon()
            Column() {
                // Titulo del inmueble
                Text(
                    text = titulo,
                    modifier = Modifier
                        .padding(18.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                // "Descripción del inmueble"
                Text(
                    text = descripcion,
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                // Precio del inmueble
                Text(
                    text = precio.toString(),
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                // Tipo del inmueble
                Text(
                    text = tipo,
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}