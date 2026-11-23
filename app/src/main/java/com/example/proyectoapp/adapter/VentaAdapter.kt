package com.example.proyectoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoapp.databinding.ItemVentaBinding
import com.example.proyectoapp.model.Venta

class VentaAdapter(private var listaVentas: List<Venta>) :
    RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

    inner class VentaViewHolder(val binding: ItemVentaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val binding = ItemVentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VentaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {
        val venta = listaVentas[position]
        holder.binding.tvVentaProducto.text = "${venta.productoNombre} (x${venta.cantidad})"
        holder.binding.tvVentaFechaTotal.text = "${venta.fecha} | Total: $${String.format("%.2f", venta.total)}"
    }

    override fun getItemCount(): Int = listaVentas.size

    fun actualizarLista(nuevaLista: List<Venta>) {
        listaVentas = nuevaLista
        notifyDataSetChanged()
    }
}