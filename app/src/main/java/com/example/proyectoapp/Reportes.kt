package com.example.proyectoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectoapp.adapter.VentaAdapter
import com.example.proyectoapp.database.DatabaseHelper
import com.example.proyectoapp.databinding.ActivityReportesBinding

class Reportes : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: VentaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        binding.rvVentas.layoutManager = LinearLayoutManager(this)

        adapter = VentaAdapter(emptyList())
        binding.rvVentas.adapter = adapter

        cargarHistorial()

        binding.btnCorteCaja.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Corte de Caja")
                .setMessage("¿Deseas cerrar el turno y reiniciar el historial de ventas?")
                .setPositiveButton("Sí, hacer corte") { _, _ ->
                    dbHelper.limpiarVentas()
                    cargarHistorial()
                    Toast.makeText(this, "Corte de caja realizado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun cargarHistorial() {
        val ventas = dbHelper.obtenerVentas()
        adapter.actualizarLista(ventas)
        val totalIngresos = ventas.sumOf { it.total }
        binding.tvIngresosTotales.text = "Ingresos Totales: $${String.format("%.2f", totalIngresos)}"
    }
}