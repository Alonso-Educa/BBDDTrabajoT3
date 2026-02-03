package com.example.contador.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface InmueblesDao {

    // 🔹 Insertar inmueble nuevo
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun nuevoInmueble(inmueble: InmueblesData)

    // 🔹 Actualizar inmueble existente
    @Update
    fun actualizaInmueble(inmueble: InmueblesData)

    // 🔹 Eliminar inmueble
    @Query(
        """
    DELETE FROM ${Estructura.Inmuebles.TABLE_NAME}
    WHERE idInmueble = :idInmueble
    """
    )
    suspend fun eliminarInmueble(idInmueble: Int)

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun getInmueble(id: Int): UsuarioData?

    // Obtener todos los inmuebles de un usuario
    @Query("SELECT * FROM INMUEBLES  WHERE idInmueble != :idInmueble")
    suspend fun getInmueblesUsuario(idInmueble: Int): List<InmueblesData>

    // Obtener todos los inmuebles de todos los usuariso
    @Query("SELECT * FROM INMUEBLES")
    suspend fun getListaInmueblesTodos(): List<InmueblesData>

    @Query(
        """
        SELECT * FROM INMUEBLES
        WHERE idInmueble = :idInmueble
        LIMIT 1
    """
    )
    suspend fun existeInmueble(idInmueble: Int): InmueblesData?

}