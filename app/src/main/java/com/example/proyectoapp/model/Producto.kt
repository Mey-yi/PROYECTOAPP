package com.example.proyectoapp.model

class Producto (
    val id: Int=0,
    val nombre:String,
    val categoria: String,
    val precio: Double,
    val stock: Int
){
    override fun toString(): String {
        return "$nombre ($${String.format("%.2f", precio)} - Disp: $stock)"
    }
}