package com.example.proyectoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoapp.database.DatabaseHelper
import com.example.proyectoapp.databinding.ActivityFormularioBinding
import com.example.proyectoapp.model.Producto

class Formulario : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioBinding
    private lateinit var dbHelper: DatabaseHelper
    private var productoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        if (intent.hasExtra("ID")) {
            productoId = intent.getIntExtra("ID", 0)
            binding.tvTituloForm.text = "Editar Artículo"
            binding.etNombreProducto.setText(intent.getStringExtra("NOMBRE"))
            binding.etCategoriaProducto.setText(intent.getStringExtra("CATEGORIA"))
            binding.etPrecioProducto.setText(intent.getDoubleExtra("PRECIO", 0.0).toString())
            binding.etStockProducto.setText(intent.getIntExtra("STOCK", 0).toString())
        }

        binding.btnGuardarProducto.setOnClickListener {
            guardarDatos()
        }
    }

    private fun guardarDatos() {
        val nombre = binding.etNombreProducto.text.toString().trim()
        val categoria = binding.etCategoriaProducto.text.toString().trim()
        val precioStr = binding.etPrecioProducto.text.toString().trim()
        val stockStr = binding.etStockProducto.text.toString().trim()

        if (nombre.isEmpty() || categoria.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioStr.toDoubleOrNull()
        val stock = stockStr.toIntOrNull()

        if (precio == null || precio <= 0) {
            binding.etPrecioProducto.error = "Ingresa un precio válido mayor a 0"
            return
        }

        if (stock == null || stock < 0) {
            binding.etStockProducto.error = "Ingresa un stock válido"
            return
        }

        val producto = Producto(productoId, nombre, categoria, precio, stock)

        if (productoId == 0) {
            dbHelper.insertarProducto(producto)
            Toast.makeText(this, "Artículo agregado", Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.actualizarProducto(producto)
            Toast.makeText(this, "Artículo actualizado", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}