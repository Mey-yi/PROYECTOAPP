package com.example.proyectoapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectoapp.model.Producto
import com.example.proyectoapp.model.Venta

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "papeleria.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE productos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                categoria TEXT NOT NULL,
                precio REAL NOT NULL,
                stock INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE ventas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                producto_nombre TEXT NOT NULL,
                cantidad INTEGER NOT NULL,
                total REAL NOT NULL,
                fecha TEXT NOT NULL
            )
        """)

        // Artículos iniciales para la papelería
        db.execSQL("INSERT INTO productos (nombre, categoria, precio, stock) VALUES ('Cuaderno Profesional Raya', 'Cuadernos', 38.50, 25)")
        db.execSQL("INSERT INTO productos (nombre, categoria, precio, stock) VALUES ('Bolígrafo Punto Medio Azul', 'Escritura', 8.00, 50)")
        db.execSQL("INSERT INTO productos (nombre, categoria, precio, stock) VALUES ('Paquete 100 Hojas Blancas', 'Papel', 24.00, 15)")
        db.execSQL("INSERT INTO productos (nombre, categoria, precio, stock) VALUES ('Caja 12 Colores Madera', 'Arte y Dibujo', 65.00, 10)")
        db.execSQL("INSERT INTO productos (nombre, categoria, precio, stock) VALUES ('Lápiz Adhesivo 20g', 'Pegamentos', 18.50, 20)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS productos")
        db.execSQL("DROP TABLE IF EXISTS ventas")
        onCreate(db)
    }

    fun insertarProducto(producto: Producto): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("categoria", producto.categoria)
            put("precio", producto.precio)
            put("stock", producto.stock)
        }
        return db.insert("productos", null, values)
    }

    fun obtenerProductos(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM productos ORDER BY nombre ASC", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Producto(
                        id = cursor.getInt(0),
                        nombre = cursor.getString(1),
                        categoria = cursor.getString(2),
                        precio = cursor.getDouble(3),
                        stock = cursor.getInt(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun actualizarProducto(producto: Producto): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("categoria", producto.categoria)
            put("precio", producto.precio)
            put("stock", producto.stock)
        }
        return db.update("productos", values, "id = ?", arrayOf(producto.id.toString()))
    }

    fun eliminarProducto(id: Int): Int {
        val db = writableDatabase
        return db.delete("productos", "id = ?", arrayOf(id.toString()))
    }

    fun registrarVenta(venta: Venta, productoId: Int, nuevoStock: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("producto_nombre", venta.productoNombre)
            put("cantidad", venta.cantidad)
            put("total", venta.total)
            put("fecha", venta.fecha)
        }
        val idVenta = db.insert("ventas", null, values)

        val stockValues = ContentValues().apply { put("stock", nuevoStock) }
        db.update("productos", stockValues, "id = ?", arrayOf(productoId.toString()))

        return idVenta
    }

    fun obtenerVentas(): List<Venta> {
        val lista = mutableListOf<Venta>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ventas ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Venta(
                        id = cursor.getInt(0),
                        productoNombre = cursor.getString(1),
                        cantidad = cursor.getInt(2),
                        total = cursor.getDouble(3),
                        fecha = cursor.getString(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun limpiarVentas() {
        writableDatabase.execSQL("DELETE FROM ventas")
    }
}