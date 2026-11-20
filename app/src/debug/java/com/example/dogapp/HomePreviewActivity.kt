package com.example.dogapp

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.dogapp.data.local.database.DatabaseProvider
import com.example.dogapp.data.local.entity.CitaEntity
import com.example.dogapp.view.detalle.DetalleCitaNavegacion
import com.example.dogapp.view.editarcita.EditarCitaFragment
import com.example.dogapp.view.home.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomePreviewActivity : AppCompatActivity(), DetalleCitaNavegacion {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contenedor = FrameLayout(this).apply {
            id = ID_CONTENEDOR
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(contenedor)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(ID_CONTENEDOR, HomeFragment())
                .commit()
            sembrarCitasDeEjemplo()
        }
    }

    override fun volverAlHome() {
        supportFragmentManager.popBackStack()
    }

    override fun irAEditarCita(citaId: Long) {
        val fragmentoEditar = EditarCitaFragment().apply {
            arguments = bundleOf("citaId" to citaId)
        }
        supportFragmentManager.beginTransaction()
            .replace(ID_CONTENEDOR, fragmentoEditar)
            .addToBackStack(null)
            .commit()
    }

    private fun sembrarCitasDeEjemplo() {
        val dao = DatabaseProvider.getDatabase(this).citaDao()
        CoroutineScope(Dispatchers.IO).launch {
            if (dao.contar() > 0) return@launch
            CITAS_DE_EJEMPLO.forEach { dao.insertar(it) }
        }
    }

    private companion object {
        const val ID_CONTENEDOR = 0x0F0F01

        val CITAS_DE_EJEMPLO = listOf(
            CitaEntity(
                nombreMascota = "Luna",
                raza = "Labrador",
                nombrePropietario = "Ana Gomez",
                telefono = "3001234567",
                sintoma = "Cojera de la pata trasera derecha"
            ),
            CitaEntity(
                nombreMascota = "Rocky",
                raza = "Criollo",
                nombrePropietario = "Carlos Ruiz",
                telefono = "3109876543",
                sintoma = "Vacunacion anual y desparasitacion"
            ),
            CitaEntity(
                nombreMascota = "Michi",
                raza = "Siames",
                nombrePropietario = "Laura Diaz",
                telefono = "3205551234",
                sintoma = "Control postoperatorio"
            )
        )
    }
}
