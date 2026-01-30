package com.example.contador.screens

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults.PositionalThreshold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Looks3
import androidx.compose.material.icons.filled.LooksTwo
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Looks3
import androidx.compose.material.icons.outlined.LooksOne
import androidx.compose.material.icons.outlined.LooksTwo
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.contador.R
import com.example.contador.navigation.AppScreens
import com.example.contador.navigation.BottomNavItem
import com.example.contador.notification.NotificationHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// para hacer la barra de navegación lateral hace falta meter el scaffold dentro del drawer
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollP3(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Estado para pull-to-refresh
    var isRefreshing by remember { mutableStateOf(false) }
    val items = remember { mutableStateListOf("Elemento 1", "Elemento 2", "Elemento 3") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Menú", style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()

                    NavigationDrawerItem(
                        label = { Text("Primera") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.LooksOne, contentDescription = null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppScreens.PrimeraP.route)
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Segunda") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.LooksTwo, contentDescription = null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppScreens.SegundaP.route)
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Tercera") },
                        selected = true,
                        icon = { Icon(Icons.Outlined.Looks3, contentDescription = null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    modifier = Modifier.height(60.dp),
                    title = { Text(text = "Tercera ventana", fontSize = 15.sp) },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "backIcon")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Lanzar un aviso Dialog",
                                actionLabel = "Action",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                showDialog = true
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            bottomBar = { BottomBar(navController as NavHostController) }
        ) { innerPadding ->

//            val pullRefreshState = rememberPullRefreshState(
//                refreshing = isRefreshing,
//                onRefresh = {
//                    // Lógica al hacer pull-to-refresh
//                    scope.launch {
//                        isRefreshing = true
//                        delay(1500) // Simula carga de datos
//                        items.add("Nuevo elemento ${items.size + 1}")
//                        isRefreshing = false
//                    }
//                }
//            )

            PullToRefreshCustomIndicatorSample(
                items = items,
                isRefreshing = isRefreshing,
                onRefresh = {
                    // Lógica al hacer pull-to-refresh
                    scope.launch {
                        isRefreshing = true
                        delay(1000) // Simula carga de datos
                        items.add("Nuevo elemento ${items.size + 1}")
                        isRefreshing = false
                    }
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val notificationHandler = NotificationHandler(context)

                Button(onClick = {
                    scope.launch {
                        Toast.makeText(context, "Presionaste ir a la primera ventana", Toast.LENGTH_SHORT).show()
                        notificationHandler.showSimpleNotification(
                            "¡Hola!",
                            "Notificación que irá a la Primera Ventana",
                            "PrimeraP"
                        )
                    }
                }) {
                    Text("Clic para notificación a Primera")
                }

                Button(onClick = {
                    scope.launch {
                        Toast.makeText(context, "Presionaste ir a la segunda ventana", Toast.LENGTH_SHORT).show()
                        notificationHandler.showSimpleNotification(
                            "¡Hola!",
                            "Notificación que irá a la Segunda Ventana",
                            "SegundaP"
                        )
                    }
                }) {
                    Text("Clic para notificación a Segunda")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Tarjeta()
                Spacer(modifier = Modifier.height(16.dp))
                CarouselExample_MultiBrowse()

                if (showDialog) {
                    Dialog(
                        onDismiss = { showDialog = false },
                        onConfirm = {
                            showDialog = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun Tarjeta() {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Text(
            text = "Elevated",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}
@Composable
fun CarouselExample_MultiBrowse() {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val items = remember {
        listOf(
            CarouselItem(0, R.drawable.pou, "cupcake"),
            CarouselItem(1, R.drawable.perro, "donut"),
            CarouselItem(2, R.drawable.gato, "eclair"),
            CarouselItem(3, R.drawable.trex, "froyo"),
            CarouselItem(4, R.drawable.tiburon, "gingerbread"),
        )
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = item.imageResId),
            contentDescription = item.contentDescription,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Dialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Example Icon")
        },
        title = { Text("Alert dialog example") },
        text = { Text("This is an example of an alert dialog with buttons.") },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}


@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(AppScreens.PrimeraP, AppScreens.SegundaP, AppScreens.TerceraP)
    val labels = listOf("Primera", "Segunda", "Tercera")
    val icons = listOf(Icons.Default.Home, Icons.Default.LooksTwo, Icons.Default.Looks3)

    NavigationBar {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                selected = navController.currentDestination?.route == screen.route,
                onClick = { navController.navigate(screen.route) },
                icon = { Icon(icons[index], contentDescription = labels[index]) },
                label = { Text(labels[index]) }
            )
        }
    }
}

@Composable
fun PullToRefreshCustomIndicatorSample(
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
            MyCustomIndicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(items) {
                ListItem({ Text(text = it) })
            }
        }
    }
}

@Composable
fun MyCustomIndicator(
    state: PullRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.pullRefresh(state),
        contentAlignment = Alignment.TopCenter
    ) {
        // Mostrar indicador según si está refrescando o no
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .padding(top = 16.dp)
            )
        } else {
            // Icono que crece según la distancia del pull
            val progress = state.indicatorOffset / 80f // 80f = altura máxima del pull
            Icon(
                imageVector = Icons.Filled.CloudDownload,
                contentDescription = "Pull to refresh",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        this.alpha = progress.coerceIn(0f, 1f)
                        this.scaleX = progress.coerceIn(0f, 1f)
                        this.scaleY = progress.coerceIn(0f, 1f)
                    }
                    .padding(top = 16.dp)
            )
        }
    }
}
