package com.example.dogapp.view.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogapp.R
import com.example.dogapp.adapter.CitaAdapter
import com.example.dogapp.data.local.database.DatabaseProvider
import com.example.dogapp.databinding.FragmentHomeBinding
import com.example.dogapp.viewmodel.HomeViewModel

/**
 * HU 2.0 - Home Administrador de Citas (contenedor minimo).
 *
 * Une las dos subtareas de Medina: el CitaAdapter pinta la lista y el HomeViewModel
 * entrega el LiveData que viene de Room. El estado vacio se muestra cuando la tabla
 * no tiene filas.
 *
 * Pendiente de otras subtareas de la misma HU (no se tocan aqui):
 *  - header curvo con titulo, perro y marca            -> Santos
 *  - boton atras del Home -> finishAffinity()          -> Agudelo
 *  - boton flotante de nueva cita y navegacion al detalle
 */
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.fabrica(DatabaseProvider.getDatabase(requireContext()).citaDao())
    }

    private val adapter = CitaAdapter { cita ->
        // TODO: navegar al detalle de la cita (fuera de esta subtarea).
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.rvCitas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitas.adapter = adapter

        viewModel.citas.observe(viewLifecycleOwner) { citas ->
            adapter.submitList(citas)
            binding.tvVacio.isVisible = citas.isEmpty()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
