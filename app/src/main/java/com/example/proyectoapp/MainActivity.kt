package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoapp.database.DatabaseHelper
import com.example.proyectoapp.databinding.ActivityMainBinding
import com.example.proyectoapp.util.SesionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SesionManager.autenticado) {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.btnIrInventario.setOnClickListener {
            startActivity(Intent(this, Inventario::class.java))
        }

        binding.btnIrVentas.setOnClickListener {
            startActivity(Intent(this, Ventas::class.java))
        }

        binding.btnIrReportes.setOnClickListener {
            startActivity(Intent(this, Reportes::class.java))
        }

        binding.btnCerrarSesion.setOnClickListener {
            SesionManager.autenticado = false
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::binding.isInitialized) return
        binding.tvContadorProductos.text = dbHelper.obtenerProductos().size.toString()
        binding.tvContadorVentas.text = dbHelper.obtenerVentas().size.toString()
    }
}