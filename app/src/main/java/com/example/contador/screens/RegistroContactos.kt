package com.example.contador.screens

import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.contador.localdb.AppDB
import com.example.contador.localdb.ContactosData
import com.example.contador.localdb.Estructura
import com.example.contador.localdb.UsuarioData
import com.example.contador.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroContactos(navController: NavController) {

    // Variables usadas
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    val uid = Firebase.auth.currentUser?.uid ?: ""

    // Base de datos de room
    val db = remember {
        Room.databaseBuilder(
            context.applicationContext, AppDB::class.java, Estructura.DB.NAME
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    val contactosDao = db.contactosDao()
    val usuarioDao = db.usuarioDao()

    // Variables para la operación create
    var createId by remember { mutableStateOf("") }
    var createNombre by remember { mutableStateOf("") }
    var createApellidos by remember { mutableStateOf("") }
    var createCorreo by remember { mutableStateOf("") }

    // Variables para la operación read
    var readId by remember { mutableStateOf("") }
    var readResultadoRoom by remember { mutableStateOf<ContactosData?>(null) }
    var readResultadoFirebase by remember { mutableStateOf("") }

    // Variables para la operación update
    var updateId by remember { mutableStateOf("") }
    var updateNombre by remember { mutableStateOf("") }
    var updateApellidos by remember { mutableStateOf("") }
    var updateCorreo by remember { mutableStateOf("") }
    var booleanContactoEstaCargadoRoom by remember { mutableStateOf(false) }
    var booleanContactoEstaCargadoFirebase by remember { mutableStateOf(false) }

    // Variables para la operación delete
    var deleteId by remember { mutableStateOf("") }

    // Variables del usuario
    var idUsuarioSesionActual by remember { mutableStateOf("") }
    var usuarioSesion by remember { mutableStateOf<UsuarioData?>(null) }

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var incorporacion by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("Hombre") }

    // Carga de datos
    LaunchedEffect(Unit) {
        idUsuarioSesionActual = uid
        usuarioSesion = db.usuarioDao().getUsuarioPorId(uid)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Contactos", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    // Regresar a mi perfil
                    IconButton(onClick = {
                        navController.navigate(AppScreens.MenuPrincipal.route)
                    }) {
                        Icon(
                            Icons.Default.Home, contentDescription = "Regresar a mi perfil"
                        )
                    }

                    usuarioSesion?.let { usuario ->
                        var showCardDialog by remember { mutableStateOf(false) }

                        // Icono circular clicable
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

                        // Dialog con tarjeta de usuario
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
        bottomBar = { BottomBarProductos(navController as NavHostController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Operación CRUD 1: Create
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.tertiary
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("1. CREATE", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            // Contenido visual del create
            Text(
                text = "Room:  INSERT INTO CONTACTOS (idContacto, idUsuario, nombre, apellidos, correo)\n" + "  VALUES (${createId.ifBlank { "?" }}, \"$uid\",\n" + "  \"${createNombre.ifBlank { "?" }}\", \"${createApellidos.ifBlank { "?" }}\", \"${createCorreo.ifBlank { "?" }}\")\n\n" + "Firebase: contactos/${createId.ifBlank { "?" }}\n" + "  .set({ nombre: \"${createNombre.ifBlank { "?" }}\", apellidos: \"${createApellidos.ifBlank { "?" }}\", correo: \"${createCorreo.ifBlank { "?" }}\" })",
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            )

            // Campos para formulario
            OutlinedTextField(
                createId,
                { createId = it },
                label = { Text("ID de Contacto (int)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                createNombre,
                { createNombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                createApellidos,
                { createApellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                createCorreo,
                { createCorreo = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botones para crear contacto
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    modifier = Modifier.weight(1f), onClick = {
                        scope.launch {
                            if (createId.isBlank() || createNombre.isBlank() || createApellidos.isBlank() || createCorreo.isBlank()) {
                                Toast.makeText(
                                    context, "Rellena todos los campos", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val id = createId.toIntOrNull()
                                if (id == null) {
                                    Toast.makeText(
                                        context,
                                        "El ID debe ser un número entero",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    try {
                                        val usuarioExiste = contactosDao.getContactoPorId(id)
                                        if (usuarioExiste == null) {
                                            contactosDao.nuevoContacto(
                                                ContactosData(
                                                    id,
                                                    uid,
                                                    createNombre,
                                                    createApellidos,
                                                    createCorreo
                                                )
                                            )
                                            Toast.makeText(
                                                context,
                                                "Room: Contacto creado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Room: Ya existe un contacto con ese ID",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context, "Room error: ${e.message}", Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text("Guardar en Room")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (createId.isBlank() || createNombre.isBlank() || createApellidos.isBlank() || createCorreo.isBlank()) {
                            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            // Comprueba si el contacto ya existe antes de crearlo
                            firestore.collection("contactos").document(createId).get()
                                .addOnSuccessListener { doc ->
                                    if (doc.exists()) {
                                        Toast.makeText(
                                            context,
                                            "Ya existe un contacto con ese ID",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val data = mapOf(
                                            "idContacto" to createId,
                                            "idUsuario" to uid,
                                            "nombre" to createNombre,
                                            "apellidos" to createApellidos,
                                            "correo" to createCorreo
                                        )

                                        firestore.collection("contactos").document(createId)
                                            .set(data)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Firebase: Contacto creado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }.addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    "Firebase error: ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Error al verificar contacto: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                ) {
                    Text("Guardar en Firebase")
                }
            }

            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))

            // Operación CRUD 2: Read
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("2. READ", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            // Contenido visual del read
            Text(
                text = "Room:  SELECT * FROM CONTACTOS\n" + "  WHERE idContacto = ${readId.ifBlank { "?" }} LIMIT 1\n\n" + "Firebase: contactos/${readId.ifBlank { "?" }}.get()",
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            )

            // Campos para formulario
            OutlinedTextField(
                readId,
                { readId = it },
                label = { Text("ID del Contacto a buscar") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botones para buscar contacto
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            val id = readId.toIntOrNull()
                            if (id == null) {
                                Toast.makeText(
                                    context, "El ID debe ser un número entero", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                readResultadoRoom = contactosDao.getContactoPorId(id)
                                if (readResultadoRoom == null) Toast.makeText(
                                    context,
                                    "Room: No existe ningún contacto con ese ID",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Text("Buscar en Room")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (readId.isBlank()) {
                            Toast.makeText(context, "Introduce un ID", Toast.LENGTH_SHORT).show()
                        }
                        firestore.collection("contactos").document(readId).get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    readResultadoFirebase =
                                        "id: ${snapshot.id}\n" + "nombre: ${snapshot.getString("nombre")}\n" + "apellidos: ${
                                            snapshot.getString("apellidos")
                                        }\n" + "correo: ${snapshot.getString("correo")}"
                                } else {
                                    readResultadoFirebase = ""
                                    Toast.makeText(
                                        context,
                                        "Firebase: No existe ningún contacto con ese ID",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    context, "Firebase error: ${e.message}", Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                ) {
                    Text("Buscar en Firebase")
                }
            }

            // Muestra los resultados si se devuelve algo
            if (readResultadoRoom != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("Resultado Room", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            "ID: ",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(readResultadoRoom!!.idContacto.toString(), fontSize = 13.sp)
                    }
                    Row {
                        Text(
                            "Nombre: ",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(readResultadoRoom!!.nombre, fontSize = 13.sp)
                    }
                    Row {
                        Text(
                            "Apellidos: ",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(readResultadoRoom!!.apellidos, fontSize = 13.sp)
                    }
                    Row {
                        Text(
                            "Correo: ",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(readResultadoRoom!!.correo, fontSize = 13.sp)
                    }
                }
            }

            // Muestra los resultados si se devuelve algo
            if (readResultadoFirebase.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text("Resultado Firebase", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(readResultadoFirebase, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                }
            }

            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))

            // Operación CRUD 3: Update
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("3. UPDATE", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            // Contenido visual del update
            Text(
                text = "Room:  UPDATE CONTACTOS SET\n" + "  nombre = \"${updateNombre.ifBlank { "?" }}\",\n" + "  apellidos = \"${updateApellidos.ifBlank { "?" }}\",\n" + "  correo = \"${updateCorreo.ifBlank { "?" }}\"\n" + "  WHERE idContacto = ${updateId.ifBlank { "?" }}\n\n" + "Firebase: contactos/${updateId.ifBlank { "?" }}\n" + "  .update({ nombre: \"${updateNombre.ifBlank { "?" }}\", apellidos: \"${updateApellidos.ifBlank { "?" }}\", correo: \"${updateCorreo.ifBlank { "?" }}\" })",
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Campos para formulario
                OutlinedTextField(
                    value = updateId,
                    onValueChange = {
                        updateId = it; booleanContactoEstaCargadoRoom =
                        false; booleanContactoEstaCargadoFirebase = false
                    },
                    label = { Text("ID del Contacto a actualizar") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón para cargar contacto en Room
                    Button(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        onClick = {
                            scope.launch {
                                val id = updateId.toIntOrNull()
                                if (id == null) {
                                    Toast.makeText(
                                        context, "El ID debe ser un número entero", Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val c = contactosDao.getContactoPorId(id)
                                    if (c != null) {
                                        updateNombre = c.nombre
                                        updateApellidos = c.apellidos
                                        updateCorreo = c.correo
                                        booleanContactoEstaCargadoRoom = true
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Room: No existe ningún contacto con ese ID",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Cargar (Room)")
                    }

                    // Botón para cargar contacto en Firebase
                    Button(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        onClick = {
                            if (updateId.isBlank()) {
                                Toast.makeText(context, "Introduce un ID", Toast.LENGTH_SHORT).show()
                            } else {
                                firestore.collection("contactos").document(updateId).get()
                                    .addOnSuccessListener { doc ->
                                        if (doc.exists()) {
                                            updateNombre = doc.getString("nombre") ?: ""
                                            updateApellidos = doc.getString("apellidos") ?: ""
                                            updateCorreo = doc.getString("correo") ?: ""
                                            booleanContactoEstaCargadoFirebase = true
                                            booleanContactoEstaCargadoRoom = false
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Firebase: No existe ningún contacto con ese ID",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Firebase error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }
                    ) {
                        Text("Cargar (Firebase)")
                    }
                }
            }

            // Cargar contacto en Room
            if (booleanContactoEstaCargadoRoom) {
                booleanContactoEstaCargadoFirebase = false
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    "Editar campos en Room (el ID no se puede modificar)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    updateNombre,
                    { updateNombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    updateApellidos,
                    { updateApellidos = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    updateCorreo,
                    { updateCorreo = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botones de actualizar contacto
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        modifier = Modifier.weight(1f), onClick = {
                            scope.launch {
                                try {
                                    contactosDao.actualizarContacto(
                                        ContactosData(
                                            updateId.toInt(),
                                            uid,
                                            updateNombre,
                                            updateApellidos,
                                            updateCorreo
                                        )
                                    )
                                    Toast.makeText(
                                        context,
                                        "Room: Contacto actualizado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context, "Room error: ${e.message}", Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    ) {
                        Text("Actualizar Room")
                    }
                }
            }

            // Cargar contacto en Firebase
            if (booleanContactoEstaCargadoFirebase) {
                booleanContactoEstaCargadoRoom = false
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    "Editar campos en Firebase (el ID no se puede modificar)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    updateNombre,
                    { updateNombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    updateApellidos,
                    { updateApellidos = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    updateCorreo,
                    { updateCorreo = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botones de actualizar contacto
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        modifier = Modifier.weight(1f), onClick = {
                            if (updateId.isNotBlank()) {
                                firestore.collection("contactos").document(updateId).update(
                                    mapOf(
                                        "nombre" to updateNombre,
                                        "apellidos" to updateApellidos,
                                        "correo" to updateCorreo
                                    )
                                ).addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Firebase: Contacto actualizado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Firebase error: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    ) {
                        Text("Actualizar Firebase")
                    }
                }
            }

            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))

            // Operación CRUD 4: Delete
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("4. DELETE", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            // Contenido visual del delete
            Text(
                text = "Room:  DELETE FROM CONTACTOS\n" + "  WHERE idContacto = ${deleteId.ifBlank { "?" }}\n\n" + "Firebase: contactos/${deleteId.ifBlank { "?" }}.delete()",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            )

            // Campos para formulario
            OutlinedTextField(
                deleteId,
                { deleteId = it },
                label = { Text("ID del Contacto a eliminar") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botones para eliminar contacto
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        scope.launch {
                            val id = deleteId.toIntOrNull()
                            if (id == null) {
                                Toast.makeText(
                                    context, "El ID debe ser un número entero", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val contacto = contactosDao.getContactoPorId(id)
                                if (contacto != null) {
                                    contactosDao.borrarContacto(contacto)
                                    Toast.makeText(
                                        context,
                                        "Room: Contacto eliminado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Room: No se encontró ningún contacto con ese ID",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                ) {
                    Text("Eliminar en Room")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        if (deleteId.isBlank()) {
                            Toast.makeText(context, "Introduce un ID", Toast.LENGTH_SHORT).show()
                        } else {
                            firestore.collection("contactos").document(deleteId).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Firebase: Contacto eliminado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        context, "Firebase error: ${e.message}", Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                ) {
                    Text("Eliminar en Firebase")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}