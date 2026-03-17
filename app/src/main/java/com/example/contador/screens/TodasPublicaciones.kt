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
import com.composables.icons.lucide.Share
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.InmueblesData
import com.example.contador.localdb.PublicacionesData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodasPublicaciones(navController: NavController) {

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
    var estaActivo by remember { mutableStateOf<Int?>(null) }
    var favoritos by remember { mutableStateOf(setOf<Int>()) }

    var idUsuarioSesionActual by remember { mutableStateOf("") }

    // Cargar sesión y lista de inmuebles
    LaunchedEffect(Unit) {
        val uid = Firebase.auth.currentUser?.uid ?: ""
        idUsuarioSesionActual = uid
        usuarioSesion = usuarioDao.getUsuarioPorId(uid)

        firestore.collection("publicaciones")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { publicacion ->
                    try {
                        PublicacionesData(
                            idPublicacion = publicacion.id.hashCode(),
                            idUsuario = publicacion.getString("idUsuario") ?: "",
                            nombreUsuario = publicacion.getString("nombreUsuario") ?: "",
                            titulo = publicacion.getString("titulo") ?: "",
                            descripcion = publicacion.getString("descripcion") ?: "",
                            urlImagen = publicacion.getString("urlImagen") ?: ""
                        )
                    } catch (e: Exception) { null }
                }
                listaPublicaciones = lista
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error cargando publicaciones", Toast.LENGTH_SHORT).show()
            }
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas las Publicaciones", fontSize = 15.sp) },
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

                    // Ir a perfil
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
                }
            )
        },
        bottomBar = { BottomBarPublicaciones(navController as NavHostController) }
    ) { padding ->

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
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(publicacion.nombreUsuario, fontWeight = FontWeight.Bold)
                        AsyncImage(
                            model = publicacion.urlImagen,
                            contentDescription = publicacion.descripcion,
                            modifier = Modifier.size(200.dp)
                        )
                        Row(modifier = Modifier.padding(8.dp)) {
                            // Botón de favorito
                            IconButton(onClick = {
                                favoritos = if (favoritos.contains(publicacion.idPublicacion)) {
                                    favoritos - publicacion.idPublicacion
                                } else {
                                    favoritos + publicacion.idPublicacion
                                }
                            }) {
                                Icon(
                                    imageVector = if (favoritos.contains(publicacion.idPublicacion))
                                        Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (favoritos.contains(publicacion.idPublicacion)) Color.Red else Color.Black
                                )
                            }
                            // Botón de comentar
                            IconButton(onClick = {
                                // por hacer
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Comment,
                                    contentDescription = "Comentario",
                                    tint = Color.Black
                                )
                            }
                            // Botón de compartir
                            IconButton(onClick = {
                                // por hacer
                            }) {
                                Icon(
                                    imageVector = Lucide.Share,
                                    contentDescription = "Compartir",
                                    tint = Color.Black
                                )
                            }
                        }
                        Text(publicacion.titulo, fontWeight = FontWeight.Bold)
                        Text(
                            publicacion.descripcion,
                            maxLines = if (estaActivo == publicacion.idPublicacion) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .animateContentSize()
                                .clickable {
                                    estaActivo =
                                        if (estaActivo == publicacion.idPublicacion) null else publicacion.idPublicacion
                                }
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            }
        }
    }
}
