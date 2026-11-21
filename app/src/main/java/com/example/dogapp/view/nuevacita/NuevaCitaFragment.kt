package com.example.dogapp.view.nuevacita

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dogapp.R
import com.example.dogapp.data.local.database.DatabaseProvider
import com.example.dogapp.data.remote.api.RetrofitClient
import com.example.dogapp.data.repository.CitaRepository
import com.example.dogapp.databinding.FragmentNuevaCitaBinding
import com.example.dogapp.viewmodel.NuevaCitaViewModel

class NuevaCitaFragment : Fragment(R.layout.fragment_nueva_cita) {

    private var _binding: FragmentNuevaCitaBinding? = null
    private val binding get() = _binding!!

    // Inicializamos el repositorio con la BD y la API
    private val repository: CitaRepository by lazy {
        CitaRepository(
            DatabaseProvider.getDatabase(requireContext()).citaDao(),
            RetrofitClient.dogCeoApi
        )
    }

    // ViewModel con la fábrica
    private val viewModel: NuevaCitaViewModel by viewModels {
        NuevaCitaViewModel.fabrica(repository)
    }

    // Lista de síntomas quemada en código (Criterio #6)
    private val sintomasLista = listOf(
        "Síntomas", // Opción por defecto
        "Solo duerme",
        "No come",
        "Fractura extremidad",
        "Tiene pulgas",
        "Tiene garrapatas",
        "Bota demasiado pelo"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNuevaCitaBinding.bind(view)

        configurarInterfaz()
        configurarAutocompletadoRaza()
        configurarSintomasDropdown()
        configurarValidaciones()
        observarViewModel()
    }

    private fun configurarInterfaz() {
        // Botón volver a Home (Criterio #1)
        binding.btnVolverNuevaCita.setOnClickListener {
            findNavController().navigateUp()
        }

        // Botón Guardar Cita
        binding.btnGuardarCita.setOnClickListener {
            val sintomaSeleccionado = binding.autoCompleteSintomas.text.toString()

            // Criterio #8: Si falta seleccionar síntoma, mostrar aviso
            if (sintomaSeleccionado.isEmpty() || sintomaSeleccionado == "Síntomas") {
                Toast.makeText(requireContext(), "Selecciona un síntoma", Toast.LENGTH_SHORT).show()
            } else {
                // Guardar la cita
                val nombre = binding.etNombreMascota.text.toString()
                val raza = binding.autoCompleteRaza.text.toString()
                val propietario = binding.etPropietario.text.toString()
                val telefono = binding.etTelefono.text.toString()

                viewModel.guardarCita(nombre, raza, propietario, telefono, sintomaSeleccionado)
            }
        }
    }

    //Configuración de autocompletado por prefijo y umbral 2
    private fun configurarAutocompletadoRaza() {
        // Criterio #3: Umbral de 2 letras
        binding.autoCompleteRaza.threshold = 2

        // Escuchar cuando el usuario escribe
        binding.autoCompleteRaza.doAfterTextChanged { text ->
            val query = text.toString().trim()
            viewModel.filtrarRazas(query)
            validarFormulario()
        }
    }

    private fun configurarSintomasDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sintomasLista)
        binding.autoCompleteSintomas.setAdapter(adapter)

        // Seleccionar opción por defecto "Síntomas"
        binding.autoCompleteSintomas.setText("Síntomas", false)

        binding.autoCompleteSintomas.setOnItemClickListener { _, _, _, _ ->
            validarFormulario()
        }
    }

    private fun configurarValidaciones() {
        binding.etNombreMascota.doAfterTextChanged { validarFormulario() }
        binding.etPropietario.doAfterTextChanged { validarFormulario() }
        binding.etTelefono.doAfterTextChanged { validarFormulario() }
    }

    // Criterio #7 y #9: Inactivo mientras falte nombre, raza, propietario o teléfono.
    private fun validarFormulario() {
        val nombre = binding.etNombreMascota.text.toString().trim()
        val raza = binding.autoCompleteRaza.text.toString().trim()
        val propietario = binding.etPropietario.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()

        val formularioValido = nombre.isNotEmpty() && raza.isNotEmpty() &&
                propietario.isNotEmpty() && telefono.isNotEmpty()

        binding.btnGuardarCita.isEnabled = formularioValido
    }

    private fun observarViewModel() {
        // Observar razas filtradas de Retrofit para el autocompletado
        viewModel.razasFiltradas.observe(viewLifecycleOwner) { listaRazas ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listaRazas)
            binding.autoCompleteRaza.setAdapter(adapter)
            adapter.notifyDataSetChanged()
        }

        // Criterio #10: Al guardar exitosamente, volver al Home (HU 2.0)
        viewModel.guardadoExitoso.observe(viewLifecycleOwner) { exitoso ->
            if (exitoso) {
                Toast.makeText(requireContext(), "Cita guardada con éxito", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { mensajeError ->
            mensajeError?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}