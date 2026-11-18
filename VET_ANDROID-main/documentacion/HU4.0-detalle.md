# HU 4.0 — Ventana Detalle de la Cita (VET_ANDROID)

## Reparto de la HU (Jira DOG-5)

| Subtarea | Responsable | Estado |
|---|---|---|
| Toolbar con el nombre de la mascota y flecha rosada | Santos | base hecha, se puede pulir |
| Tarjetas superpuestas (solape + elevación) | Santos | base hecha, se puede pulir |
| Datos: turno, raza, síntoma, divisor rosado, propietario y teléfono | Chicaiza | hecho |
| DetalleViewModel con LiveData<Cita?> por citaId | Medina | versión base hecha (mismo patrón de HomeViewModel) |
| FAB eliminar (Room) y FAB editar hacia HU 5.0 | Chicaiza | hecho; editar espera la navegación |
| QA: solape visible, borrado y regreso al Home | Agudelo | pendiente |

## Archivos

- `view/detalle/DetalleCitaFragment.kt` — pantalla; recibe el argumento `citaId`.
- `view/detalle/DetalleCitaNavegacion.kt` — contrato `volverAlHome()` / `irAEditarCita(id)`.
- `viewmodel/DetalleViewModel.kt` — `LiveData<CitaEntity?>` por `citaId` y `eliminar()`.
- `res/layout/fragment_detalle_cita.xml` — layout completo (bloques comentados por subtarea).
- `res/drawable/`: `bg_toolbar_detalle`, `ic_arrow_back`, `ic_delete`, `ic_edit`.
- `src/debug/.../DetallePreviewActivity.kt` — solo debug, para revisar sin login.
- `src/test/.../DetalleViewModelTest.kt` — pruebas del ViewModel.
- `FakeCitaDao.observarPorId` ahora es reactivo (antes devolvía una foto fija).

## Integración (Sebastián / Home)

1. En `HomeFragment`, el clic de la tarjeta debe abrir el detalle con el ID:
   `DetalleCitaFragment.newInstance(cita.id)`, o con Navigation un argumento `Long` llamado `citaId`.
2. La Activity anfitriona implementa `DetalleCitaNavegacion`:
   `volverAlHome()` → HU 2.0 e `irAEditarCita(id)` → HU 5.0.
3. Si la Activity anfitriona ya aplica insets de barras del sistema (como `MainActivity`),
   quitar `ajustarBarrasDelSistema()` del Fragment para no duplicar el padding.

## Cómo verlo

Android Studio: ejecutar `app` y abrir **Detalle Cita (debug)**. Otra cita:

```bash
adb shell am start -n com.example.dogapp/com.example.dogapp.DetallePreviewActivity --el citaId 2
```

Al presionar el FAB rosado la cita se borra de Room y la pantalla vuelve al Home sin ella.

## Evidencia pendiente

- Captura del detalle (solape visible).
- Captura del Home después de eliminar.
