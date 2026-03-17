package com.example.contador.localdb

import androidx.room.*

@Dao
interface ContactosDao {

    // Operación CRUD 1: CREATE - Crear un nuevo contacto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun nuevoContacto(contacto: ContactosData)

    // Operación CRUD 2: READ - Obtener un contacto por id
    @Query("SELECT * FROM CONTACTOS WHERE idContacto = :id LIMIT 1")
    fun getContactoPorId(id: Int): ContactosData?

    // Operación CRUD 3: UPDATE - Actualizar un contacto
    @Update
    fun actualizarContacto(contacto: ContactosData)

    // Operación CRUD 4: DELETE - Eliminar un contacto
    @Delete
    fun borrarContacto(contacto: ContactosData)
}