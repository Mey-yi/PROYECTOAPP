package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectoapp.adapter.ProductoAdapter
import com.example.proyectoapp.database.DatabaseHelper
import com.example.proyectoapp.databinding.ActivityInventarioBinding
import com.example.proyectoapp.model.Producto

class Inventario : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ProductoAdapter
    private var listaCompleta: List<Producto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        binding.rvProductos.layoutManager = LinearLayoutManager(this)

        adapter = ProductoAdapter(
            listaProductos = emptyList(),
            onEditarClick = { producto ->
                val intent = Intent(this, Formulario::class.java)
                intent.putExtra("ID", producto.id)
                intent.putExtra("NOMBRE", producto.nombre)
                intent.putExtra("CATEGORIA", producto.categoria)
                intent.putExtra("PRECIO", producto.precio)
                intent.putExtra("STOCK", producto.stock)
                startActivity(intent)
            },
            onEliminarClick = { producto ->
                confirmarEliminacion(producto)
            }
        )
        binding.rvProductos.adapter = adapter

        binding.btnNuevoProducto.setOnClickListener {
            startActivity(Intent(this, Formulario::class.java))
        }

        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtro = s.toString().lowercase()
                val filtrados = listaCompleta.filter {
                    it.nombre.lowercase().contains(filtro) || it.categoria.lowercase().contains(filtro)
                }
                adapter.actualizarLista(filtrados)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }

    private fun cargarProductos() {
        listaCompleta = dbHelper.obtenerProductos()
        adapter.actualizarLista(listaCompleta)
    }

    private fun confirmarEliminacion(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Artículo")
            .setMessage("¿Seguro que deseas eliminar '${producto.nombre}' del inventario?")
            .setPositiveButton("Eliminar") { _, _ ->
                dbHelper.eliminarProducto(producto.id)
                cargarProductos()
                Toast.makeText(this, "Artículo eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}