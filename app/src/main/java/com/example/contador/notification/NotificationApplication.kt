package com.example.contador.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.jar.Manifest

class NotificationApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.O) // Android 8 (Oreo) o superior
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O) // Android 8 (Oreo) o superior
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "notification_channel_id", // ID único (como dirección)
            "Notificaciones", // Nombre que ve el usuario
            NotificationManager.IMPORTANCE_HIGH // Prioridad (sonido + LED + pantalla
            // bloqueada +prioridad alta en la lista)
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel) // Creamos el canal
    }
}


