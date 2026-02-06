package com.example.contador.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SesionDao {

    // 🔹 Obtener estado de sesión (no usado actualmente)
    @Query("SELECT * FROM ${Estructura.Sesion.TABLE_NAME} LIMIT 1")
    fun getEstadoSesion(): SesionData?

    // 🔹 Obtener id de usuario de la sesión (ahora String)
    @Query("SELECT idUsuario FROM ${Estructura.Sesion.TABLE_NAME} LIMIT 1")
    fun getIdUsuarioSesion(): String?

    // 🔹 Obtener usuario de la sesión (ahora String)
    @Query("SELECT * FROM ${Estructura.Usuario.TABLE_NAME} WHERE idUsuario = :id LIMIT 1")
    fun getUsuarioSesion(id: String): UsuarioData?

    // 🔹 Iniciar nueva sesión
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun nuevaSesion(sesion: SesionData)

    // 🔹 Eliminar sesión de un usuario (idUsuario como String)
    @Query(
        """
    DELETE FROM ${Estructura.Sesion.TABLE_NAME}
    WHERE idUsuario = :idUsuario
    """
    )
    suspend fun eliminarSesionUsuario(idUsuario: String)

    // 🔹 Obtener usuario actual de sesión (join)
    @Query("""
    SELECT *
    FROM ${Estructura.Usuario.TABLE_NAME} u
    INNER JOIN ${Estructura.Sesion.TABLE_NAME} s ON u.idUsuario = s.idUsuario
    LIMIT 1
""")
    suspend fun getUsuarioSesionActual(): UsuarioData?

}