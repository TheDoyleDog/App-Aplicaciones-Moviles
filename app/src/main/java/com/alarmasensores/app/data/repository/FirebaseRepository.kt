package com.alarmasensores.app.data.repository

import com.alarmasensores.app.data.model.AlarmConfig
import com.alarmasensores.app.data.model.AlarmState
import com.alarmasensores.app.data.model.DetectionEvent
import com.alarmasensores.app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // --- Autenticación ---

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return firebaseUser?.let {
            User(
                id = it.uid,
                email = it.email ?: "",
                fullName = it.displayName ?: "Usuario"
            )
        }
    }

    suspend fun login(email: String, pass: String): User {
        val result = auth.signInWithEmailAndPassword(email, pass).await()
        val user = result.user ?: throw Exception("Error al obtener usuario")
        return User(
            id = user.uid,
            email = user.email ?: "",
            fullName = user.displayName ?: "Usuario"
        )
    }

    suspend fun register(email: String, pass: String, name: String): User {
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        val user = result.user ?: throw Exception("Error al crear usuario")
        
        // Actualizar perfil con nombre
        /* 
           Nota: Actualizar el perfil podría requerir UserProfileChangeRequest, 
           pero por simplicidad podemos guardarlo en la BD o asumir que se configura luego.
           Por ahora, retornamos el usuario.
        */
        
        return User(
            id = user.uid,
            email = user.email ?: "",
            fullName = name
        )
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    // --- Base de Datos en Tiempo Real ---

    private fun getUserRef() = auth.currentUser?.uid?.let { uid ->
        database.getReference("users").child(uid)
    }

    // Estado de la Alarma
    fun getAlarmState(): Flow<AlarmState> = callbackFlow {
        val ref = getUserRef()?.child("alarm_state")
        if (ref == null) {
            close(Exception("No hay usuario logueado"))
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val state = snapshot.getValue(AlarmState::class.java) ?: AlarmState()
                trySend(state)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun saveAlarmState(state: AlarmState) {
        getUserRef()?.child("alarm_state")?.setValue(state)?.await()
    }

    // Configuración de la Alarma
    fun getAlarmConfig(): Flow<AlarmConfig> = callbackFlow {
        val ref = getUserRef()?.child("alarm_config")
        if (ref == null) {
            close(Exception("No hay usuario logueado"))
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val config = snapshot.getValue(AlarmConfig::class.java) ?: AlarmConfig()
                trySend(config)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun saveAlarmConfig(config: AlarmConfig) {
        getUserRef()?.child("alarm_config")?.setValue(config)?.await()
    }

    // Historial de Detecciones
    fun getDetectionHistory(): Flow<List<DetectionEvent>> = callbackFlow {
        val ref = getUserRef()?.child("history")
        if (ref == null) {
            close(Exception("No hay usuario logueado"))
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<DetectionEvent>()
                for (child in snapshot.children) {
                    child.getValue(DetectionEvent::class.java)?.let { events.add(it) }
                }
                // Ordenar por fecha descendente
                events.sortByDescending { it.timestamp }
                trySend(events)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun addDetectionEvent(event: DetectionEvent) {
        getUserRef()?.child("history")?.child(event.id)?.setValue(event)?.await()
    }
    
    suspend fun clearHistory() {
        getUserRef()?.child("history")?.removeValue()?.await()
    }
}
