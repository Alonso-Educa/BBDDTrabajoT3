package com.example.contador.notification

import android.R
//import com.example.contador.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.example.contador.MainActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.jar.Manifest
import kotlin.random.Random


class NotificationHandler(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val channelId = "notification_channel_id" // ID único (como dirección)

    fun showSimpleNotification(titulo: String, cuerpo: String, destino: String) {
        // Intent explícito a la Activity para abrir MainActivity al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            // Flags de cómo abrir la Activity
            // NEW_TASK: Abre nueva instancia si app cerrada CLEAR_TASK: Elimina todas las instancias anteriores va directo a MainActivity
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destino", destino) // Ventana a la que se irá al hacer clic en la notificación
        }
        // PendingIntent (lo que se ejecuta al tocar la notificación), es decir, permiso para ejecutar el Intent
        val pendingIntent = PendingIntent.getActivity(
            context, // Quién (app) lo pide, el contexto
            0, // ID único
            intent, // Intent que lleva dentro, creado anteriormente
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            // UPDATE_CURRENT: Reemplaza si ya existe
            // IMMUTABLE: No lo modifica (seguridad para Android 12 o superior)
        )
        // Construir la notificación. Builder con setContentIntent(pendingIntent) para la creación de la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(titulo) // Título
            .setContentText(cuerpo) // Texto / cuerpo de la notificación
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono del sistema
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Tipo de Prioridad (importancia)
            .setAutoCancel(true) // Que la notificación se descarte (borre) al tocarla
            .setContentIntent(pendingIntent) // Abre la aplicación al hacer clic
            .build() // Crea objeto final
        notificationManager.notify(Random.nextInt(), notification)
    }
}