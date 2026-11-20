package com.example.dogapp.view.detalle

/**
 * Contrato de navegacion del detalle. Lo implementa la Activity anfitriona
 * (Sebastian: Navigation / integracion), asi el Fragment no depende de como se navega.
 *
 * Si se usa Navigation Component, basta con reemplazar el cuerpo de estas dos funciones por
 * findNavController().popBackStack() y findNavController().navigate(...) con el citaId.
 */
interface DetalleCitaNavegacion {
    /** Vuelve a HU 2.0 (Home). */
    fun volverAlHome()

    /** Abre HU 5.0 (Editar cita) con el ID de la cita. */
    fun irAEditarCita(citaId: Long)
}
