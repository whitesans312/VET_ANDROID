package com.example.dogapp.view.editarcita

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.dogapp.R
import com.example.dogapp.data.local.database.DatabaseProvider
import com.example.dogapp.data.local.entity.CitaEntity
import com.example.dogapp.data.remote.api.RetrofitClient
import com.example.dogapp.data.remote.model.RazaMascota
import com.example.dogapp.data.repository.CitaRepository
import com.example.dogapp.databinding.FragmentEditarCitaBinding
import com.example.dogapp.view.detalle.DetalleCitaNavegacion
import com.example.dogapp.viewmodel.EditarCitaViewModel

class EditarCitaFragment : Fragment(R.layout.fragment_editar_cita) {

    private var _binding: FragmentEditarCitaBinding? = null
    private val binding get() = _binding!!

    private val citaId: Long by lazy {
        requireArguments().getLong(ARG_CITA_ID)
    }

    private val repository: CitaRepository by lazy {
        CitaRepository(
            DatabaseProvider.getDatabase(requireContext()).citaDao(),
            RetrofitClient.dogCeoApi
        )
    }

    private val viewModel: EditarCitaViewModel by viewModels {
        EditarCitaViewModel.fabrica(repository, citaId)
    }

    private var citaOriginal: CitaEntity? = null
    private var razasDisponibles: List<RazaMascota> = emptyList()
    private var razaSeleccionada: RazaMascota? = null
    private var imagenActual: String? = null
    private var datosPrecargados = false

    private val navegacion: DetalleCitaNavegacion?
        get() = activity as? DetalleCitaNavegacion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEditarCitaBinding.bind(view)

