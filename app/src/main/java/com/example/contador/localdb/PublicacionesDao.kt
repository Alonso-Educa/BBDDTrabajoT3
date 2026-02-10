package com.example.contador.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PublicacionesDao {
    // 🔹 Obtener todas las publicaciones de todos los usuarios
    @Query("SELECT * FROM PUBLICACIONES")
    suspend fun getListaPublicacionesTodos(): List<PublicacionesData>

    // 🔹 Obtener todas las publicaciones de un usuario
    @Query("SELECT * FROM PUBLICACIONES WHERE idUsuario = :idUsuario")
    suspend fun getPublicacionesDeUsuario(idUsuario: String): List<PublicacionesData>

    // 🔹 Obtener todas las publicaciones excepto las del usuario actual
    @Query("SELECT * FROM PUBLICACIONES WHERE idUsuario != :idUsuario")
    suspend fun getPublicacionesExcluyendoUsuario(idUsuario: String): List<PublicacionesData>

    // 🔹 Obtener una publicacion por id
    @Query("SELECT * FROM PUBLICACIONES WHERE idPublicacion = :idPublicacion LIMIT 1")
    suspend fun getPublicacion(idPublicacion: Int): PublicacionesData?

    // 🔹 Comprobar si existe una publicacion por id
    @Query("SELECT * FROM PUBLICACIONES WHERE idPublicacion = :idPublicacion LIMIT 1")
    suspend fun existePublicacion(idPublicacion: Int): PublicacionesData?

    // 🔹 Insertar nueva publicacion
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun nuevaPublicacion(publicacion: PublicacionesData)

    // 🔹 Actualizar publicacion existente
    @Update
    suspend fun actualizaPublicacion(publicacion: PublicacionesData)

    // 🔹 Eliminar publicacion por id
    @Query("DELETE FROM PUBLICACIONES WHERE idPublicacion = :idPublicacion")
    suspend fun eliminarPublicacion(idPublicacion: Int)
}
