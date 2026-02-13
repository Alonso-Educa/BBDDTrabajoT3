package com.example.contador.localdb

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Dao

@Dao
interface ProductosDao {
    // 🔹 Obtener todos los productos de todos los usuarios
    @Query("SELECT * FROM PRODUCTOS")
    fun getListaProductosTodos(): List<ProductosData>

    // 🔹 Obtener todos los productos de un usuario
    @Query("SELECT * FROM PRODUCTOS WHERE idUsuario = :idUsuario")
    fun getProductosDeUsuario(idUsuario: String): List<ProductosData>

    // 🔹 Obtener todos los productos excepto los del productos actual
    @Query("SELECT * FROM PRODUCTOS WHERE idUsuario != :idUsuario")
    fun getProductosExcluyendoUsuario(idUsuario: String): List<ProductosData>

    // 🔹 Obtener un productos por id
    @Query("SELECT * FROM PRODUCTOS WHERE idProducto = :idProducto LIMIT 1")
    fun getProducto(idProducto: Int): ProductosData?

    // 🔹 Comprobar si existe un productos por id
    @Query("SELECT * FROM PRODUCTOS WHERE idProducto = :idProducto LIMIT 1")
    fun existeProducto(idProducto: Int): ProductosData?

    // 🔹 Insertar nuevo productos
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun nuevoProducto(producto: ProductosData)

    // 🔹 Actualizar productos existente
    @Update
    fun actualizaProducto(producto: ProductosData)

    // 🔹 Eliminar productos por id
    @Query("DELETE FROM PRODUCTOS WHERE idProducto = :idProducto")
    fun eliminarProducto(idProducto: Int)
}