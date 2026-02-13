package com.example.contador.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contador.notification.NotificationHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Para las notificaciones del inicio con destino, solo lo uso para este caso
class RegistroViewModel : ViewModel() {

    fun enviarNotificaciones(notificationHandler: NotificationHandler) {
        viewModelScope.launch {

            notificationHandler.enviarNotificacionSimple(
                "¡Acabas de crear un nuevo usuario!",
                "Inicia sesión para acceder a tu perfil"
            )

            delay(15000)

            notificationHandler.enviarNotificacionConDestino(
                "¡No te pierdas de todas las funcionalidades!",
                "Haz clic aquí para ir a Amigos",
                "Amigos"
            )

            delay(15000)

            notificationHandler.enviarNotificacionConDestino(
                "¡No te pierdas de todas las funcionalidades!",
                "Haz clic aquí para ir a Inmuebles",
                "MisInmuebles"
            )
        }
    }
}
