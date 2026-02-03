//package com.example.contador.screens
//
//import android.widget.Toast
//import androidx.compose.animation.animateContentSize
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.filled.FavoriteBorder
//import androidx.compose.material.icons.filled.Logout
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.BottomAppBar
//import androidx.compose.material3.BottomAppBarDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.FloatingActionButtonDefaults
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import androidx.room.Room
//import coil.compose.AsyncImage
//import com.composables.icons.lucide.CircleUserRound
//import com.composables.icons.lucide.HousePlus
//import com.composables.icons.lucide.Lucide
//import com.example.contador.localdb.AmistadData
//import com.example.contador.localdb.AppDB
//import com.example.contador.localdb.Estructura
//import com.example.contador.localdb.InmueblesData
//import com.example.contador.localdb.UsuarioData
//import com.example.contador.navigation.AppScreens
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.launch
//import kotlin.collections.set
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TodosInmuebles(navController: NavController) {
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
//    var isFavorito by remember { mutableStateOf(false) }
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
//                title = { Text("Lista de Inmuebles") },
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
//                }
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
//    ) { innerPadding ->
//
//        // Screen content
//        LazyColumn {
//            items(listaInmuebles.size) { inmueble ->
//
//                AsyncImage(
//                    model = urlImagen,
//                    contentDescription = descripcion,
//                    modifier = Modifier.size(40.dp)
//                )
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//
//                    Text(
//                        text = inmueble.tituloInmueble,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    Text(
//                        text = it.descripcion,
//                        maxLines = if (estaActivo) Int.MAX_VALUE else 3,
//                        overflow = TextOverflow.Ellipsis,
//                        modifier = Modifier
//                            .weight(2f)
//                            .animateContentSize()
//                            .clickable {
//                                estaActivo = !estaActivo
//                            }
//                    )
//
//                    Text(
//                        text = it.precio,
//                        modifier = Modifier.weight(1f).color(Color.Blue)
//                    )
//
//                    Text(
//                        text = "Tipo: " + "${it.tipo}",
//                        modifier = Modifier.weight(2f)
//                    )
//
//                    IconButton(
//                        onClick = {
//                            isFavorito = !isFavorito
//                        }
//                    ) {
//                        Icon(
//                            imageVector = if (isFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
//                            contentDescription = "Favorito usuario",
//                            tint = if (isFavorito) {
//                                Color.Red
//                            } else {
//                                Color.Black
//                            }
//                        )
//                    }
//                }
//                HorizontalDivider(
//                    modifier = Modifier.padding(horizontal = 16.dp),
//                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
//                )
//            }
//        }
//    }
//}
//
