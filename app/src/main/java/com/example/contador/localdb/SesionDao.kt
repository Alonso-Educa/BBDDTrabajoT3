package com.example.contador.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SesionDao {

    //Iniciar sesion


    //Consultar inicio de sesión

    //esto no esta siendo usado vvv
    @Query("SELECT * FROM ${Estructura.Sesion.TABLE_NAME} LIMIT 1")
    fun getEstadoSesion(): SesionData?


    @Query("SELECT idUsuario FROM ${Estructura.Sesion.TABLE_NAME} LIMIT 1")
    fun getIdUsuarioSesion(): Int?

    @Query("SELECT * FROM ${Estructura.Usuario.TABLE_NAME} WHERE idUsuario = :id LIMIT 1")
    fun getUsuarioSesion(id: Int): UsuarioData?
// IDUSUARIO

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun nuevaSesion(sesion: SesionData)

//    @Query(
//        """
//        SELECT *
//        FROM ${Estructura.Usuario.TABLE_NAME} u
//        INNER JOIN ${Estructura.Sesion.TABLE_NAME} s
//            ON u.idUsuario = s.idUsuario
//            ORDER BY idSesion DESC
//        LIMIT 1
//    """
//    )
//    fun getUsuarioSesionActual(): UsuarioData?

    @Query(
        """
    DELETE FROM ${Estructura.Sesion.TABLE_NAME}
    WHERE idUsuario = :idUsuario
    """
    )
    suspend fun eliminarSesionUsuario(idUsuario: Int)

//    @Query("DELETE FROM sesion WHERE idUsuario = :idUsuario")
//    suspend fun eliminarSesionUsuario(idUsuario: Int)

    @Query("""
    SELECT *
    FROM usuarios u
    INNER JOIN SESIONES s ON u.idUsuario = s.idUsuario
    LIMIT 1
""")
    suspend fun getUsuarioSesionActual(): UsuarioData?

}