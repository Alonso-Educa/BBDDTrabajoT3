package com.example.contador.localdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    // Indica que esta clase define una base de datos Room.
    entities = [UsuarioData::class,SesionData::class, AmistadData::class, InmueblesData::class, PublicacionesData::class, ProductosData::class],
    // La base de datos contiene una tabla representada por la entidad UsuarioData.
    // Si hubieran más tablas, se añadirían así:
    // [UsuariosData::class, SesionData::class]

    version = 1,
    // Versión actual de la BD. Se incrementa cuando se realizan cambios para manejar migraciones.

    exportSchema = true
    // Indica que se exportará un archivo con el esquema para mantener historial.
)
abstract class AppDB : RoomDatabase() {

    // Room genera la implementación automáticamente.
    abstract fun usuarioDao(): UsuarioDao
    abstract fun sesionDao(): SesionDao

    abstract fun amistadDao(): AmistadDao
    abstract fun inmueblesDao(): InmueblesDao
    abstract fun publicacionesDao(): PublicacionesDao
    abstract fun productosDao(): ProductosDao
}
