package com.example.contador.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.AsyncImage
import com.composables.icons.lucide.BadgeEuro
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Users
import com.example.contador.R
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.InmueblesData
import com.example.contador.localdb.ProductosData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.example.contador.notification.NotificationHandler
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Productos(navController: NavController) {
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
    val productoDao = db.productosDao()

    // Para ir a la web
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))

    // Estados
    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }
    var listaProductos by remember { mutableStateOf<List<ProductosData>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogEliminar by remember { mutableStateOf(false) }
    var productoEditando by remember { mutableStateOf<ProductosData?>(null) }

    var idUsuarioSesionActual by remember { mutableStateOf("") }

    // Campos para formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var urlImagen by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var showSnackBar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var isPromocion by remember { mutableStateOf(false) }
    var porcentajePromocion by remember { mutableStateOf("") }

    val notificationHandler = NotificationHandler(context)

    // Cargar sesión y lista de productos
    val uid = Firebase.auth.currentUser?.uid ?: ""

    // FIX 1: Helper para recargar productos desde Firestore (fuente de verdad)
    fun recargarProductosDesdeFirestore() {
        firestore.collection("productos").whereEqualTo("idUsuario", uid).get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { producto ->
                    try {
                        ProductosData(
                            idProducto = producto.id.hashCode(),
                            idUsuario = producto.getString("idUsuario") ?: "",
                            nombre = producto.getString("nombre") ?: "",
                            descripcion = producto.getString("descripcion") ?: "",
                            urlImagen = producto.getString("urlImagen") ?: "",
                            precio = producto.getDouble("precio") ?: 0.0,
                            porcentajePromocion = producto.getDouble("porcentajePromocion") ?: 0.0
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                listaProductos = lista
            }.addOnFailureListener {
                Toast.makeText(context, "Error cargando productos", Toast.LENGTH_SHORT).show()
            }
    }

    LaunchedEffect(Unit) {
        idUsuarioSesionActual = uid
        usuarioSesion = usuarioDao.getUsuarioPorId(uid)
        recargarProductosDesdeFirestore()
    }

    // UI
    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }, topBar = {
        TopAppBar(
            title = { Text("Mis Productos", fontSize = 20.sp) }, colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White
            ), navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
            }, actions = {

                // Regresar a mi perfil
                IconButton(onClick = {
                    navController.navigate(AppScreens.MenuPrincipal.route)
                }) {
                    Icon(
                        Icons.Default.Home, contentDescription = "Regresar a mi perfil"
                    )
                }

                // Cerrar sesión
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
            })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                productoEditando = null
                nombre = ""
                descripcion = ""
                urlImagen = ""
                precio = ""
                porcentajePromocion = ""
                isPromocion = false
                showDialog = true
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir producto")
        }
    }, bottomBar = { BottomBarProductos(navController as NavHostController) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                text = "\nTus productos",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (listaProductos.isEmpty()) {
                    item {
                        Text(
                            text = "No has subido productos todavía",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(listaProductos) { producto ->
                        if (producto.nombre.isNotEmpty() && producto.descripcion.isNotEmpty() && producto.urlImagen.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Imagen del producto
                                AsyncImage(
                                    model = producto.urlImagen,
                                    contentDescription = producto.descripcion,
                                    modifier = Modifier.size(80.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .weight(1f)
                                ) {
                                    Text(producto.nombre, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Descripción: ${producto.descripcion}",
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    // FIX 3: Formatear precio a 2 decimales para evitar errores de punto flotante
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (producto.porcentajePromocion > 0) {
                                            val precioConDescuento = "%.2f".format(
                                                producto.precio * (1 - producto.porcentajePromocion / 100)
                                            )
                                            val precioOriginal = "%.2f".format(producto.precio)
                                            Text(
                                                text = "$precioConDescuento €",
                                                color = Color.Blue,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "  $precioOriginal €",
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                            Text(
                                                text = "  -${producto.porcentajePromocion.toInt()}%",
                                                fontSize = 12.sp,
                                                color = Color.Red,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "${"%.2f".format(producto.precio)} €",
                                                color = Color.Blue
                                            )
                                        }
                                    }
                                }

                                if (producto.idUsuario == idUsuarioSesionActual) {
                                    MenuDesplegableOpcionesInmuebles(onEditarClick = {
                                        productoEditando = producto
                                        nombre = producto.nombre
                                        descripcion = producto.descripcion
                                        urlImagen = producto.urlImagen
                                        precio = producto.precio.toString()
                                        porcentajePromocion =
                                            producto.porcentajePromocion.toString()
                                        isPromocion = producto.porcentajePromocion > 0
                                        showDialog = true
                                    }, onEliminarClick = {
                                        productoEditando = producto
                                        showDialogEliminar = true
                                    })
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }

            // Diálogo para confirmación de eliminación
            if (showDialogEliminar && productoEditando != null) {
                DialogProducto(
                    onDismiss = {
                        showDialogEliminar = false
                        productoEditando = null
                    }, onConfirm = {
                        scope.launch {
                            val id = productoEditando!!.idProducto
                            val productoRecuperar = productoEditando

                            productoDao.eliminarProducto(id)

                            firestore.collection("productos")
                                .whereEqualTo("idUsuario", idUsuarioSesionActual)
                                .whereEqualTo("nombre", productoEditando!!.nombre).get()
                                .addOnSuccessListener { result ->
                                    result.documents.forEach { it.reference.delete() }
                                }

                            // FIX 1: Actualizar lista en memoria en vez de leer de Room
                            listaProductos = listaProductos.filter {
                                it.idProducto != id
                            }

                            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()

                            showDialogEliminar = false
                            productoEditando = null

                            val result = snackbarHostState.showSnackbar(
                                message = "¿Deseas deshacer la eliminación del producto?",
                                actionLabel = "Deshacer",
                                duration = SnackbarDuration.Long
                            )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    productoRecuperar?.let {
                                        productoDao.nuevoProducto(it)
                                    }

                                    val docRef = firestore.collection("productos").document()
                                    val dataFirebase = mapOf(
                                        "idProducto" to docRef.id,
                                        "idUsuario" to idUsuarioSesionActual,
                                        "nombre" to (productoRecuperar?.nombre ?: ""),
                                        "descripcion" to (productoRecuperar?.descripcion ?: ""),
                                        "urlImagen" to (productoRecuperar?.urlImagen ?: ""),
                                        "precio" to productoRecuperar?.precio,
                                        "porcentajePromocion" to productoRecuperar?.porcentajePromocion
                                    )

                                    docRef.set(dataFirebase).addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Producto restaurado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // FIX 1: Recargar desde Firestore para asegurar consistencia
                                        recargarProductosDesdeFirestore()
                                    }.addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Error Firebase: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    showSnackBar = false
                                }

                                SnackbarResult.Dismissed -> {
                                    showSnackBar = false
                                }
                            }
                        }
                    }, nombre = productoEditando!!.nombre
                )
            }

            // Diálogo para añadir / editar producto
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(if (productoEditando == null) "Nuevo Producto" else "Editar Producto") },
                    text = {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                nombre,
                                { nombre = it },
                                label = { Text("Nombre del producto") })
                            OutlinedTextField(
                                descripcion,
                                { descripcion = it },
                                label = { Text("Descripción del producto") })
                            OutlinedTextField(
                                urlImagen,
                                { urlImagen = it },
                                label = { Text("Imagen en URL") })
                            OutlinedTextField(
                                precio,
                                { precio = it },
                                label = { Text("Precio (€)") })
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("¿Artículo en promoción?: ")
                                SwitchMinimalExample(isPromocion, { isPromocion = !isPromocion })
                            }

                            if (isPromocion) {
                                OutlinedTextField(
                                    porcentajePromocion,
                                    { porcentajePromocion = it },
                                    label = { Text("Descuento (%)") })
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                when {
                                    nombre.isBlank() -> showToast(
                                        context, "El nombre no puede estar vacío"
                                    )

                                    descripcion.isBlank() -> showToast(
                                        context, "La descripción no puede estar vacía"
                                    )

                                    precio.isBlank() -> showToast(
                                        context, "El precio no puede estar vacío"
                                    )

                                    precio.toDoubleOrNull() == null -> showToast(
                                        context, "El precio debe ser un número válido"
                                    )

                                    isPromocion && porcentajePromocion.isBlank() -> showToast(
                                        context, "Introduce el porcentaje de descuento"
                                    )

                                    isPromocion && (porcentajePromocion.toDoubleOrNull() == null || porcentajePromocion.toDouble() !in 1.0..99.0) -> showToast(
                                        context, "El descuento debe estar entre 1 y 99"
                                    )

                                    else -> {
                                        val porcentajeFinal = if (isPromocion)
                                            porcentajePromocion.toDoubleOrNull() ?: 0.0
                                        else 0.0

                                        val productoNuevo = ProductosData(
                                            idProducto = productoEditando?.idProducto ?: 0,
                                            idUsuario = idUsuarioSesionActual,
                                            nombre = nombre,
                                            descripcion = descripcion,
                                            urlImagen = urlImagen,
                                            precio = precio.toDoubleOrNull() ?: 0.0,
                                            porcentajePromocion = porcentajeFinal
                                        )

                                        // --- NUEVO PRODUCTO ---
                                        if (productoEditando == null) {
                                            productoDao.nuevoProducto(productoNuevo)

                                            val docRef =
                                                firestore.collection("productos").document()
                                            val dataFirebase = mapOf(
                                                "idProducto" to docRef.id,
                                                "idUsuario" to idUsuarioSesionActual,
                                                "nombre" to nombre,
                                                "descripcion" to descripcion,
                                                "urlImagen" to urlImagen,
                                                "precio" to precio.toDoubleOrNull(),
                                                "porcentajePromocion" to porcentajeFinal
                                            )

                                            docRef.set(dataFirebase).addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Producto subido a Firebase",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                // FIX 1: Recargar desde Firestore
                                                recargarProductosDesdeFirestore()
                                            }.addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    "Error Firebase: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            notificationHandler.enviarNotificacionConDestino(
                                                "Acabas de subir un nuevo producto",
                                                "Haz clic para ir a la ventana de Productos",
                                                "Productos"
                                            )
                                            showDialog = false

                                            // --- EDITAR PRODUCTO ---
                                        } else {
                                            // FIX 2: Guardar estado anterior para poder deshacer
                                            val productoAnterior = productoEditando!!
                                            val nombreAnterior = productoAnterior.nombre

                                            firestore.collection("productos")
                                                .whereEqualTo("idUsuario", idUsuarioSesionActual)
                                                .whereEqualTo("nombre", nombreAnterior)
                                                .get()
                                                .addOnSuccessListener { result ->
                                                    result.documents.forEach { doc ->
                                                        doc.reference.update(
                                                            mapOf(
                                                                "idUsuario" to idUsuarioSesionActual,
                                                                "nombre" to nombre,
                                                                "descripcion" to descripcion,
                                                                "urlImagen" to urlImagen,
                                                                "precio" to precio.toDoubleOrNull(),
                                                                "porcentajePromocion" to porcentajeFinal
                                                            )
                                                        )
                                                    }

                                                    // FIX 1: Actualizar lista en memoria directamente
                                                    listaProductos = listaProductos.map {
                                                        if (it.idProducto == productoAnterior.idProducto) productoNuevo else it
                                                    }

                                                    showDialog = false
                                                    productoEditando = null

                                                    // FIX 2: Mostrar snackbar con opción de deshacer edición
                                                    scope.launch {
                                                        val snackResult = snackbarHostState.showSnackbar(
                                                            message = "Producto actualizado",
                                                            actionLabel = "Deshacer",
                                                            duration = SnackbarDuration.Long
                                                        )
                                                        when (snackResult) {
                                                            SnackbarResult.ActionPerformed -> {
                                                                // Revertir en Firebase buscando por el nombre NUEVO
                                                                firestore.collection("productos")
                                                                    .whereEqualTo("idUsuario", idUsuarioSesionActual)
                                                                    .whereEqualTo("nombre", nombre)
                                                                    .get()
                                                                    .addOnSuccessListener { revertResult ->
                                                                        revertResult.documents.forEach { doc ->
                                                                            doc.reference.update(
                                                                                mapOf(
                                                                                    "nombre" to productoAnterior.nombre,
                                                                                    "descripcion" to productoAnterior.descripcion,
                                                                                    "urlImagen" to productoAnterior.urlImagen,
                                                                                    "precio" to productoAnterior.precio,
                                                                                    "porcentajePromocion" to productoAnterior.porcentajePromocion
                                                                                )
                                                                            )
                                                                        }
                                                                        // FIX 1: Revertir lista en memoria
                                                                        listaProductos = listaProductos.map {
                                                                            if (it.idProducto == productoAnterior.idProducto) productoAnterior else it
                                                                        }
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Edición deshecha",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                            }
                                                            SnackbarResult.Dismissed -> { /* No action needed */ }
                                                        }
                                                    }

                                                }.addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        context,
                                                        "Error Firebase: ${e.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
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
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    })
            }
        }
    }
}

// Para Barra de navegación inferior
@Composable
fun BottomBarProductos(navController: NavHostController) {
    val items = listOf(AppScreens.MenuPrincipal, AppScreens.MisInmuebles, AppScreens.Productos, AppScreens.RegistroContactos)
    val labels = listOf("Perfil", "Inmuebles", "Productos", "Contactos")
    val icons = listOf(Icons.Default.Home, Lucide.House, Lucide.BadgeEuro, Lucide.Users)

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
                    indicatorColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// Cuadro de diálogo para confirmar eliminación
@Composable
fun DialogProducto(
    onDismiss: () -> Unit, onConfirm: () -> Unit, nombre: String
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Icono de ejemplo")
        },
        title = { Text("Confirmar la eliminación del producto") },
        text = { Text("¿Desea eliminar el producto?: $nombre") },
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

@Composable
fun SwitchMinimalExample(isPromocion: Boolean, function: () -> Unit) {
    Switch(
        checked = isPromocion, onCheckedChange = { function() })
}