package com.example.contador.screens

import android.provider.CalendarContract.Instances.query
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.compose.AsyncImage
import com.composables.icons.lucide.CircleUserRound
import com.composables.icons.lucide.HousePlus
import com.composables.icons.lucide.Lucide
import com.example.contador.localdb.AmistadData
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.InmueblesData
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlin.collections.set
import kotlin.collections.toMutableMap

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

    val listState = rememberLazyListState()
    var query by remember { mutableStateOf("") }

    // Cargar sesión y lista de inmuebles
    LaunchedEffect(Unit) {
        val sesion = sesionDao.getUsuarioSesionActual()
        idUsuarioSesionActual = sesion?.idUsuario ?: ""
        usuarioSesion = sesion?.let { usuarioDao.getUsuarioPorId(it.idUsuario) }

        firestore.collection("inmuebles")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { inmueble ->
                    try {
                        InmueblesData(
                            idInmueble = inmueble.id.hashCode(), // ID local temporal
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
                }
            }
        }
    ) {
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
            bottomBar = { BottomBarInmuebles(navController as NavHostController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                BarraBusquedaInmuebles(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { /* opcional */ },
                    searchResults = listaInmuebles
                        .filter { it.titulo.contains(query, ignoreCase = true) }
                        .map { it.titulo },
                    onResultClick = { tituloSeleccionado ->
                        val index = listaInmuebles.indexOfFirst { it.titulo == tituloSeleccionado }
                        if (index != -1) {
                            scope.launch {
                                listState.animateScrollToItem(index)
                            }
                        }
                    }
                )
                val listaFiltrada = listaInmuebles.filter {
                    it.titulo.contains(query, ignoreCase = true)
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.padding(padding),

                    ) {
//                items(listaInmuebles) { inmueble ->
                    items(listaFiltrada, key = { it.idInmueble }) { inmueble ->
                        var isFavorito by remember(inmueble.idInmueble) {
                            mutableStateOf(false)
                        }
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
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    isFavorito = !isFavorito
                                    scope.launch {
                                        if (isFavorito) {
                                            showToast(
                                                context,
                                                "Inmueble ${inmueble.titulo} añadido correctamente a favoritos"
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Inmueble favorito usuario",
                                    tint = if (isFavorito) {
                                        Color.Red
                                    } else {
                                        Color.Black
                                    }
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraBusquedaInmuebles(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<String>,
    onResultClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    // Customization options
    placeholder: @Composable () -> Unit = { Text("Buscar") },
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            Icons.Default.Search,
            contentDescription = "Search"
        )
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingContent: (@Composable (String) -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    // Track expanded state of search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .wrapContentHeight()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                // Customizable input field implementation
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(query)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // Show search results in a lazy column for better performance
            LazyColumn {
                items(count = searchResults.size) { index ->
                    val resultText = searchResults[index]
                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = supportingContent?.let { { it(resultText) } },
                        leadingContent = leadingContent,
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                onResultClick(resultText)
                                expanded = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// Para Deslizar para Actualizar (8)
@Composable
fun DeslizarParaActualizarInmuebles(
    items: List<String>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(items) {
                ListItem({ Text(text = it) })
            }
        }
    }
}
