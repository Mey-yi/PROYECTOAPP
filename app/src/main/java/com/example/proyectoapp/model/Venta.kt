package com.example.proyectoapp.model

data class Venta(
    val id: Int = 0,
    val productoNombre: String,
    val cantidad: Int,
    val total: Double,
    val fecha: String
)