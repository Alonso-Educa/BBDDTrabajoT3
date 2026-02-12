package com.example.contador.screens

import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.Image
//import androidx.compose.foundation.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Room
import com.example.contador.localdb.AmistadData
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.UsuarioData
import kotlin.collections.associate
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImagePainter.State.Empty.painter
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.contador.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Amigos(navController: NavController) {

    val context = LocalContext.current

//    val db = remember {
//        Room.databaseBuilder(
//            context.applicationContext,
//            AppDB::class.java,
//            Estructura.DB.NAME
//        ).build()
//    }
    val db = remember {
        Room.databaseBuilder(context, AppDB::class.java, Estructura.DB.NAME)
            .allowMainThreadQueries().build()
    }
    // Scope para clicks y eventos
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    // ---------------- ESTADOS ----------------
    var listaUsuarios by remember {
        mutableStateOf<List<UsuarioData>>(emptyList())
    }

    var favoritos by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    // ---------------- CARGA DE DATOS ----------------

    // Sesión
    var idSesionActual by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        idSesionActual = db.sesionDao()
            .getUsuarioSesionActual()
            ?.idUsuario ?: ""
    }

    var idUsuarioSesionActual by remember { mutableStateOf("") }
    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }

//    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }
//
//    LaunchedEffect(Unit) {
//        usuarioSesion = db.sesionDao()
//            .getUsuarioSesionActual()
//
//
//    }getListaUsuarios getAmistadUsuario

    LaunchedEffect(Unit) {
        idUsuarioSesionActual = db.sesionDao()
            .getUsuarioSesionActual()
            ?.idUsuario ?: ""
    }

    LaunchedEffect(Unit) {
        usuarioSesion = db.sesionDao().getUsuarioSesionActual()
    }


    // Usuarios + amistades
    LaunchedEffect(idSesionActual) {
        if (idSesionActual.isNotEmpty()) {

            // 🔥 Cargar usuarios desde Firebase
            firestore.collection("usuarios")
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.documents.mapNotNull { doc ->
                        try {
                            UsuarioData(
                                idUsuario = doc.id, // 👈 el ID es el nombre del documento
                                nombreUsuario = doc.getString("nombre") ?: "",
                                apellidosUsuario = doc.getString("apellidos") ?: "",
                                email = doc.getString("email") ?: "",
                                sexo = doc.getString("sexo") ?: "",
                                incorporacionUsuario = doc.getString("incorporacion") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    listaUsuarios = lista.filter { it.idUsuario != idSesionActual } // ❌ quita tu propio usuario
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error cargando usuarios", Toast.LENGTH_SHORT).show()
                }

            // ⭐ Cargar amistades desde Room (solo relación local)
            val listaAmistades = db.amistadDao()
                .getAmistadUsuario(idSesionActual)

            favoritos = listaAmistades.associate {
                it.idUsuario2 to true
            }
        }
    }


    // ---------------- UI ----------------

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Text("Mis amigos", fontSize = 15.sp)
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        Toast.makeText(context, "Volver atrás", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            navController.navigate(AppScreens.Inicio.route) {
                                popUpTo(0) { inclusive = true }
                                Toast.makeText(context, "Saliendo al menú", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Salir al menú"
                        )
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (idUsuarioSesionActual.isNotEmpty()) {
                                    db.sesionDao().eliminarSesionUsuario(idUsuarioSesionActual)
                                }

                                navController.navigate(AppScreens.Inicio.route) {
                                    popUpTo(0) { inclusive = true }
                                    Toast.makeText(context, "Cerrando sesión", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
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
                },
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(listaUsuarios) { user ->

                val isFavorito by remember(favoritos, user.idUsuario) {
                    mutableStateOf(favoritos[user.idUsuario] == true)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val iniciales = buildString {
                        append(user.nombreUsuario.first().uppercase())
                        if (user.apellidosUsuario.isNotBlank()) {
                            append(user.apellidosUsuario.first().uppercase())
                        }
                    }


                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                MaterialTheme.colorScheme.tertiary,
                                CircleShape
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.secondary,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iniciales,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.nombreUsuario, style = MaterialTheme.typography.titleMedium)
                        Text(user.apellidosUsuario, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }


                    IconButton(
                        onClick = {
                            val nuevoEstado = !isFavorito
                            favoritos =
                                favoritos.toMutableMap().also { it[user.idUsuario] = nuevoEstado }
                            scope.launch {
                                if (nuevoEstado) {

                                    if (db.amistadDao().existeAmistad(idSesionActual,user.idUsuario)==null) {
                                        db.amistadDao().nuevaAmistad(
                                            AmistadData(
                                                idUsuario1 = idSesionActual,
                                                idUsuario2 = user.idUsuario
                                            )
                                        )
                                    }
                                } else {
                                    db.amistadDao().eliminarAmistad(
                                        idUsuario1 = idSesionActual,
                                        idUsuario2 = user.idUsuario
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito usuario",
                            tint = if (isFavorito) {
                                Color.Red
                            } else {
                                Color.Black
                            }
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            }
        }
    }
}



