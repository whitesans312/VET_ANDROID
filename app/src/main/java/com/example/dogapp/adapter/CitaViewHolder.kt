package com.example.dogapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogapp.R
import com.example.dogapp.data.local.entity.CitaEntity
import com.example.dogapp.databinding.ItemCitaBinding

/**
 * HU 2.0 - Subtarea "RecyclerView + Adapter + ViewHolder" (Medina Hernan).
 *
 * Pinta una cita del Home: avatar circular con Glide, nombre de la mascota,
 * sintoma y numero de turno (el id autogenerado por Room).
 * Sin modelo de dominio todavia: se enlaza la entidad de Room directamente y la
 * logica de presentacion vive aqui, no en el layout.
 */
class CitaViewHolder(
    private val binding: ItemCitaBinding,
    private val alHacerClic: (CitaEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun pintar(cita: CitaEntity) {
        binding.tvNombreMascota.text = cita.nombreMascota
        binding.tvSintoma.text = cita.sintoma
        binding.tvTurno.text = binding.root.context.getString(R.string.home_turno, cita.id)

        Glide.with(binding.root)
            .load(cita.imagenUrl)
            .placeholder(R.drawable.ic_dog_head)
            .error(R.drawable.ic_dog_head)
            .centerCrop()
            .into(binding.ivMascota)

        binding.cardCita.setOnClickListener { alHacerClic(cita) }
    }
}
