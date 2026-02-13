package com.example.contador.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface InmueblesDao {
    // 🔹 Obtener todos los inmuebles de todos los usuarios
    @Query("SELECT * FROM INMUEBLES")
     fun getListaInmueblesTodos(): List<InmueblesData>

    // 🔹 Obtener todos los inmuebles de un usuario
    @Query("SELECT * FROM INMUEBLES WHERE idUsuario = :idUsuario")
     fun getInmueblesDeUsuario(idUsuario: String): List<InmueblesData>

    // 🔹 Obtener todos los inmuebles excepto los del usuario actual
    @Query("SELECT * FROM INMUEBLES WHERE idUsuario != :idUsuario")
     fun getInmueblesExcluyendoUsuario(idUsuario: String): List<InmueblesData>

    // 🔹 Obtener un inmueble por id
    @Query("SELECT * FROM INMUEBLES WHERE idInmueble = :idInmueble LIMIT 1")
     fun getInmueble(idInmueble: Int): InmueblesData?

    // 🔹 Comprobar si existe un inmueble por id
    @Query("SELECT * FROM INMUEBLES WHERE idInmueble = :idInmueble LIMIT 1")
     fun existeInmueble(idInmueble: Int): InmueblesData?

    // 🔹 Insertar nuevo inmueble
    @Insert(onConflict = OnConflictStrategy.ABORT)
     fun nuevoInmueble(inmueble: InmueblesData)

    // 🔹 Actualizar inmueble existente
    @Update
     fun actualizaInmueble(inmueble: InmueblesData)

    // 🔹 Eliminar inmueble por id
    @Query("DELETE FROM INMUEBLES WHERE idInmueble = :idInmueble")
     fun eliminarInmueble(idInmueble: Int)
}
