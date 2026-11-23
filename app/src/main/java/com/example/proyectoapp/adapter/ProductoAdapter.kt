package com.example.proyectoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapp.databinding.ItemProductoBinding
import com.example.proyectoapp.model.Producto

class ProductoAdapter(
    private var listaProductos: List<Producto>,
    private val onEditarClick: (Producto) -> Unit,
    private val onEliminarClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(val binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.binding.tvItemNombre.text = "${producto.nombre} (${producto.categoria})"
        holder.binding.tvItemDetalles.text = "Precio: $${String.format("%.2f", producto.precio)} | Stock: ${producto.stock}"

        holder.binding.btnItemEditar.setOnClickListener { onEditarClick(producto) }
        holder.binding.btnItemEliminar.setOnClickListener { onEliminarClick(producto) }
    }

    override fun getItemCount(): Int = listaProductos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        listaProductos = nuevaLista
        notifyDataSetChanged()
    }
}