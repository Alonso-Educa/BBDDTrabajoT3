package com.example.contador.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
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
import com.composables.icons.lucide.Settings
import com.example.contador.R
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.InmueblesData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

// https://i.pinimg.com/originals/ce/2a/a4/ce2aa4b802e2645bb741353f3e519d9f.jpg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisInmuebles(navController: NavController) {

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
    val inmuebleDao = db.inmueblesDao()

    // Para ir a la web
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))

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
    var showSnackBar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    // Cargar sesión y lista de inmuebles
    val uid = Firebase.auth.currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        idUsuarioSesionActual = uid
        usuarioSesion = usuarioDao.getUsuarioPorId(uid)

        firestore.collection("inmuebles")
            .whereEqualTo("idUsuario", uid) // siempre disponible
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { inmueble ->
                    try {
                        InmueblesData(
                            idInmueble = inmueble.id.hashCode(), // usamos el ID de Firebase directamente
                            idUsuario = inmueble.getString("idUsuario") ?: "",
                            titulo = inmueble.getString("titulo") ?: "",
                            descripcion = inmueble.getString("descripcion") ?: "",
                            urlImagen = inmueble.getString("urlImagen") ?: "",
                            precio = inmueble.getDouble("precio") ?: 0.0,
                            tipo = inmueble.getString("tipo") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                listaInmuebles = lista
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error cargando inmuebles", Toast.LENGTH_SHORT).show()
            }
    }


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

                    HorizontalDivider()

                    // Sección secundaria
                    Text(
                        "Ayuda",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )

                    NavigationDrawerItem(
                        label = { Text("Ajustes") },
                        selected = false,
                        icon = { Icon(Lucide.Settings, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(AppScreens.Ajustes.route)
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Ayuda y diagnóstico") },
                        selected = false,
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.Help,
                                contentDescription = null
                            )
                        },
                        onClick = { context.startActivity(intent) },
                        badge = { Icon(Icons.Outlined.ArrowOutward, contentDescription = null) }
                    )
                }
            }
        }
    ) {
        // UI
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopAppBar(
                    title = { Text("Mis Inmuebles", fontSize = 20.sp) }, colors = topAppBarColors(
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
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Regresar al menú principal"
                            )
                        }

                        // Cerrar sesión
                        IconButton(onClick = {
                            scope.launch {
                                if (idUsuarioSesionActual.isNotEmpty()) {
                                    sesionDao.eliminarSesionUsuario(idUsuarioSesionActual)
                                }
                                Firebase.auth.signOut() // cerrar sesión de firebase
                                navController.navigate(AppScreens.Inicio.route) {
                                    popUpTo(0) { inclusive = true }
                                    Toast.makeText(context, "Cerrando sesión", Toast.LENGTH_SHORT)
                                        .show()
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
                FloatingActionButton(
                    onClick = {
                        inmuebleEditando = null
                        titulo = ""
                        descripcion = ""
                        urlImagen = ""
                        precio = ""
                        tipo = "Alquiler"
                        showDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir inmueble")
                }
            }, bottomBar = { BottomBarInmuebles(navController as NavHostController) }) { padding ->
            // Lista de inmuebles
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Galería de imágenes de tus inmuebles",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold
                )

                // Carrusel de imágenes de los inmuebles del usuario
                CarruselInmuebles(
                    inmuebles = listaInmuebles
                )

                Text(
                    text = "\nTus inmuebles",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (listaInmuebles.isEmpty()) {
                        item {
                            Text(
                                text = "No has subido inmuebles todavía",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
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
                                        "Descripción: ${inmueble.descripcion}",
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text("Precio: ${inmueble.precio} €")
                                    Text("Tipo: ${inmueble.tipo}")
                                }

                                if (inmueble.idUsuario == idUsuarioSesionActual) {
                                    MenuDesplegableOpcionesInmuebles(
                                        onEditarClick = {
                                            inmuebleEditando = inmueble
                                            titulo = inmueble.titulo
                                            descripcion = inmueble.descripcion
                                            urlImagen = inmueble.urlImagen
                                            precio = inmueble.precio.toString()
                                            tipo = inmueble.tipo
                                            showDialog = true
                                        },
                                        onEliminarClick = {
                                            inmuebleEditando = inmueble
                                            showDialogEliminar = true
                                        }
                                    )
                                }
                            }
                            HorizontalDivider()
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
                                val inmuebleRecuperar = inmuebleEditando

                                inmuebleDao.eliminarInmueble(id)

                                firestore.collection("inmuebles")
                                    .whereEqualTo("idUsuario", idUsuarioSesionActual)
                                    .whereEqualTo("titulo", inmuebleEditando!!.titulo)
                                    .get()
                                    .addOnSuccessListener { result ->
                                        result.documents.forEach { it.reference.delete() }
                                    }

                                listaInmuebles =
                                    inmuebleDao.getInmueblesDeUsuario(idUsuarioSesionActual)

                                Toast.makeText(context, "Inmueble eliminado", Toast.LENGTH_SHORT)
                                    .show()

                                showDialogEliminar = false
                                inmuebleEditando = null
                                val result = snackbarHostState
                                    .showSnackbar(
                                        message = "¿Deseas deshacer la eliminación del Inmueble?",
                                        actionLabel = "Deshacer",
                                        // Defaults to SnackbarDuration.Short
                                        duration = SnackbarDuration.Long
                                    )
                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        inmuebleRecuperar?.let {
                                            inmuebleDao.nuevoInmueble(it)
                                        }

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
                                                context,
                                                "Inmueble subido a Firebase",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }.addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Error Firebase: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        listaInmuebles =
                                            inmuebleDao.getInmueblesDeUsuario(idUsuarioSesionActual)
                                        showSnackBar = false
                                    }

                                    SnackbarResult.Dismissed -> {
                                        showSnackBar = false
                                    }
                                }
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
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState()) // ← añadir esto
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    titulo,
                                    { titulo = it },
                                    label = { Text("Título") })
                                OutlinedTextField(
                                    descripcion,
                                    { descripcion = it },
                                    label = { Text("Descripción") })
                                OutlinedTextField(
                                    urlImagen,
                                    { urlImagen = it },
                                    label = { Text("URL Imagen") })
                                OutlinedTextField(
                                    precio,
                                    { precio = it },
                                    label = { Text("Precio (€)") })
                                OutlinedTextField(
                                    tipo,
                                    { tipo = it },
                                    label = { Text("Tipo (Alquiler/Venta)") })
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        when {
                                            titulo.isBlank() -> showToast(
                                                context,
                                                "El titulo no puede estar vacío"
                                            )

                                            descripcion.isBlank() -> showToast(
                                                context,
                                                "La descripción no puede estar vacía"
                                            )

                                            precio.isBlank() -> showToast(
                                                context,
                                                "El precio no puede estar vacío"
                                            )

                                            tipo.isBlank() -> showToast(
                                                context,
                                                "El tipo no puede estar vacío"
                                            )

                                            else -> {

                                                val inmueble = InmueblesData(
                                                    idInmueble = 0, // Room lo autogenera
                                                    idUsuario = idUsuarioSesionActual,      // idUsuario como cadena
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
                                                    val docRef =
                                                        firestore.collection("inmuebles").document()
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
                                                            context,
                                                            "Inmueble subido a Firebase",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }.addOnFailureListener { e ->
                                                        Toast.makeText(
                                                            context,
                                                            "Error Firebase: ${e.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                } else {
                                                    // Actualizar Room
                                                    inmuebleDao.actualizaInmueble(inmueble)

                                                    // Actualizar Firebase con el mismo documento
                                                    firestore.collection("inmuebles")
                                                        .whereEqualTo(
                                                            "idUsuario",
                                                            idUsuarioSesionActual
                                                        )
                                                        .whereEqualTo(
                                                            "titulo",
                                                            inmuebleEditando!!.titulo
                                                        )
                                                        .get()
                                                        .addOnSuccessListener { result ->
                                                            result.documents.forEach { doc ->
                                                                doc.reference.update(
                                                                    mapOf(
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
                                                        }.addOnFailureListener { e ->
                                                            Toast.makeText(
                                                                context,
                                                                "Error Firebase: ${e.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }

                                                // Recargar lista usando el idUsuario como cadena
                                                listaInmuebles =
                                                    inmuebleDao.getInmueblesDeUsuario(
                                                        idUsuarioSesionActual
                                                    )
                                                showDialog = false
                                            }
                                        }
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
        }
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

            val selected = navController.currentDestination?.route == screen.route

            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(screen.route) },
                icon = { Icon(icons[index], contentDescription = labels[index]) },
                label = { Text(labels[index]) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onTertiary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.tertiary, // color de fondo

                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
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
        })
}

// Para Tarjeta (1)
@Composable
fun TarjetaInmuebles(
    titulo: String, descripcion: String, urlImagen: String, precio: Double, tipo: String
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.size(width = 240.dp, height = 100.dp)
    ) {
        Row() {
            // URL de la imagen
//        Icon()
            Column() {
                // Titulo del inmueble
                Text(
                    text = titulo,
                    modifier = Modifier.padding(18.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                // "Descripción del inmueble"
                Text(
                    text = descripcion,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                // Precio del inmueble
                Text(
                    text = precio.toString(),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                // Tipo del inmueble
                Text(
                    text = tipo,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// Para Carrusel (2)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarruselInmuebles(
    inmuebles: List<InmueblesData>
) {
    if (inmuebles.isEmpty()) {
        Text(
            text = "No has subido inmuebles todavía",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { inmuebles.size },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 16.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->

        val inmueble = inmuebles[i]

        AsyncImage(
            model = inmueble.urlImagen,
            contentDescription = inmueble.descripcion,
            modifier = Modifier
                .height(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            contentScale = ContentScale.Crop
        )
    }
}

// Para el Menú Desplegable (4)
@Composable
fun MenuDesplegableOpcionesInmuebles(
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        // Icono de tres puntos
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opciones"
            )
        }

        // Menú desplegable
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Editar") },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                onClick = {
                    expanded = false
                    onEditarClick()
                }
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("Eliminar") },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = {
                    expanded = false
                    onEliminarClick()
                }
            )
        }
    }
}