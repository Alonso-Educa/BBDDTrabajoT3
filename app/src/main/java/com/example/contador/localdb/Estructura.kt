package com.example.contador.localdb


class Estructura {
    object DB {
        const val NAME = "DBUsuarios.db"
    }

    object Usuario {
        const val TABLE_NAME = "USUARIOS"
        const val IDUSUARIO = "idUsuario"
        const val NOMBRE = "nombreUsuario"
        const val APELLIDOS = "apellidosUsuario"
        const val INCORPORACION = "incorporacionUsuario"
        const val EMAIL = "email"
        const val SEXO = "sexo"
    }
    object Sesion {
        const val TABLE_NAME = "SESIONES"
        const val IDSESION = "idSesion"
        const val IDUSUARIO = "idUsuario" // Clave foránea que apunta a la tabla USUARIOS
        const val FECHA_INICIO = "fechaInicio"
    }

    object Amistad {
        const val TABLE_NAME = "AMISTADES"
        const val IDAMISTAD = "idAmistad"
        const val IDUSUARIO1 = "idUsuario1"
        const val IDUSUARIO2 = "idUsuario2"
    }

    object Inmuebles {
        const val TABLE_NAME = "INMUEBLES"
        const val IDINMUEBLE = "idInmueble"
        const val IDUSUARIO = "idUsuario"
        const val TITULO = "titulo"
        const val DESCRIPCION = "descripcion"
        const val URLIMAGEN = "urlImagen"
        const val PRECIO = "precio"
        const val TIPO = "tipo"
    }

    object Publicaciones {
        const val TABLE_NAME = "PUBLICACIONES"
        const val IDINMUEBLE = "idPublicacion"
        const val IDUSUARIO = "idUsuario"
        const val NOMBREUSUARIO = "nombreUsuario"
        const val TITULO = "titulo"
        const val DESCRIPCION = "descripcion"
        const val URLIMAGEN = "urlImagen"
    }
}