package com.example.contador.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsuarioDao {

    // 🔹 Obtener todos los usuarios excepto uno (ahora idUsuario es String)
    @Query("SELECT * FROM usuarios WHERE idUsuario != :idUsuario")
    fun getListaUsuariosPorId(idUsuario: String): List<UsuarioData>

    // 🔹 Obtener todos los usuarios
    @Query("SELECT * FROM usuarios")
    fun getListaUsuarios(): List<UsuarioData>

    // 🔹 Obtener usuario por email (sesión / edición)
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun getUsuarioPorEmail(email: String): UsuarioData?

    // 🔹 Obtener un usuario por email
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun getUnUser(email: String): UsuarioData?

    // 🔹 Insertar usuario nuevo
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun nuevoUsuario(usuario: UsuarioData)

    // 🔹 Actualizar usuario existente
    @Update
    fun actualizaUsuario(usuario: UsuarioData)

    // 🔹 Eliminar usuario
    @Delete
    fun borrarUsuario(usuario: UsuarioData)

    // 🔹 Obtener usuario por ID (ahora idUsuario es String)
    @Query("SELECT * FROM usuarios WHERE idUsuario = :id LIMIT 1")
    fun getUsuarioPorId(id: String): UsuarioData?
}
