package com.example.contador.screens

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
import com.example.contador.R
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.InmueblesData
import com.example.contador.localdb.ProductosData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.example.contador.notification.NotificationHandler
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
        ).allowMainThreadQueries().build()
    }

    val sesionDao = db.sesionDao()
    val usuarioDao = db.usuarioDao()
    val productoDao = db.productosDao()

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

    // Cargar sesión y lista de inmuebles
    LaunchedEffect(Unit) {
        val sesion = sesionDao.getUsuarioSesionActual()
        idUsuarioSesionActual = sesion?.idUsuario ?: ""
        usuarioSesion = sesion?.let { usuarioDao.getUsuarioPorId(it.idUsuario) }

        firestore.collection("productos")
            .whereEqualTo("idUsuario", idUsuarioSesionActual) // solo del usuario actual
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { producto ->
                    try {
                        ProductosData(
                            idProducto = producto.id.hashCode(), // usamos el ID de Firebase directamente
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
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error cargando productos", Toast.LENGTH_SHORT).show()
            }
    }

    // UI
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
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
                            Icons.Default.Home,
                            contentDescription = "Regresar a mi perfil"
                        )
                    }

                    // Cerrar sesión
                    IconButton(onClick = {
                        scope.launch {
                            if (idUsuarioSesionActual.isNotEmpty()) {
                                sesionDao.eliminarSesionUsuario(idUsuarioSesionActual)
                            }
                            navController.navigate(AppScreens.Inicio.route) {
                                popUpTo(0) { inclusive = true }
                                Toast.makeText(context, "Cerrando sesión", Toast.LENGTH_SHORT)
                                    .show()
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
        // Lista de productos
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
//            Text(
//                text = "Galería de imágenes de tus inmuebles",
//                modifier = Modifier.padding(8.dp),
//                fontWeight = FontWeight.Bold
//            )
//
//            // Carrusel de imágenes de los inmuebles del usuario
//            CarruselInmuebles(
//                inmuebles = listaInmuebles
//            )

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
                                    Row() {
                                        val precioConDescuento =
                                            producto.precio * (1 - producto.porcentajePromocion / 100)
                                        if (producto.porcentajePromocion > 0) {
                                            Text("Precio: $precioConDescuento €    ", color = Color.Blue)
                                            Text(
                                                "Descuento: ${producto.porcentajePromocion}%",
                                                fontSize = 12.sp,
                                                color = Color.Red,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        } else {
                                            Text("Precio: ${producto.precio} €", color = Color.Blue)
                                        }
                                    }
                                }

                                if (producto.idUsuario == idUsuarioSesionActual) {
                                    MenuDesplegableOpcionesInmuebles(
                                        onEditarClick = {
                                            productoEditando = producto
                                            nombre = producto.nombre
                                            descripcion = producto.descripcion
                                            urlImagen = producto.urlImagen
                                            precio = producto.precio.toString()
                                            porcentajePromocion =
                                                producto.porcentajePromocion.toString()
                                            showDialog = true
                                        },
                                        onEliminarClick = {
                                            productoEditando = producto
                                            showDialogEliminar = true
                                        }
                                    )
                                }
                            }
                            HorizontalDivider()
                        }

                    }
                }
            }

            // Para la eliminación del producto
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

                            firestore.collection("productos").whereEqualTo("idProductoRoom", id)
                                .get()
                                .addOnSuccessListener { result ->
                                    result.documents.forEach { it.reference.delete() }
                                }

                            listaProductos =
                                productoDao.getProductosDeUsuario(idUsuarioSesionActual)

                            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT)
                                .show()

                            showDialogEliminar = false
                            productoEditando = null
                            val result = snackbarHostState
                                .showSnackbar(
                                    message = "¿Deseas deshacer la eliminación del producto?",
                                    actionLabel = "Deshacer",
                                    // Defaults to SnackbarDuration.Short
                                    duration = SnackbarDuration.Long
                                )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    productoRecuperar?.let {
                                        productoDao.nuevoProducto(it)
                                    }

                                    // Crear documento con ID único en Firebase
                                    val docRef = firestore.collection("productos").document()
                                    val dataFirebase = mapOf(
                                        "idProducto" to docRef.id,
                                        "idUsuario" to idUsuarioSesionActual,
                                        "nombre" to nombre,
                                        "descripcion" to descripcion,
                                        "urlImagen" to urlImagen,
                                        "precio" to precio.toDoubleOrNull(),
                                        "porcentajePromocion" to porcentajePromocion.toDoubleOrNull()
                                    )

                                    docRef.set(dataFirebase).addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Producto subido a Firebase",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Error Firebase: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    listaProductos =
                                        productoDao.getProductosDeUsuario(idUsuarioSesionActual)
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

            // Dialogo para añadir / editar inmueble
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(if (productoEditando == null) "Nuevo Producto" else "Editar Producto") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        TextButton(
                            onClick = {
                                scope.launch {

                                    val producto = ProductosData(
                                        idProducto = 0, // Room lo autogenera
                                        idUsuario = idUsuarioSesionActual,      // idUsuario como cadena
                                        nombre = nombre,
                                        descripcion = descripcion,
                                        urlImagen = urlImagen,
                                        precio = precio.toDoubleOrNull() ?: 0.0,
                                        porcentajePromocion = porcentajePromocion.toDoubleOrNull()
                                            ?: 0.0
                                    )

                                    // Cuando se sube el producto
                                    if (productoEditando == null) {
                                        // Guardar en Room
                                        productoDao.nuevoProducto(producto)

                                        // Crear documento con ID único en Firebase
                                        val docRef =
                                            firestore.collection("productos").document()
                                        val dataFirebase = mapOf(
                                            "idProducto" to docRef.id,
                                            "idUsuario" to idUsuarioSesionActual,
                                            "nombre" to nombre,
                                            "descripcion" to descripcion,
                                            "urlImagen" to urlImagen,
                                            "precio" to precio.toDoubleOrNull(),
                                            "porcentajePromocion" to porcentajePromocion.toDoubleOrNull()
                                        )

                                        docRef.set(dataFirebase).addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Producto subido a Firebase",
                                                Toast.LENGTH_SHORT
                                            ).show()

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
                                        listaProductos =
                                            productoDao.getProductosDeUsuario(idUsuarioSesionActual)
                                        // Cuando se actualiza el producto
                                    } else {
                                        // Actualizar Room
                                        productoDao.actualizaProducto(producto)
//                                        val productoRecuperar = productoEditando

                                        // Actualizar Firebase con el mismo documento
                                        firestore.collection("productos").whereEqualTo(
                                            "idProductoRoom", productoEditando!!.idProducto
                                        ).get().addOnSuccessListener { result ->
                                            result.documents.forEach { doc ->
                                                doc.reference.set(
                                                    mapOf(
                                                        "idProductoRoom" to productoEditando!!.idProducto,
                                                        "idUsuario" to idUsuarioSesionActual,
                                                        "nombre" to nombre,
                                                        "descripcion" to descripcion,
                                                        "urlImagen" to urlImagen,
                                                        "precio" to precio.toDoubleOrNull(),
                                                        "porcentajePromocion" to porcentajePromocion.toDoubleOrNull()
                                                    )
                                                )
                                            }
                                            Toast.makeText(
                                                context,
                                                "Producto actualizado en Firebase",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            showDialog = false
                                            productoEditando = null



                                        }.addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Error Firebase: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

//                                        // Para evitar la actualización del producto
//                                        val result = snackbarHostState
//                                            .showSnackbar(
//                                                message = "¿Deseas deshacer la edición del producto?",
//                                                actionLabel = "Deshacer",
//                                                // Defaults to SnackbarDuration.Short
//                                                duration = SnackbarDuration.Long
//                                            )
//                                        when (result) {
//                                            SnackbarResult.ActionPerformed -> {
//                                                productoRecuperar?.let {
//                                                    productoDao.actualizaProducto(it)
//                                                }
//
//                                                // Actualizar Firebase con el mismo documento
//                                                firestore.collection("productos").whereEqualTo(
//                                                    "idProductoRoom", productoRecuperar!!.idProducto
//                                                ).get().addOnSuccessListener { result ->
//                                                    result.documents.forEach { doc ->
//                                                        doc.reference.set(
//                                                            mapOf(
//                                                                "idProductoRoom" to productoRecuperar.idProducto,
//                                                                "idUsuario" to idUsuarioSesionActual,
//                                                                "nombre" to nombre,
//                                                                "descripcion" to descripcion,
//                                                                "urlImagen" to urlImagen,
//                                                                "precio" to precio.toDoubleOrNull(),
//                                                                "porcentajePromocion" to porcentajePromocion.toDoubleOrNull()
//                                                            )
//                                                        )
//                                                    }
//                                                    Toast.makeText(
//                                                        context,
//                                                        "Inmueble actualizado en Firebase",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }.addOnFailureListener { e ->
//                                                    Toast.makeText(
//                                                        context,
//                                                        "Error Firebase: ${e.message}",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                                listaProductos =
//                                                    productoDao.getProductosDeUsuario(
//                                                        idUsuarioSesionActual
//                                                    )
//                                                showSnackBar = false
//                                            }
//
//                                            SnackbarResult.Dismissed -> {
//                                                showSnackBar = false
//                                            }
//                                        }
                                    }
                                    showDialog = false
                                    listaProductos =
                                        productoDao.getProductosDeUsuario(idUsuarioSesionActual)
                                }
//                            }
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

// Para Barra de navegación inferior (5)
@Composable
fun BottomBarProductos(navController: NavHostController) {
    val items = listOf(AppScreens.MenuPrincipal, AppScreens.MisInmuebles, AppScreens.Productos)
    val labels = listOf("Perfil", "Inmuebles", "Productos")
    val icons = listOf(Icons.Default.Home, Lucide.House, Lucide.BadgeEuro)

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
        }
    )
}

