package com.example.dogapp

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dogapp.data.local.database.DatabaseProvider
import com.example.dogapp.data.local.entity.CitaEntity
import com.example.dogapp.view.detalle.DetalleCitaFragment
import com.example.dogapp.view.detalle.DetalleCitaNavegacion
import com.example.dogapp.view.home.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Atajo SOLO de debug para revisar la HU 4.0 (Detalle de la Cita) sin login ni navegacion.
 *
 * Abre el detalle de una cita (por defecto la primera; se puede pedir otra con
 * `--el citaId 2`). Al eliminar, "volver" muestra el Home, que es la evidencia que pide la HU.
 * El boton de editar solo avisa con un Toast: HU 5.0 es de otra subtarea.
 */
class DetallePreviewActivity : AppCompatActivity(), DetalleCitaNavegacion {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(crearContenedor())
        if (savedInstanceState == null) abrirDetalleDeEjemplo()
    }

    private fun crearContenedor() = FrameLayout(this).apply {
        id = ID_CONTENEDOR
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    private fun abrirDetalleDeEjemplo() {
        val dao = DatabaseProvider.getDatabase(this).citaDao()
        val citaPedida = intent.getLongExtra(EXTRA_CITA_ID, 0L)
        CoroutineScope(Dispatchers.IO).launch {
            if (dao.contar() == 0) CITAS_DE_EJEMPLO.forEach { dao.insertar(it) }
            val id = if (citaPedida > 0) citaPedida else dao.observarTodas().first().firstOrNull()?.id ?: 0L
            runOnUiThread { mostrar(DetalleCitaFragment.newInstance(id)) }
        }
    }

    private fun mostrar(fragment: androidx.fragment.app.Fragment) {
        if (isFinishing || isDestroyed) return
        supportFragmentManager.beginTransaction()
            .replace(ID_CONTENEDOR, fragment)
            .commitAllowingStateLoss()
    }

    override fun volverAlHome() = mostrar(HomeFragment())

    override fun irAEditarCita(citaId: Long) {
        Toast.makeText(this, "HU 5.0 Editar cita (citaId=$citaId)", Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val ID_CONTENEDOR = 0x0F0F02
        const val EXTRA_CITA_ID = "citaId"

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
