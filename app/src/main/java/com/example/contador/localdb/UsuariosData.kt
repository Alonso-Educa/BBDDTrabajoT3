package com.example.contador.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = Estructura.Usuario.TABLE_NAME,
    indices = [Index(value = [Estructura.Usuario.EMAIL], unique = true)]
)
data class UsuarioData(

    @PrimaryKey val idUsuario: String = "", // antes era Int, ahora String para Firebase UID

    @ColumnInfo(name = Estructura.Usuario.NOMBRE) val nombreUsuario: String,
    @ColumnInfo(name = Estructura.Usuario.APELLIDOS) val apellidosUsuario: String,
    @ColumnInfo(name = Estructura.Usuario.INCORPORACION) val incorporacionUsuario: String,
    @ColumnInfo(name = Estructura.Usuario.EMAIL) val email: String,
    @ColumnInfo(name = Estructura.Usuario.SEXO) val sexo: String
)


@Entity(
    tableName = Estructura.Sesion.TABLE_NAME,
    foreignKeys = [ForeignKey(
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
    @PrimaryKey(autoGenerate = true) val idSesion: Int = 0,
    val idUsuario: String, // antes Int
    val fechaInicio: String
)


@Entity(
    tableName = "AMISTADES",
    indices = [Index(value = ["idUsuario1", "idUsuario2"], unique = true)]
)
data class AmistadData(
    @PrimaryKey(autoGenerate = true) val idAmistad: Int = 0,
    val idUsuario1: String,
    val idUsuario2: String
)

@Entity(
    tableName = "INMUEBLES",
    foreignKeys = [ForeignKey(
        entity = UsuarioData::class,
        parentColumns = ["idUsuario"],
        childColumns = ["idUsuario"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idUsuario")]
)
data class InmueblesData(
    @PrimaryKey(autoGenerate = true)
    val idInmueble: Int = 0,
    val idUsuario: String,
    val titulo: String,
    val descripcion: String,
    val urlImagen: String,
    val precio: Double,
    val tipo: String
)

@Entity(
    tableName = "PUBLICACIONES",
    foreignKeys = [ForeignKey(
        entity = UsuarioData::class,
        parentColumns = ["idUsuario"],
        childColumns = ["idUsuario"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idUsuario")]
)
data class PublicacionesData(
    @PrimaryKey(autoGenerate = true)
    val idPublicacion: Int = 0,
    val idUsuario: String,
    val titulo: String,
    val descripcion: String,
    val urlImagen: String,
    val nombreUsuario: String
)

@Entity(
    tableName = "PRODUCTOS",
    foreignKeys = [ForeignKey(
        entity = UsuarioData::class,
        parentColumns = ["idUsuario"],
        childColumns = ["idUsuario"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idUsuario")]
)
data class ProductosData(
    @PrimaryKey(autoGenerate = true)
    val idProducto: Int = 0,
    val idUsuario: String,
    val nombre: String,
    val descripcion: String,
    val urlImagen: String,
    val precio: Double,
    val porcentajePromocion: Double
)

@Entity(
    tableName = "CONTACTOS",
    foreignKeys = [ForeignKey(
        entity = UsuarioData::class,
        parentColumns = ["idUsuario"],
        childColumns = ["idUsuario"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idUsuario")]
)
data class ContactosData(
    @PrimaryKey(autoGenerate = false)
    val idContacto: Int = 0,
    val idUsuario: String,
    val nombre: String,
    val apellidos: String,
    val correo: String
)