package com.example.contador.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AmistadDao {

    // 🔹 Obtener todos los usuarios
    @Query("SELECT * FROM AMISTADES  WHERE idUsuario1 != :idAmigo")
    suspend fun getAmistadUsuario(idAmigo: Int): List<AmistadData>

    // 🔹 Insertar usuario nuevo
    @Insert
    suspend fun nuevaAmistad(amistad: AmistadData)

    @Query(
        """
        DELETE FROM AMISTADES
        WHERE idUsuario1 = :idUsuario1
        AND idUsuario2 = :idUsuario2
    """
    )
    suspend fun eliminarAmistad(
        idUsuario1: Int,
        idUsuario2: Int
    )

    @Query(
        """
        SELECT *
        FROM ${Estructura.Usuario.TABLE_NAME} u
        INNER JOIN ${Estructura.Sesion.TABLE_NAME} s
            ON u.idUsuario = s.idUsuario
            ORDER BY idUsuario DESC
        LIMIT 1
    """
    )
    suspend fun getUsuarioSesionActual(): UsuarioData?

    @Query(
        """
        SELECT * FROM AMISTADES
        WHERE idUsuario1 = :idUsuario
        AND idUsuario2 = :idAmigo
        LIMIT 1
    """
    )
    suspend fun existeAmistad(idUsuario: Int, idAmigo: Int): AmistadData?
}

