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
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController

/**
 * HU 2.0 - Home Administrador de Citas.
 *
 * - Header curvo con título, perro y marca (Criterio 2) → layout/header_home.xml
 * - Back button del teléfono → finishAffinity() (Criterio 1)
 * - FAB rosa → Nueva Cita (HU 3.0)
 * - Click item → Detalle Cita (HU 4.0)
 * - Lista desde Room via HomeViewModel + CitaAdapter
 */
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.fabrica(DatabaseProvider.getDatabase(requireContext()).citaDao())
    }

    private val adapter = CitaAdapter { cita ->
        val bundle = androidx.core.os.bundleOf("citaId" to cita.id)
        try {
            // Flujo oficial con Navigation Component (MainActivity)
            androidx.navigation.fragment.NavHostFragment.findNavController(this)
                .navigate(R.id.detalleCitaFragment, bundle)
        } catch (_: Exception) {
            // Respaldo para HomePreviewActivity
            val containerId = (requireView().parent as? android.view.View)?.id ?: return@CitaAdapter
            parentFragmentManager.beginTransaction()
                .replace(containerId, com.example.dogapp.view.detalle.DetalleCitaFragment.newInstance(cita.id))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Criterio 1: Back button del teléfono → finishAffinity() (va al escritorio, no al login)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
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

        // FAB Nueva Cita → HU 3.0
        binding.fabNuevaCita.setOnClickListener {
            findNavController().navigate(R.id.nuevaCitaFragment)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}