package com.example.dogapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.dogapp.data.local.entity.CitaEntity
import com.example.dogapp.databinding.ItemCitaBinding

/**
 * HU 2.0 - Subtarea "RecyclerView + Adapter + ViewHolder" (Medina Hernan).
 *
 * ListAdapter con DiffUtil: cuando Room emite un cambio en la tabla de citas,
 * el Home se refresca solo (crear, editar o borrar) sin recargar la pantalla.
 */
class CitaAdapter(
    private val alHacerClic: (CitaEntity) -> Unit
) : ListAdapter<CitaEntity, CitaViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemCitaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CitaViewHolder(binding, alHacerClic)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.pintar(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CitaEntity>() {
            override fun areItemsTheSame(oldItem: CitaEntity, newItem: CitaEntity): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CitaEntity, newItem: CitaEntity): Boolean =
                oldItem == newItem
        }
    }
}
