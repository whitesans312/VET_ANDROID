package com.example.dogapp.view.detalle

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.dogapp.R
import com.example.dogapp.data.local.database.DatabaseProvider
import com.example.dogapp.data.local.entity.CitaEntity
import com.example.dogapp.databinding.FragmentDetalleCitaBinding
import com.example.dogapp.viewmodel.DetalleViewModel

/**
 * HU 4.0 - Ventana Detalle de la Cita.
 *
 * Recibe solo el ID de la cita (argumento "citaId", el mismo nombre que usaria Safe Args),
 * lo consulta a traves del [DetalleViewModel] y pinta el detalle.
 *
 * - FAB izquierdo: elimina la cita de Room y vuelve al Home (sin dialogo de confirmacion).
 * - FAB derecho: abre HU 5.0 (Editar) con el mismo ID.
 */
class DetalleCitaFragment : Fragment(R.layout.fragment_detalle_cita) {

    private var _binding: FragmentDetalleCitaBinding? = null
    private val binding get() = _binding!!

    private val citaId: Long by lazy { requireArguments().getLong(ARG_CITA_ID) }

    private val viewModel: DetalleViewModel by viewModels {
        DetalleViewModel.fabrica(DatabaseProvider.getDatabase(requireContext()).citaDao(), citaId)
    }

    private val navegacion: DetalleCitaNavegacion?
        get() = activity as? DetalleCitaNavegacion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetalleCitaBinding.bind(view)

        ajustarBarrasDelSistema()
        configurarBotones()
        observarViewModel()
    }

    private fun configurarBotones() {
        binding.btnVolver.setOnClickListener { volverAlHome() }
        binding.fabEliminar.setOnClickListener { viewModel.eliminar() }
        binding.fabEditar.setOnClickListener { navegacion?.irAEditarCita(citaId) }
    }

    private fun observarViewModel() {
        viewModel.cita.observe(viewLifecycleOwner) { cita ->
            when {
                cita != null -> pintar(cita)
                !viewModel.eliminando -> {
                    Toast.makeText(requireContext(), R.string.detalle_no_encontrada, Toast.LENGTH_SHORT).show()
                    volverAlHome()
                }
            }
        }
        viewModel.citaEliminada.observe(viewLifecycleOwner) { eliminada ->
            if (eliminada) volverAlHome()
        }
    }

    private fun pintar(cita: CitaEntity) {
        binding.tvTituloToolbar.text = cita.nombreMascota
        binding.tvTurno.text = getString(R.string.home_turno, cita.id)
        binding.tvRaza.text = cita.raza
        binding.tvSintoma.text = cita.sintoma
        binding.tvPropietario.text = cita.nombrePropietario
        binding.tvTelefono.text = cita.telefono

        Glide.with(binding.root)
            .load(cita.imagenUrl)
            .placeholder(R.drawable.ic_dog_head)
            .error(R.drawable.ic_dog_head)
            .centerCrop()
            .into(binding.ivMascota)
    }

    private fun volverAlHome() {
        navegacion?.volverAlHome() ?: parentFragmentManager.popBackStack()
    }

    /** Con edge-to-edge la toolbar y los FAB quedarian bajo las barras del sistema. */
    private fun ajustarBarrasDelSistema() {
        val paddingSuperiorBase = binding.toolbarDetalle.paddingTop
        val margenInferiorBase = (binding.fabEliminar.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootDetalle) { _, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbarDetalle.updatePadding(top = paddingSuperiorBase + barras.top)
            listOf(binding.fabEliminar, binding.fabEditar).forEach { fab ->
                fab.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = margenInferiorBase + barras.bottom
                }
            }
            insets
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_CITA_ID = "citaId"

        fun newInstance(citaId: Long) = DetalleCitaFragment().apply {
            arguments = bundleOf(ARG_CITA_ID to citaId)
        }
    }
}