        configurarInterfaz()
        configurarEventos()
        observarViewModel()
    }

    private fun configurarInterfaz() {
        binding.tvTituloEditar.text = getString(R.string.editar_titulo)
        binding.actvRaza.threshold = 2
        actualizarEstadoBoton(false)
    }


    private fun configurarEventos() {

        binding.btnVolverEditar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.etNombreMascota.doAfterTextChanged {
            binding.tilNombreMascota.error = null
            validarFormulario()
        }

        binding.etPropietario.doAfterTextChanged {
            binding.tilPropietario.error = null
            validarFormulario()
        }

        binding.etTelefono.doAfterTextChanged {
            binding.tilTelefono.error = null
            validarFormulario()
        }

        binding.actvRaza.doAfterTextChanged { texto ->
            binding.tilRaza.error = null
            val entrada = texto?.toString()?.trim().orEmpty()
            val coincidencia = razasDisponibles.firstOrNull {
                it.nombre.equals(entrada, ignoreCase = true)
            }
            if (coincidencia != null && coincidencia != razaSeleccionada) {
                razaSeleccionada = coincidencia
                viewModel.buscarImagenRaza(coincidencia)
            } else if (coincidencia == null) {
                razaSeleccionada = null
            }
            validarFormulario()
        }

        binding.actvRaza.setOnItemClickListener { parent, _, position, _ ->
            val nombreSeleccionado = parent.getItemAtPosition(position)?.toString()
            razaSeleccionada = razasDisponibles.firstOrNull {
                it.nombre.equals(nombreSeleccionado, ignoreCase = true)
            }
            binding.tilRaza.error = null
            razaSeleccionada?.let { raza ->
                viewModel.buscarImagenRaza(raza)
            }
            validarFormulario()
        }

        binding.btnEditarCita.setOnClickListener {
            guardarCambios()
        }
    }

    private fun observarViewModel() {

        viewModel.cita.observe(viewLifecycleOwner) { cita ->

            if (cita == null) {
                Toast.makeText(
                    requireContext(),
                    "No se encontró la cita",
                    Toast.LENGTH_SHORT
                ).show()

                volverAlHome()
                return@observe
            }

            citaOriginal = cita

            if (!datosPrecargados) {
                precargarDatos(cita)
                datosPrecargados = true
            }
        }

        viewModel.razas.observe(viewLifecycleOwner) { razas ->

            razasDisponibles = razas

            val nombres = razas.map { it.nombre }

            val adaptador = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nombres
            )

            binding.actvRaza.setAdapter(adaptador)

            val razaActual = citaOriginal?.raza

            if (razaActual != null) {

                razaSeleccionada = razas.firstOrNull {
                    it.nombre.equals(razaActual, ignoreCase = true)
                }

                if (razaSeleccionada != null) {
                    binding.actvRaza.setText(
                        razaSeleccionada?.nombre,
                        false
                    )
                }

                validarFormulario()
            }
        }

        viewModel.imagenUrl.observe(viewLifecycleOwner) { url ->

            if (!url.isNullOrBlank()) {

                imagenActual = url

                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_dog_head)
                    .error(R.drawable.ic_dog_head)
                    .centerCrop()
                    .into(binding.ivRazaEditar)
            }
        }

        viewModel.guardado.observe(viewLifecycleOwner) { resultado ->

            if (resultado == true) {

                Toast.makeText(
                    requireContext(),
                    R.string.editar_exito,
                    Toast.LENGTH_SHORT
                ).show()

                volverAlHome()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { mensaje ->

            if (!mensaje.isNullOrBlank()) {

                Toast.makeText(
                    requireContext(),
                    mensaje,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun precargarDatos(cita: CitaEntity) {

        binding.etNombreMascota.setText(cita.nombreMascota)
        binding.etPropietario.setText(cita.nombrePropietario)
        binding.etTelefono.setText(cita.telefono)

        binding.actvRaza.setText(cita.raza, false)

        imagenActual = cita.imagenUrl

        if (!cita.imagenUrl.isNullOrBlank()) {

            Glide.with(this)
                .load(cita.imagenUrl)
                .placeholder(R.drawable.ic_dog_head)
                .error(R.drawable.ic_dog_head)
                .centerCrop()
                .into(binding.ivRazaEditar)
        }

        validarFormulario()
    }

    private fun actualizarEstadoBoton(habilitado: Boolean) {
        binding.btnEditarCita.isEnabled = habilitado
        val colorFondo = if (habilitado) R.color.editar_rosa else R.color.editar_rosa_deshabilitado
        binding.btnEditarCita.backgroundTintList =
            androidx.core.content.ContextCompat.getColorStateList(requireContext(), colorFondo)
        binding.btnEditarCita.setTextColor(
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.blanco)
        )
        binding.btnEditarCita.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun validarFormulario() {
        val original = citaOriginal

        val nombre = binding.etNombreMascota.text?.toString()?.trim().orEmpty()
        val propietario = binding.etPropietario.text?.toString()?.trim().orEmpty()
        val telefono = binding.etTelefono.text?.toString()?.trim().orEmpty()
        val razaTexto = binding.actvRaza.text?.toString()?.trim().orEmpty()

        val camposDiligenciados =
            nombre.isNotBlank() && nombre.length <= 15 &&
                    razaTexto.isNotBlank() &&
                    propietario.isNotBlank() && propietario.length <= 30 &&
                    telefono.isNotBlank() && telefono.length <= 10 && telefono.all { it.isDigit() }

        val hayCambios = original != null && (
                nombre != original.nombreMascota.trim() ||
                        !razaTexto.equals(original.raza.trim(), ignoreCase = true) ||
                        propietario != original.nombrePropietario.trim() ||
                        telefono != original.telefono.trim()
                )

        actualizarEstadoBoton(camposDiligenciados && hayCambios)
    }


    private fun guardarCambios() {

        val cita = citaOriginal ?: return

        val nombre = binding.etNombreMascota.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val propietario = binding.etPropietario.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val telefono = binding.etTelefono.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val razaNombre = razaSeleccionada?.nombre
            ?: binding.actvRaza.text?.toString()?.trim().orEmpty()

        if (nombre.isBlank()) {
            binding.tilNombreMascota.error =
                getString(R.string.editar_error_nombre)
            return
        }

        if (razaNombre.isBlank()) {
            binding.tilRaza.error =
                getString(R.string.editar_error_raza)
            return
        }

        if (propietario.isBlank()) {
            binding.tilPropietario.error =
                getString(R.string.editar_error_propietario)
            return
        }

        if (telefono.isBlank() ||
            telefono.length > 10 ||
            !telefono.all { it.isDigit() }
        ) {
            binding.tilTelefono.error =
                getString(R.string.editar_error_telefono)
            return
        }

        val citaActualizada = cita.copy(
            nombreMascota = nombre,
            raza = razaNombre,
            nombrePropietario = propietario,
            telefono = telefono,
            imagenUrl = imagenActual ?: cita.imagenUrl
        )

        binding.btnEditarCita.isEnabled = false

        viewModel.actualizarCita(citaActualizada)
    }


    private fun volverAlHome() {
        navegacion?.volverAlHome()
            ?: parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        const val ARG_CITA_ID = "citaId"

        fun newInstance(citaId: Long) =
            EditarCitaFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CITA_ID, citaId)
                }
            }
    }
}