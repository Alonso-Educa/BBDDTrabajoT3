package com.example.contador.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.AsyncImage
import com.composables.icons.lucide.HousePlus
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Phone
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.PublicacionesData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPublicaciones(navController: NavController) {

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
    val publicacionDao = db.publicacionesDao()

    // Estados
    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }
    var listaPublicaciones by remember { mutableStateOf<List<PublicacionesData>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogEliminar by remember { mutableStateOf(false) }
    var publicacionEditando by remember { mutableStateOf<PublicacionesData?>(null) }

    var idUsuarioSesionActual by remember { mutableStateOf("") }

    // Campos para formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var urlImagen by remember { mutableStateOf("") }
    val idPublicacion by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }

    // Cargar sesión y lista de publicaciones
    LaunchedEffect(Unit) {
        val sesion = sesionDao.getUsuarioSesionActual()
        idUsuarioSesionActual = sesion?.idUsuario ?: ""
        usuarioSesion = sesion?.let { usuarioDao.getUsuarioPorId(it.idUsuario) }

        firestore.collection("publicaciones")
            .whereEqualTo("idUsuario", idUsuarioSesionActual)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { publicacion ->
                    try {
                        PublicacionesData(
                            idPublicacion = publicacion.id.hashCode(), // ID de Firebase
                            idUsuario = publicacion.getString("idUsuario") ?: "",
                            nombreUsuario = publicacion.getString("nombreUsuario") ?: "",
                            titulo = publicacion.getString("titulo") ?: "",
                            descripcion = publicacion.getString("descripcion") ?: "",
                            urlImagen = publicacion.getString("urlImagen") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                listaPublicaciones = lista
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error cargando publicaciones", Toast.LENGTH_SHORT).show()
            }
    }

    // UI
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Mis Publicaciones", fontSize = 15.sp) }, colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White
            ), navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
            }, actions = {
                // Regresar al menú
                IconButton(onClick = {
                    navController.navigate(AppScreens.MenuPrincipal.route)
                }) {
                    Icon(Icons.Default.Home, contentDescription = "Regresar al menú principal")
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
            })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            publicacionEditando = null
            nombreUsuario = ""
            titulo = ""
            descripcion = ""
            urlImagen = ""
            showDialog = true
        }) {
            Icon(Icons.Default.Add, contentDescription = "Añadir publicacion")
        }
    }, bottomBar = { BottomBarPublicaciones(navController as NavHostController) }) { padding ->
        // Lista de publicaciones
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(listaPublicaciones) { publicacion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    ) {
                        Text(publicacion.nombreUsuario, fontWeight = FontWeight.Bold)
                        AsyncImage(
                            model = publicacion.urlImagen,
                            contentDescription = publicacion.descripcion,
                            modifier = Modifier.size(200.dp)
                        )
                        Text(publicacion.titulo, fontWeight = FontWeight.Bold)
                        Text(
                            publicacion.descripcion,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.animateContentSize()
                        )
                    }

                    // Editar / Eliminar
                    if (publicacion.idUsuario == idUsuarioSesionActual) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                publicacionEditando = publicacion
                                titulo = publicacion.titulo
                                descripcion = publicacion.descripcion
                                urlImagen = publicacion.urlImagen
                                nombreUsuario = publicacion.nombreUsuario
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = {
                                publicacionEditando = publicacion
                                showDialogEliminar = true
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }

                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
    // Para la eliminación de la publicacion
    if (showDialogEliminar && publicacionEditando != null) {
        DialogPublicacion(
            onDismiss = {
                showDialogEliminar = false
                publicacionEditando = null
            }, onConfirm = {
                scope.launch {
                    val id = publicacionEditando!!.idPublicacion

                    publicacionDao.eliminarPublicacion(id)

                    firestore.collection("publicaciones").whereEqualTo("idPublicacionRoom", id).get()
                        .addOnSuccessListener { result ->
                            result.documents.forEach { it.reference.delete() }
                        }

                    listaPublicaciones = publicacionDao.getPublicacionesDeUsuario(idUsuarioSesionActual)

                    Toast.makeText(context, "Publicacion eliminada", Toast.LENGTH_SHORT).show()

                    showDialogEliminar = false
                    publicacionEditando = null
                }
            }, titulo = publicacionEditando!!.titulo
        )
    }


    // Dialogo para añadir / editar publicacion
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (publicacionEditando == null) "Nueva publicación" else "Editar Publicación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(nombreUsuario, { nombreUsuario = it }, label = { Text("Nombre de Usuario") })
                    OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") })
                    OutlinedTextField(
                        descripcion,
                        { descripcion = it },
                        label = { Text("Descripción") })
                    OutlinedTextField(urlImagen, { urlImagen = it }, label = { Text("URL Imagen") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val publicacion = PublicacionesData(
                            idPublicacion = 0, // Room lo autogenera
                            idUsuario = idUsuarioSesionActual,      // idUsuario como cadena
                            titulo = titulo,
                            descripcion = descripcion,
                            urlImagen = urlImagen,
                            nombreUsuario = nombreUsuario
                        )

                        if (publicacionEditando == null) {
                            // Guardar en Room
                            publicacionDao.nuevaPublicacion(publicacion)

                            // Crear documento con ID único en Firebase
                            val docRef = firestore.collection("publicaciones").document()
                            val dataFirebase = mapOf(
                                "idPublicacion" to docRef.id,
                                "idUsuario" to idUsuarioSesionActual,
                                "nombreUsuario" to nombreUsuario,
                                "titulo" to titulo,
                                "descripcion" to descripcion,
                                "urlImagen" to urlImagen
                            )

                            docRef.set(dataFirebase).addOnSuccessListener {
                                Toast.makeText(
                                    context, "Publicacion subida a Firebase", Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    context, "Error Firebase: ${e.message}", Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            // Actualizar Room
                            publicacionDao.actualizaPublicacion(publicacion)

                            // Actualizar Firebase con el mismo documento
                            firestore.collection("publicaciones")
                                .whereEqualTo("idPublicacionRoom", publicacionEditando!!.idPublicacion).get()
                                .addOnSuccessListener { result ->
                                    result.documents.forEach { doc ->
                                        doc.reference.set(
                                            mapOf(
                                                "idPublicacionRoom" to publicacionEditando!!.idPublicacion,
                                                "idUsuario" to idUsuarioSesionActual,
                                                "nombreUsuario" to nombreUsuario,
                                                "titulo" to titulo,
                                                "descripcion" to descripcion,
                                                "urlImagen" to urlImagen
                                            )
                                        )
                                    }
                                    Toast.makeText(
                                        context,
                                        "Publicacion actualizada en Firebase",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        context, "Error Firebase: ${e.message}", Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                        // Recargar lista usando el idUsuario como cadena
                        listaPublicaciones = publicacionDao.getPublicacionesDeUsuario(idUsuarioSesionActual)
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
fun BottomBarPublicaciones(navController: NavHostController) {
    val items = listOf(AppScreens.MisPublicaciones, AppScreens.TodasPublicaciones)
    val labels = listOf("Mis Publicaciones", "Todas las Publicaciones")
    val icons = listOf(Icons.Default.AccountCircle, Icons.Default.PhoneIphone)

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
fun DialogPublicacion(
    onDismiss: () -> Unit, onConfirm: () -> Unit, titulo: String
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Icono de ejemplo")
        },
        title = { Text("Confirmar la eliminación de la publicación") },
        text = { Text("¿Desea eliminar la publicación?: $titulo") },
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
        })
}

// Para Tarjeta (1)
@Composable
fun TarjetaPublicaciones(
    titulo: String, descripcion: String, urlImagen: String, precio: Double, tipo: String
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.size(width = 240.dp, height = 100.dp)
    ) {
        Row() {
            // URL de la imagen
//        Icon()
            Column() {
                // Titulo de la publicación
                Text(
                    text = titulo,
                    modifier = Modifier.padding(18.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                // "Descripción de la publicación"
                Text(
                    text = descripcion,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                // Precio de la publicación
                Text(
                    text = precio.toString(),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                // Tipo de la publicación
                Text(
                    text = tipo,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}