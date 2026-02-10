package com.example.contador.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.AsyncImage
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.HousePlus
import com.composables.icons.lucide.Lucide
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.InmueblesData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodosInmuebles(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDB::class.java,
            Estructura.DB.NAME
        ).allowMainThreadQueries().build()
    }

    val sesionDao = db.sesionDao()
    val usuarioDao = db.usuarioDao()
    val inmuebleDao = db.inmueblesDao()

    // Estados
    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }
    var listaInmuebles by remember { mutableStateOf<List<InmueblesData>>(emptyList()) }
    var estaActivo by remember { mutableStateOf<Int?>(null) }
    var favoritos by remember { mutableStateOf(setOf<Int>()) }

    var idUsuarioSesionActual by remember { mutableStateOf("") }

    // Cargar sesión y lista de inmuebles
    LaunchedEffect(Unit) {
        val sesion = sesionDao.getUsuarioSesionActual()
        idUsuarioSesionActual = sesion?.idUsuario ?: ""
        usuarioSesion = sesion?.let { usuarioDao.getUsuarioPorId(it.idUsuario) }

        firestore.collection("inmuebles")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    try {
                        InmueblesData(
                            idInmueble = doc.id.hashCode(), // ID local temporal
                            idUsuario = doc.getString("idUsuario") ?: "",
                            titulo = doc.getString("titulo") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            urlImagen = doc.getString("urlImagen") ?: "",
                            precio = doc.getDouble("precio") ?: 0.0,
                            tipo = doc.getString("tipo") ?: ""
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


    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todos los Inmuebles", fontSize = 15.sp) },
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
                        Icon(Icons.Default.Home, contentDescription = "Perfil")
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
                    // Inicial usuario
                    usuarioSesion?.let {
                        val inicial = it.nombreUsuario.firstOrNull()?.uppercase() ?: "U"
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.tertiary, CircleShape
                                )
                                .border(
                                    1.dp, MaterialTheme.colorScheme.secondary, CircleShape
                                ), contentAlignment = Alignment.Center
                        ) {
                            Text(text = inicial, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            )
        },
        bottomBar = { BottomBarInmuebles(navController as NavHostController) }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding)) {
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
                            inmueble.descripcion,
                            maxLines = if (estaActivo == inmueble.idInmueble) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .animateContentSize()
                                .clickable {
                                    estaActivo =
                                        if (estaActivo == inmueble.idInmueble) null else inmueble.idInmueble
                                }
                        )
                        Text("€ ${inmueble.precio}")
                        Text("Tipo: ${inmueble.tipo}")
                    }

                    // Botón de favorito
                    IconButton(onClick = {
                        favoritos = if (favoritos.contains(inmueble.idInmueble)) {
                            favoritos - inmueble.idInmueble
                        } else {
                            favoritos + inmueble.idInmueble
                        }
                    }) {
                        Icon(
                            imageVector = if (favoritos.contains(inmueble.idInmueble))
                                Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (favoritos.contains(inmueble.idInmueble)) Color.Red else Color.Black
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            }
        }
    }

}
