package com.example.contador.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsuarioDao {

    // 🔹 Obtener todos los usuarios
    @Query("SELECT * FROM usuarios WHERE idUsuario != :idUsuario")
    fun getListaUsuariosPorId(idUsuario: Int): List<UsuarioData>

    @Query("SELECT * FROM usuarios  ")
    fun getListaUsuarios(): List<UsuarioData>

//    @Query("SELECT * FROM usuarios")
//     fun getListaUsuarios(): List<UsuarioData>

    // 🔹 Obtener usuario por email (sesión / edición)
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun getUsuarioPorEmail(email: String): UsuarioData?

    // 🔹 Obtener un usuario
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun getUnUser(email: String): UsuarioData?

    // 🔹 Insertar usuario nuevo
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun nuevoUsuario(usuario: UsuarioData)

    // 🔹 Actualizar usuario existente
    @Update
    fun actualizaUsuario(usuario: UsuarioData)

    // 🔹 Eliminar usuario (opcional)
    @Delete
    fun borrarUsuario(usuario: UsuarioData)

    @Query("SELECT * FROM usuarios WHERE idUsuario = :id LIMIT 1")
    fun getUsuarioPorId(id: Int): UsuarioData?


}

