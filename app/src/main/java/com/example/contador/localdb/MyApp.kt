package com.example.contador.localdb

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa Firebase al inicio de la app
        FirebaseApp.initializeApp(this)
    }
}
