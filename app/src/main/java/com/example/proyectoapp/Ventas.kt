package com.example.proyectoapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoapp.database.DatabaseHelper
import com.example.proyectoapp.databinding.ActivityVentasBinding
import com.example.proyectoapp.model.Producto
import com.example.proyectoapp.model.Venta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Ventas : AppCompatActivity() {

    private lateinit var binding: ActivityVentasBinding
    private lateinit var dbHelper: DatabaseHelper
    private var listaProductos: List<Producto> = emptyList()
    private var productoSeleccionado: Producto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVentasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        cargarSpinner()

        binding.etCantidadVenta.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcularTotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnConfirmarVenta.setOnClickListener {
            realizarVenta()
        }
    }

    private fun cargarSpinner() {
        listaProductos = dbHelper.obtenerProductos().filter { it.stock > 0 }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaProductos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spProductosVenta.adapter = adapter

        binding.spProductosVenta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                productoSeleccionado = listaProductos[position]
                calcularTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun calcularTotal() {
        val prod = productoSeleccionado ?: return
        val cantidad = binding.etCantidadVenta.text.toString().toIntOrNull() ?: 0
        val total = prod.precio * cantidad
        binding.tvTotalVenta.text = "Total a pagar: $${String.format("%.2f", total)}"
    }

    private fun realizarVenta() {
        val prod = productoSeleccionado
        if (prod == null) {
            Toast.makeText(this, "No hay artículos con stock disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidad = binding.etCantidadVenta.text.toString().toIntOrNull()
        if (cantidad == null || cantidad <= 0) {
            binding.etCantidadVenta.error = "Ingresa una cantidad válida"
            return
        }

        if (cantidad > prod.stock) {
            binding.etCantidadVenta.error = "Stock insuficiente (Disponibles: ${prod.stock})"
            return
        }

        val total = prod.precio * cantidad
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val venta = Venta(0, prod.nombre, cantidad, total, fechaActual)

        dbHelper.registrarVenta(venta, prod.id, prod.stock - cantidad)
        Toast.makeText(this, "Venta registrada con éxito", Toast.LENGTH_SHORT).show()
        finish()
    }
}