@Composable
fun SwitchMinimalExample(isPromocion: Boolean, function: () -> Unit) {
    Switch(
        checked = isPromocion,
        onCheckedChange = { function() }
    )
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CarruselTarjetasProductos(productos: List<ProductosData>) {
//    data class CarouselItem(
//        val id: Int,
//        @DrawableRes val imageResId: Int,
//        val contentDescription: String
//    )
//
//    val tarjetas = listOf(productos)
//        OutlinedCard(
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.surface,
//            ),
//            border = BorderStroke(1.dp, Color.Black),
//            modifier = Modifier.size(width = 240.dp, height = 100.dp)
//        ) {
//            Row() {
//                // URL de la imagen
//                AsyncImage(
//                    model = tarjetas.urlImagen,
//                    contentDescription = producto.descripcion,
//                    modifier = Modifier.size(80.dp)
//                )
//                Column() {
//                    // Nombre del producto
//                    Text(
//                        text = nombre,
//                        modifier = Modifier.padding(18.dp),
//                        textAlign = TextAlign.Center,
//                        fontWeight = FontWeight.Bold
//                    )
//                    // "Descripción del inmueble"
//                    Text(
//                        text = descripcion,
//                        modifier = Modifier.padding(16.dp),
//                        textAlign = TextAlign.Center,
//                    )
//                    // Precio del inmueble
//                    Text(
//                        text = precio.toString(),
//                        modifier = Modifier.padding(16.dp),
//                        textAlign = TextAlign.Center,
//                    )
//                    // Tipo del inmueble
//                    Text(
//                        text = tipo,
//                        modifier = Modifier.padding(16.dp),
//                        textAlign = TextAlign.Center,
//                    )
//                }
//            }
//        }
//    )
//
//    HorizontalMultiBrowseCarousel(
//        state = rememberCarouselState { tarjetas.count() },
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//            .padding(top = 16.dp, bottom = 16.dp),
//        preferredItemWidth = 186.dp,
//        itemSpacing = 8.dp,
//        contentPadding = PaddingValues(horizontal = 16.dp)
//    ) { i ->
//        val item = items[i]
//    }
//}