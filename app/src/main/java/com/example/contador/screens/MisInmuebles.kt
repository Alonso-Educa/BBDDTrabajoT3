//package com.example.contador.screens
//
//import android.R.attr.onClick
//import android.content.ClipData
//import android.media.RouteListingPreference
//import android.widget.Toast
//import androidx.compose.animation.animateContentSize
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.Logout
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.BottomAppBar
//import androidx.compose.material3.BottomAppBarDefaults
//import androidx.compose.material3.Button
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.FloatingActionButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedSecureTextField
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import androidx.room.Room
//import com.example.contador.localdb.AppDB
//import com.example.contador.localdb.Estructura
//import com.example.contador.localdb.UsuarioData
//import com.google.firebase.firestore.FirebaseFirestore
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.text.style.TextOverflow
//import coil.compose.AsyncImage
//import com.composables.icons.lucide.CircleUserRound
//import com.composables.icons.lucide.HousePlus
//import com.example.contador.navigation.AppScreens
//import kotlinx.coroutines.launch
//import com.composables.icons.lucide.Lucide
//import com.composables.icons.lucide.X
//import com.example.contador.localdb.InmueblesDao
//import com.example.contador.localdb.InmueblesData
//import com.example.contador.localdb.UsuarioDao
//import kotlin.text.set
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MisInmuebles(navController: NavController) {
//
//    val context = LocalContext.current
//
//    val db = remember {
//        Room.databaseBuilder(
//            context.applicationContext,
//            AppDB::class.java,
//            Estructura.DB.NAME
//        ).allowMainThreadQueries().build()
//    }
//
//    val scope = rememberCoroutineScope()
//    val firestore = FirebaseFirestore.getInstance()
//
//    // ---------------- ESTADOS ----------------
//
//    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }
//    var listaInmuebles by remember { mutableStateOf<List<InmueblesData>>(emptyList()) }
//
//    var showDialog by remember { mutableStateOf(false) }
//
//    var usuarioEditando by remember { mutableStateOf<InmueblesData?>(null) }
//
//    var idInmuebl by remember { mutableStateOf(0) }
//    var titulo by remember { mutableStateOf("") }
//    var descripcion by remember { mutableStateOf("") }
//    var urlImagen by remember { mutableStateOf("") }
//    var precio by remember { mutableStateOf("") }
//    var tipo by remember { mutableStateOf("Alquiler") }
//
//    val dbLocal = remember {
//        Room.databaseBuilder(context, AppDB::class.java, Estructura.DB.NAME)
//            .allowMainThreadQueries().build()
//    }
//    val dbfire = FirebaseFirestore.getInstance()
//    val usuarioDao = dbLocal.usuarioDao()
//    val sesionDao = dbLocal.sesionDao()
//    val inmuebleDao = dbLocal.inmueblesDao()
//    var estaActivo by remember { mutableStateOf(false) }
//
//
//    // ---------------- CARGA DE DATOS ----------------
//
//    LaunchedEffect(Unit) {
//        usuarioSesion = db.sesionDao()
//            .getUsuarioSesionActual()
//    }
//    var idUsuarioSesionActual by remember {
//        mutableIntStateOf(0)
//    }
//    LaunchedEffect(Unit) {
//        idUsuarioSesionActual = db.sesionDao()
//            .getUsuarioSesionActual()
//            ?.idUsuario ?: 0
//    }
//
//    LaunchedEffect(Unit) {
//        usuarioSesion = db.sesionDao().getUsuarioSesionActual()
//    }
//
//
//    // ---------------- UI ----------------
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Mis Inmuebles") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, null)
//                    }
//                },
//
//                actions = {
//                    //Iconos de la barra superior derecha
//                    IconButton(
//                        onClick = {
//                            scope.launch {
//                                navController.navigate(AppScreens.Resultados.route) {
//                                    popUpTo(0) { inclusive = true }
//                                    Toast.makeText(context, "Ir a mi perfil", Toast.LENGTH_SHORT)
//                                        .show()
//                                }
//                            }
//                        }) {
//                        Icon(
//                            imageVector = Lucide.CircleUserRound,
//                            contentDescription = "Ir a mi perfil"
//                        )
//                    }
//                    IconButton(
//                        onClick = {
//                            scope.launch {
//                                navController.navigate(AppScreens.MisInmuebles.route) {
//                                    popUpTo(0) { inclusive = true }
//                                    Toast.makeText(
//                                        context,
//                                        "Ir a mis inmuebles",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        }) {
//                        Icon(
//                            imageVector = Lucide.HousePlus,
//                            contentDescription = "Ir a mis inmuebles"
//                        )
//                    }
//                    IconButton(
//                        onClick = {
//                            scope.launch {
//                                if (idUsuarioSesionActual != 0) {
//                                    db.sesionDao().eliminarSesionUsuario(idUsuarioSesionActual)
//                                }
//
//                                navController.navigate(AppScreens.Inicio.route) {
//                                    popUpTo(0) { inclusive = true }
//                                    Toast.makeText(context, "Cerrando sesión", Toast.LENGTH_SHORT)
//                                        .show()
//                                }
//                            }
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Logout,
//                            contentDescription = "Cerrar sesión"
//                        )
//                    }
//
//                    usuarioSesion?.let {
//                        val inicial = buildString {
//                            append(it.nombreUsuario.first().uppercase())
//                        }
//                        Box(
//                            modifier = Modifier
//                                .size(40.dp)
//                                .background(
//                                    MaterialTheme.colorScheme.primaryContainer,
//                                    CircleShape
//                                )
//                                .clickable { /*TODO*/ },
//
//                            ) {
//                            Text(
//                                modifier = Modifier.align(Alignment.Center),
//                                text = inicial,
//                                style = MaterialTheme.typography.titleMedium,
//
//                                )
//                        }
//                    }
//                },
//                colors = topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = Color.White
//                )
//            )
//        },
//        bottomBar = {
//            BottomAppBar(
//                actions = {},
//                floatingActionButton = {
//                    FloatingActionButton(
//                        onClick = {
//                            AlertDialog(
//                                onDismissRequest = { showDialog = false },
//                                title = { Text("Editar inmueble") },
//                                text = {
//                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//
//                                        OutlinedTextField(
//                                            titulo,
//                                            { titulo = it },
//                                            label = { Text("Titulo del inmueble") })
//                                        OutlinedTextField(
//                                            descripcion,
//                                            { descripcion = it },
//                                            label = { Text("Descripción del inmueble") })
//                                        OutlinedTextField(
//                                            urlImagen,
//                                            { urlImagen = it },
//                                            label = { Text("Imagen en URL") })
//                                        OutlinedTextField(
//                                            precio,
//                                            { precio = it },
//                                            label = { Text("Precio (€)") })
//                                        OutlinedTextField(
//                                            tipo,
//                                            { tipo = it },
//                                            label = { Text("Tipo de contrato") })
//
//                                    }
//                                },
//                                confirmButton = {
//                                    TextButton(onClick = {
//                                        scope.launch {
//
////                                            val inmuebleExistente = inmuebleDao.getInmueble(id)
//
//                                            when {
//                                                titulo.isBlank() -> showToast(
//                                                    context,
//                                                    "El titulo no puede estar vacío"
//                                                )
//
//                                                descripcion.isBlank() -> showToast(
//                                                    context, "La descripción no puede estar vacía"
//                                                )
//
//                                                urlImagen.isBlank() -> showToast(
//                                                    context,
//                                                    "La url no puede estar vacía"
//                                                )
//
//                                                precio.isBlank() -> showToast(
//                                                    context,
//                                                    "El precio no puede estar vacío"
//                                                )
//
//                                                tipo.isBlank() -> showToast(
//                                                    context,
//                                                    "El tipo no puede estar vacío"
//                                                )
//
//
////                                                usuarioExistente != null -> showToast(
////                                                    context, "Ya existe un usuario con ese email"
////                                                )
//
//                                                else -> {
//                                                    val nuevoInmueble = InmueblesData(
//                                                        idInmueble = idInmuebl,
//                                                        idUsuario = idUsuarioSesionActual,
//                                                        titulo = titulo,
//                                                        descripcion = descripcion,
//                                                        urlImagen = urlImagen,
//                                                        precio = precio.toDouble(),
//                                                        tipo = tipo
//                                                    )
//                                                    inmuebleDao.nuevoInmueble(nuevoInmueble)
//
//                                                    val dataFirebase = mapOf(
//                                                        "id" to idInmuebl,
//                                                        "idUsuario" to idUsuarioSesionActual,
//                                                        "titulo" to titulo,
//                                                        "descripcion" to descripcion,
//                                                        "urlImagen" to urlImagen,
//                                                        "precio" to precio,
//                                                        "tipo" to tipo,
//                                                    )
//
//
//                                                    dbfire.collection("inmuebles")
//                                                        .document(idInmuebl.toString())
//                                                        .set(dataFirebase)
//                                                        .addOnSuccessListener { println("Guardado en Firebase") }
//                                                        .addOnFailureListener { e -> println("Error Firebase: ${e.message}") }
//                                                    showToast(
//                                                        context,
//                                                        "Inmueble registrado correctamente."
//
//                                                    )
//                                                    //Se incrementa el id para el siguiente inmueble
//
//                                                    idInmuebl++
//                                                }
//
//                                            }
//
//
////                        if (idUsuarioSesionActual != 0) {
////                            listaUsuarios = db.usuarioDao()
////                                .getListaUsuariosPorId(idUsuarioSesionActual)
////                        }
//
//                                            showDialog = false
//                                        }
//                                    }) {
//                                        Text("Guardar")
//                                    }
//                                },
//                                dismissButton = {
//                                    TextButton(onClick = { showDialog = false }) {
//                                        Text("Cancelar")
//                                    }
//                                }
//                            )
//                        },
//                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
//                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
//                    ) {
//                        Icon(Icons.Filled.Add, "Añadir Inmueble")
//                    }
//                }
//            )
//        }
////        snackbarHost = {
////            SnackbarHost(hostState = snackbarHostState)
////        },
////        floatingActionButton = {
////            ExtendedFloatingActionButton(
////                text = { Text("Show snackbar") },
////                icon = { Icon(Icons.Filled.Image, contentDescription = "") },
////                onClick = {
////                    scope.launch {
////                        val result = snackbarHostState
////                            .showSnackbar(
////                                message = "Snackbar",
////                                actionLabel = "Action",
////                                // Defaults to SnackbarDuration.Short
////                                duration = SnackbarDuration.Indefinite
////                            )
////                        when (result) {
////                            SnackbarResult.ActionPerformed -> {
////                                /* Handle snackbar action performed */
////                            }
////
////                            SnackbarResult.Dismissed -> {
////                                /* Handle snackbar dismissed */
////                            }
////                        }
////                    }
////                }
////            )
////        }
//    ) { innerPadding ->
//
//        // Screen content
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                usuarioSesion?.let {
//
//                    IconButton(onClick = {
//                        usuarioEditando = it
//                        titulo = it.tituloInmueble
//                        descripcion = it.descripcionInmueble
//                        urlImagen = it.urlImagen
//                        precio = it.precio
//                        tipo = it.tipo
//                        showDialog = true
//                    }) {
//                        Icon(Icons.Default.Edit, contentDescription = "Editar")
//                    }
//
//                    IconButton(onClick = {
//                        inmuebleDao.eliminarInmueble(it.getPrimaryKey())
//                    }) {
//                        Icon(Icons.Default.Edit, contentDescription = "Editar")
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            LazyColumn {
//                items(listaInmuebles.size) { inmueble ->
//
//                    AsyncImage(
//                        model = urlImagen,
//                        contentDescription = descripcion,
//                        modifier = Modifier.size(40.dp)
//                    )
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//
//
//                        Text(
//                            text = inmueble.tituloInmueble,
//                            modifier = Modifier.weight(1f)
//                        )
//
//                        Text(
//                            text = it.descripcion,
//                            maxLines = if (estaActivo) Int.MAX_VALUE else 3,
//                            overflow = TextOverflow.Ellipsis,
//                            modifier = Modifier
//                                .weight(2f)
//                                .animateContentSize()
//                                .clickable {
//                                    estaActivo = !estaActivo
//                                }
//                        )
//
//                        Text(
//                            text = it.precio,
//                            modifier = Modifier.weight(1f).color(Color.Blue)
//                        )
//
//                        Text(
//                            text = "Tipo: " + "${it.tipo}",
//                            modifier = Modifier.weight(2f)
//                        )
//
//                        Row {
//                            usuarioSesion?.let { user ->
//                                IconButton(onClick = {
//                                    usuarioEditando = user
//                                    nombre = user.nombreUsuario
//                                    apellidos = user.apellidosUsuario
//                                    email = user.email
//                                    incorporacion = user.incorporacionUsuario
//                                    sexo = user.sexo
//                                    showDialog = true
//                                }) {
//                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
//                                }
//                            }
//                            usuarioSesion?.let { user ->
//                                IconButton(onClick = {
//                                    usuarioEditando = user
//                                    nombre = user.nombreUsuario
//                                    apellidos = user.apellidosUsuario
//                                    email = user.email
//                                    incorporacion = user.incorporacionUsuario
//                                    sexo = user.sexo
//                                    showDialog = true
//                                }) {
//                                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            Button(
//                modifier = Modifier.padding(16.dp),
//                onClick = {
//                    navController.navigate(AppScreens.Amigos.route)
//                }
//            ) {
//                Text("Ir a Amigos")
//            }
//
//
//        }
//    }
//
//    // Editar inmueble
//
//    if (showDialog && usuarioEditando != null) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            title = { Text("Editar inmueble") },
//            text = {
//                IconButton(onClick = {
//                    showDialog = false
//                }) {
//                    Icon(Lucide.X, contentDescription = "Cerrar")
//                }
//                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//
//                    OutlinedTextField(
//                        titulo,
//                        { titulo = it },
//                        label = { Text("Titulo del inmueble") })
//                    OutlinedTextField(
//                        descripcion,
//                        { descripcion = it },
//                        label = { Text("Descripción del inmueble") })
//                    OutlinedTextField(
//                        urlImagen,
//                        { urlImagen = it },
//                        label = { Text("Imagen en URL") })
//                    OutlinedTextField(precio, { precio = it }, label = { Text("Precio (€)") })
//                    OutlinedTextField(tipo, { tipo = it }, label = { Text("Tipo de contrato") })
//
////                    MinimalDropdownMenu(tipo = tipo) {
////                        var expanded by remember { mutableStateOf(false) }
////                        Box(
////                            modifier = Modifier
////                                .padding(16.dp)
////                        ) {
////                            IconButton(onClick = { expanded = !expanded }) {
////                                Icon(
////                                    Icons.Default.MoreVert,
////                                    contentDescription = "Tipo de contrato"
////                                )
////                            }
////                            DropdownMenu(
////                                expanded = expanded,
////                                onDismissRequest = { expanded = false }
////                            ) {
////                                DropdownMenuItem(
////                                    text = { Text("Alquiler") },
////                                    onClick = { tipo = "Alquiler" }
////                                )
////                                DropdownMenuItem(
////                                    text = { Text("Venta") },
////                                    onClick = { tipo = "Venta" }
////                                )
////                            }
////                        }
////                    }
//                }
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    scope.launch {
//
//                        val actualizado = usuarioEditando!!.copy(
//                            id = idInmueble,
//                            titulo = titulo,
//                            descripcion = descripcion,
//                            urlImagen = urlImagen,
//                            precio = precio,
//                            tipo = tipo
//                        )
//
//                        db.inmueblesDao().actualizaInmueble(actualizado)
//
//                        firestore.collection("inmuebles")
//                            .document(idInmueble)
//                            .set(
//                                mapOf(
//                                    "titulo" to titulo,
//                                    "descripcion" to descripcion,
//                                    "urlImagen" to urlImagen,
//                                    "precio" to precio,
//                                    "tipo" to tipo
//                                )
//                            )
//
//
////                        if (idUsuarioSesionActual != 0) {
////                            listaUsuarios = db.usuarioDao()
////                                .getListaUsuariosPorId(idUsuarioSesionActual)
////                        }
//
//                        showDialog = false
//                    }
//                }) {
//                    Text("Guardar")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDialog = false }) {
//                    Text("Cancelar")
//                }
//            }
//        )
//    }
//}
//
////@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
////@Composable
////fun ExposedDropdownMenuSample() {
////    val options: List<String> = listOf("Alquiler", "Venta")
////    var expanded by remember { mutableStateOf(false) }
////    var selectedOptionText by remember { mutableStateOf("ro") }
////    val textFieldState = rememberTextFieldState(options[0])
////    var checkedIndex: Int? by remember { mutableStateOf(null) }
////    ExposedDropdownMenuBox(expanded = it, onExpandedChange = { expanded = it }) {
////        TextField(
////            // The `menuAnchor` modifier must be passed to the text field to handle
////            // expanding/collapsing the menu on click. A read-only text field has
////            // the anchor type `PrimaryNotEditable`.
////            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
////            state = textFieldState,
////            readOnly = true,
////            lineLimits = TextFieldLineLimits.SingleLine,
////            label = { Text("Label") },
////            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
////            colors = ExposedDropdownMenuDefaults.textFieldColors(),
////        )
////        ExposedDropdownMenu(
////            expanded = expanded,
////            onDismissRequest = { expanded = false },
////            containerColor = MenuDefaults.groupStandardContainerColor,
////            shape = MenuDefaults.standaloneGroupShape,
////        ) {
////            val optionCount = options.size
////            options.forEachIndexed { index, option ->
////                DropdownMenuItem(
////                    shapes = MenuDefaults.itemShape(index, optionCount),
////                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
////                    selected = index == checkedIndex,
////                    onClick = {
////                        textFieldState.setTextAndPlaceCursorAtEnd(option)
////                        checkedIndex = index
////                    },
////                    checkedLeadingIcon = { Icon(Icons.Filled.Check, contentDescription = null) },
////                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
////                )
////            }
////        }
////    }
////}
//
////@Composable
////fun MinimalDropdownMenu(function: () -> Unit, tipo: String) {
////    var expanded by remember { mutableStateOf(false) }
////    Box(
////        modifier = Modifier
////            .padding(16.dp)
////    ) {
////        IconButton(onClick = { expanded = !expanded }) {
////            Icon(Icons.Default.MoreVert, contentDescription = "Tipo de contrato")
////        }
////        DropdownMenu(
////            expanded = expanded,
////            onDismissRequest = { expanded = false }
////        ) {
////            DropdownMenuItem(
////                text = { Text("Alquiler") },
////                onClick = { tipo = "Alquiler" }
////            )
////            DropdownMenuItem(
////                text = { Text("Venta") },
////                onClick = { tipo = "Venta" }
////            )
////        }
////    }
////}