package com.alarmasensores.app

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class AlarmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Habilitar persistencia offline de Firebase
        // Esto permite que la app funcione sin internet: guarda datos localmente y sincroniza 
        // cuando recupera la conexión.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
