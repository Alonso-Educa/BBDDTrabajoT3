package com.example.contador.localdb

import androidx.room.*

@Dao
interface ContactosDao {

    // Operación CRUD 1: CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun nuevoContacto(contacto: ContactosData)

    // Operación CRUD 2: READ - obtener un contacto por id
    @Query("SELECT * FROM CONTACTOS WHERE idContacto = :id LIMIT 1")
    fun getContactoPorId(id: Int): ContactosData?

    // Operación CRUD 2: READ - obtener todos los contactos de un usuario
    @Query("SELECT * FROM CONTACTOS WHERE idUsuario = :idUsuario")
    fun getContactosDeUsuario(idUsuario: String): List<ContactosData>

    // Operación CRUD 3: UPDATE
    @Update
    fun actualizarContacto(contacto: ContactosData)

    // Operación CRUD 4: DELETE
    @Delete
    fun borrarContacto(contacto: ContactosData)
}