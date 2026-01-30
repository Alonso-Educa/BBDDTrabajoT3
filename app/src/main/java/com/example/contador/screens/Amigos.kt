//package com.example.contador.screens
//
//import android.graphics.drawable.Drawable
//import android.widget.Toast
//import androidx.compose.foundation.Image
////import androidx.compose.foundation.R
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.filled.FavoriteBorder
//import androidx.compose.material3.DividerDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.room.Room
//import com.example.contador.localdb.AmistadData
//import com.example.contador.localdb.AppDB
//import com.example.contador.localdb.Estructura
//import com.example.contador.localdb.UsuarioData
//import kotlin.collections.associate
//import androidx.compose.foundation.lazy.items
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import coil.compose.AsyncImagePainter.State.Empty.painter
//import kotlinx.coroutines.launch
//import androidx.compose.foundation.Image
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import coil.compose.AsyncImage
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Amigos(navController: NavController) {
//
//    val context = LocalContext.current
//
////    val db = remember {
////        Room.databaseBuilder(
////            context.applicationContext,
////            AppDB::class.java,
////            Estructura.DB.NAME
////        ).build()
////    }
//    val db = remember {
//        Room.databaseBuilder(context, AppDB::class.java, Estructura.DB.NAME)
//            .allowMainThreadQueries().build()
//    }
//    // Scope para clicks y eventos
//    val scope = rememberCoroutineScope()
//
//    // ---------------- ESTADOS ----------------
//
//
//
//    var listaUsuarios by remember {
//        mutableStateOf<List<UsuarioData>>(emptyList())
//    }
//
//    var favoritos by remember {
//        mutableStateOf<Map<Int, Boolean>>(emptyMap())
//    }
//
//    // ---------------- CARGA DE DATOS ----------------
//
//    // Sesión
//    var idSesionActual by remember {
//        mutableIntStateOf(0)
//    }
//    LaunchedEffect(Unit) {
//        idSesionActual = db.sesionDao()
//            .getUsuarioSesionActual()
//            ?.idUsuario ?: 0
//    }
//
//
////    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }
////
////    LaunchedEffect(Unit) {
////        usuarioSesion = db.sesionDao()
////            .getUsuarioSesionActual()
////
////
////    }getListaUsuarios getAmistadUsuario
//
//
//    // Usuarios + amistades
//    LaunchedEffect(idSesionActual) {
//        if (idSesionActual != 0) {
//
//            listaUsuarios = db.usuarioDao()
//                .getListaUsuariosPorId(idSesionActual)
//
//            val listaAmistades = db.amistadDao()
//                .getAmistadUsuario(idSesionActual)
//
//            favoritos = listaAmistades.associate {
//                it.idUsuario2 to true
//            }
//        }
//    }
//
//    // ---------------- UI ----------------
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                modifier = Modifier.height(80.dp),
//                title = {
//                    Text("Mis amigos", fontSize = 15.sp)
//                },
//                colors = topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = Color.White
//                ),
//                navigationIcon = {
//                    IconButton(onClick = {
//                        navController.popBackStack()
//                        Toast.makeText(context, "Volver atrás", Toast.LENGTH_SHORT).show()
//                    }) {
//                        Icon(
//                            Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Volver"
//                        )
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//
//        LazyColumn(
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            items(listaUsuarios) { user ->
//
//                val isFavorito by remember(favoritos, user.idUsuario) {
//                    mutableStateOf(favoritos[user.idUsuario] == true)
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 12.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//                    val iniciales = buildString {
//                        append(user.nombreUsuario.first().uppercase())
//                        if (user.apellidosUsuario.isNotBlank()) {
//                            append(user.apellidosUsuario.first().uppercase())
//                        }
//                    }
//                    AsyncImage(
//                        model = "https://i.pinimg.com/originals/ce/2a/a4/ce2aa4b802e2645bb741353f3e519d9f.jpg",
//                        contentDescription = "Imagen de un león",
//                        modifier = Modifier.size(40.dp)
//                    )
//
//
//                    Box(
//                        modifier = Modifier
//                            .size(54.dp)
//                            .background(
//                                MaterialTheme.colorScheme.primaryContainer,
//                                CircleShape
//                            )
//                            .border(
//                                1.dp,
//                                MaterialTheme.colorScheme.primary,
//                                CircleShape
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = iniciales,
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(12.dp))
//
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(user.nombreUsuario, style = MaterialTheme.typography.titleMedium)
//                        Text(user.apellidosUsuario, style = MaterialTheme.typography.bodyMedium)
//                        Text(
//                            user.email,
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//
//
//                    IconButton(
//                        onClick = {
//                            val nuevoEstado = !isFavorito
//                            favoritos =
//                                favoritos.toMutableMap().also { it[user.idUsuario] = nuevoEstado }
//                            scope.launch {
//                                if (nuevoEstado) {
//
//                                    if (db.amistadDao().existeAmistad(idSesionActual,user.idUsuario)==null) {
//                                        db.amistadDao().nuevaAmistad(
//                                            AmistadData(
//                                                idUsuario1 = idSesionActual,
//                                                idUsuario2 = user.idUsuario
//                                            )
//                                        )
//                                    }
//                                } else {
//                                    db.amistadDao().eliminarAmistad(
//                                        idUsuario1 = idSesionActual,
//                                        idUsuario2 = user.idUsuario
//                                    )
//                                }
//                            }
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
//
//                HorizontalDivider(
//                    modifier = Modifier.padding(horizontal = 16.dp),
//                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
//                )
//            }
//        }
//    }
//}
//
//
//
