package com.example.contador.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = Estructura.Usuario.TABLE_NAME, indices = [Index(
        value = [Estructura.Usuario.EMAIL], unique = true
    )]
) // Marca la clase como una entidad asociada a una tabla; email será único.
data class UsuarioData(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = Estructura.Usuario.IDUSUARIO) val idUsuario: Int = 0,
    // Clave primaria autogenerada. El 0 permite crear el objeto sin asignar valor.

    @ColumnInfo(name = Estructura.Usuario.NOMBRE) val nombreUsuario: String,
    // Nombre del usuario.

    @ColumnInfo(name = Estructura.Usuario.APELLIDOS) val apellidosUsuario: String,
    // Apellidos del usuario.

    @ColumnInfo(name = Estructura.Usuario.INCORPORACION) val incorporacionUsuario: String,
    // Fecha de incorporación.

    @ColumnInfo(name = Estructura.Usuario.EMAIL) val email: String,
    // Email del usuario (con índice único).

    @ColumnInfo(name = Estructura.Usuario.SEXO) val sexo: String
)

@Entity(
    tableName = Estructura.Sesion.TABLE_NAME, foreignKeys = [ForeignKey(
        // Crea una relación entre esta entidad y la tabla UsuarioData.
        entity = UsuarioData::class,
        // Define el campo de la tabla padre (UsuarioData) que será referenciado.
        parentColumns = [Estructura.Usuario.IDUSUARIO],
        // Define el campo de esta entidad que contendrá la clave foránea.
        childColumns = [Estructura.Sesion.IDUSUARIO],
        // Si se elimina un usuario en la tabla UsuarioData, todas las sesiones
        // relacionadas en la tabla de sesiones también se eliminarán automáticamente (borrado en cascada).
        onDelete = ForeignKey.CASCADE
    )]
)
data class SesionData(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = Estructura.Sesion.IDSESION) val idSesion: Int = 0,
    // Clave primaria autogenerada, generando valores únicos incrementales.

    @ColumnInfo(name = Estructura.Sesion.IDUSUARIO) val idUsuario: Int,
    // Clave foránea hacia UsuarioData.idUsuario.

    @ColumnInfo(name = Estructura.Sesion.FECHA_INICIO) val fechaInicio: String
    // Momento en que se inició la sesión.
)

@Entity(
    tableName = "AMISTADES", indices = [Index(value = ["idUsuario1", "idUsuario2"], unique = true)]
)
data class AmistadData(
    @PrimaryKey(autoGenerate = true) val idAmistad: Int = 0,
    val idUsuario1: Int,
    val idUsuario2: Int
)

@Entity(
    tableName = "INMUEBLES", indices = [Index(value = ["idInmueble"], unique = true)]
)
data class InmueblesData(
    @PrimaryKey(autoGenerate = true) val idInmueble: Int = 0,
    // Clave foránea hacia UsuarioData.idUsuario.
    @ColumnInfo(name = Estructura.Inmuebles.IDUSUARIO) val idUsuario: Int,
    @ColumnInfo(name = Estructura.Inmuebles.TITULO) val titulo: String,
    @ColumnInfo(name = Estructura.Inmuebles.DESCRIPCION) val descripcion: String,
    @ColumnInfo(name = Estructura.Inmuebles.URLIMAGEN) val urlImagen: String,
    @ColumnInfo(name = Estructura.Inmuebles.PRECIO) val precio: Double,
    @ColumnInfo(name = Estructura.Inmuebles.TIPO) val tipo: String
)

