package com.example.contador.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.PublicacionesData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPublicaciones(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    val db = remember {
        Room.databaseBuilder(
            context.applicationContext, AppDB::class.java, Estructura.DB.NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
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
    val snackbarHostState = remember { SnackbarHostState() }

    // Campos del formulario
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var urlImagen by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }

    // FIX 1: Helper reutilizable que recarga desde Firestore (fuente de verdad)
    fun recargarPublicacionesDesdeFirestore(uid: String) {
        if (uid.isEmpty()) return
        firestore.collection("publicaciones")
            .whereEqualTo("idUsuario", uid)
            .get()
            .addOnSuccessListener { result ->
                listaPublicaciones = result.documents.mapNotNull { doc ->
                    try {
                        PublicacionesData(
                            idPublicacion = doc.id.hashCode(),
                            idUsuario = doc.getString("idUsuario") ?: "",
                            nombreUsuario = doc.getString("nombreUsuario") ?: "",
                            titulo = doc.getString("titulo") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            urlImagen = doc.getString("urlImagen") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("MisPublicaciones", "Error parseando publicación: ${e.message}")
                        null
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MisPublicaciones", "Error Firestore: ${e.message}")
                Toast.makeText(context, "Error cargando publicaciones: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // FIX 1: Leer uid DENTRO del LaunchedEffect para garantizar que Auth ya está restaurado
    LaunchedEffect(Unit) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            navController.navigate(AppScreens.Inicio.route) {
                popUpTo(0) { inclusive = true }
            }
            return@LaunchedEffect
        }
        idUsuarioSesionActual = uid
        usuarioSesion = usuarioDao.getUsuarioPorId(uid)
        recargarPublicacionesDesdeFirestore(uid)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mis Publicaciones", fontSize = 15.sp) },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(AppScreens.MenuPrincipal.route) }) {
                        Icon(Icons.Default.Home, contentDescription = "Regresar al menú principal")
                    }

                    IconButton(onClick = {
                        scope.launch {
                            if (idUsuarioSesionActual.isNotEmpty()) {
                                sesionDao.eliminarSesionUsuario(idUsuarioSesionActual)
                            }
                            Firebase.auth.signOut()
                            navController.navigate(AppScreens.Inicio.route) {
                                popUpTo(0) { inclusive = true }
                                Toast.makeText(context, "Cerrando sesión", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }

                    usuarioSesion?.let { usuario ->
                        var showCardDialog by remember { mutableStateOf(false) }
                        val inicial = usuario.nombreUsuario.firstOrNull()?.uppercase() ?: "U"

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                                .clickable { showCardDialog = true }
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = inicial,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        if (showCardDialog) {
                            Dialog(onDismissRequest = { showCardDialog = false }) {
                                Card(
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(8.dp),
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .widthIn(min = 200.dp, max = 300.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                                                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                                            ) {
                                                Text(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    text = inicial,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                    text = "${usuario.nombreUsuario} ${usuario.apellidosUsuario}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Email: ${usuario.email}", style = MaterialTheme.typography.bodyMedium)
                                                Text("Sexo: ${usuario.sexo}", style = MaterialTheme.typography.bodyMedium)
                                                Text("Incorporación: ${usuario.incorporacionUsuario}", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                        Button(
                                            modifier = Modifier.padding(bottom = 16.dp),
                                            onClick = { showCardDialog = false }
                                        ) { Text("Cerrar") }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
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
        },
        bottomBar = { BottomBarPublicaciones(navController as NavHostController) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            if (listaPublicaciones.isEmpty()) {
                item {
                    Text(
                        text = "No has publicado nada todavía",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
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
    }

    // Diálogo de confirmación de eliminación
    if (showDialogEliminar && publicacionEditando != null) {
        DialogPublicacion(
            onDismiss = {
                showDialogEliminar = false
                publicacionEditando = null
            },
            onConfirm = {
                scope.launch {
                    // FIX 2: Guardar snapshot antes de borrar
                    val publicacionRecuperar = publicacionEditando!!
                    val id = publicacionRecuperar.idPublicacion

                    publicacionDao.eliminarPublicacion(id)

                    firestore.collection("publicaciones")
                        .whereEqualTo("idUsuario", idUsuarioSesionActual)
                        .whereEqualTo("titulo", publicacionRecuperar.titulo)
                        .get()
                        .addOnSuccessListener { result ->
                            result.documents.forEach { it.reference.delete() }
                        }

                    // FIX 2: Actualizar lista en memoria, no leer de Room
                    listaPublicaciones = listaPublicaciones.filter { it.idPublicacion != id }

                    Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                    showDialogEliminar = false
                    publicacionEditando = null

                    // Snackbar para deshacer eliminación
                    val result = snackbarHostState.showSnackbar(
                        message = "¿Deseas deshacer la eliminación?",
                        actionLabel = "Deshacer",
                        duration = SnackbarDuration.Long
                    )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            publicacionDao.nuevaPublicacion(publicacionRecuperar)

                            val docRef = firestore.collection("publicaciones").document()
                            // FIX 3: Usar datos de publicacionRecuperar, NO variables del formulario
                            docRef.set(
                                mapOf(
                                    "idPublicacion" to docRef.id,
                                    "idUsuario" to idUsuarioSesionActual,
                                    "nombreUsuario" to publicacionRecuperar.nombreUsuario,
                                    "titulo" to publicacionRecuperar.titulo,
                                    "descripcion" to publicacionRecuperar.descripcion,
                                    "urlImagen" to publicacionRecuperar.urlImagen
                                )
                            ).addOnSuccessListener {
                                Toast.makeText(context, "Publicación restaurada", Toast.LENGTH_SHORT).show()
                                recargarPublicacionesDesdeFirestore(idUsuarioSesionActual)
                            }.addOnFailureListener { e ->
                                Toast.makeText(context, "Error Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        SnackbarResult.Dismissed -> { /* sin acción */ }
                    }
                }
            },
            titulo = publicacionEditando!!.titulo
        )
    }

    // Diálogo para añadir / editar publicación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (publicacionEditando == null) "Nueva publicación" else "Editar Publicación") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(nombreUsuario, { nombreUsuario = it }, label = { Text("Nombre de Usuario") })
                    OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") })
                    OutlinedTextField(descripcion, { descripcion = it }, label = { Text("Descripción") })
                    OutlinedTextField(urlImagen, { urlImagen = it }, label = { Text("URL Imagen") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        when {
                            nombreUsuario.isBlank() -> showToast(context, "El nombre no puede estar vacío")
                            titulo.isBlank() -> showToast(context, "El título no puede estar vacío")
                            descripcion.isBlank() -> showToast(context, "La descripción no puede estar vacía")

                            else -> {
                                val publicacionNueva = PublicacionesData(
                                    idPublicacion = publicacionEditando?.idPublicacion ?: 0,
                                    idUsuario = idUsuarioSesionActual,
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    urlImagen = urlImagen,
                                    nombreUsuario = nombreUsuario
                                )

                                // --- NUEVA PUBLICACIÓN ---
                                if (publicacionEditando == null) {
                                    publicacionDao.nuevaPublicacion(publicacionNueva)

                                    val docRef = firestore.collection("publicaciones").document()
                                    docRef.set(
                                        mapOf(
                                            "idPublicacion" to docRef.id,
                                            "idUsuario" to idUsuarioSesionActual,
                                            "nombreUsuario" to nombreUsuario,
                                            "titulo" to titulo,
                                            "descripcion" to descripcion,
                                            "urlImagen" to urlImagen
                                        )
                                    ).addOnSuccessListener {
                                        Toast.makeText(context, "Publicación subida a Firebase", Toast.LENGTH_SHORT).show()
                                        // FIX 2: Recargar desde Firestore tras crear
                                        recargarPublicacionesDesdeFirestore(idUsuarioSesionActual)
                                    }.addOnFailureListener { e ->
                                        Toast.makeText(context, "Error Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }

                                    showDialog = false

                                    // --- EDITAR PUBLICACIÓN ---
                                } else {
                                    val publicacionAnterior = publicacionEditando!!
                                    val tituloAnterior = publicacionAnterior.titulo

                                    firestore.collection("publicaciones")
                                        .whereEqualTo("idUsuario", idUsuarioSesionActual)
                                        .whereEqualTo("titulo", tituloAnterior)
                                        .get()
                                        .addOnSuccessListener { result ->
                                            result.documents.forEach { doc ->
                                                doc.reference.update(
                                                    mapOf(
                                                        "idUsuario" to idUsuarioSesionActual,
                                                        "nombreUsuario" to nombreUsuario,
                                                        "titulo" to titulo,
                                                        "descripcion" to descripcion,
                                                        "urlImagen" to urlImagen
                                                    )
                                                )
                                            }

                                            // FIX 2: Actualizar lista en memoria
                                            listaPublicaciones = listaPublicaciones.map {
                                                if (it.idPublicacion == publicacionAnterior.idPublicacion) publicacionNueva else it
                                            }

                                            showDialog = false
                                            publicacionEditando = null

                                            // Snackbar para deshacer edición
                                            scope.launch {
                                                val snackResult = snackbarHostState.showSnackbar(
                                                    message = "Publicación actualizada",
                                                    actionLabel = "Deshacer",
                                                    duration = SnackbarDuration.Long
                                                )
                                                when (snackResult) {
                                                    SnackbarResult.ActionPerformed -> {
                                                        firestore.collection("publicaciones")
                                                            .whereEqualTo("idUsuario", idUsuarioSesionActual)
                                                            .whereEqualTo("titulo", titulo)
                                                            .get()
                                                            .addOnSuccessListener { revertResult ->
                                                                revertResult.documents.forEach { doc ->
                                                                    doc.reference.update(
                                                                        mapOf(
                                                                            "nombreUsuario" to publicacionAnterior.nombreUsuario,
                                                                            "titulo" to publicacionAnterior.titulo,
                                                                            "descripcion" to publicacionAnterior.descripcion,
                                                                            "urlImagen" to publicacionAnterior.urlImagen
                                                                        )
                                                                    )
                                                                }
                                                                listaPublicaciones = listaPublicaciones.map {
                                                                    if (it.idPublicacion == publicacionAnterior.idPublicacion) publicacionAnterior else it
                                                                }
                                                                Toast.makeText(context, "Edición deshecha", Toast.LENGTH_SHORT).show()
                                                            }
                                                    }
                                                    SnackbarResult.Dismissed -> { /* sin acción */ }
                                                }
                                            }

                                        }.addOnFailureListener { e ->
                                            Toast.makeText(context, "Error Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        }
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// Barra de navegación inferior
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
                label = { Text(labels[index]) }
            )
        }
    }
}

// Cuadro de diálogo de confirmación de eliminación
@Composable
fun DialogPublicacion(
    onDismiss: () -> Unit, onConfirm: () -> Unit, titulo: String
) {
    AlertDialog(
        icon = { Icon(Icons.Default.Info, contentDescription = "Icono de ejemplo") },
        title = { Text("Confirmar la eliminación de la publicación") },
        text = { Text("¿Desea eliminar la publicación?: $titulo") },
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// Tarjeta de publicación
@Composable
fun TarjetaPublicaciones(
    titulo: String, descripcion: String, urlImagen: String, precio: Double, tipo: String
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.size(width = 240.dp, height = 100.dp)
    ) {
        Row {
            Column {
                Text(text = titulo, modifier = Modifier.padding(18.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Text(text = descripcion, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
                Text(text = precio.toString(), modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
                Text(text = tipo, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
            }
        }
    }
}