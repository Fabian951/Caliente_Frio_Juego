package com.example.caliente_frio_juego.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caliente_frio_juego.logic.LogicaJuego
import com.example.caliente_frio_juego.logic.CalculadoraPuntaje
import com.example.caliente_frio_juego.model.ResultadoJuego
import com.example.caliente_frio_juego.model.EstadoJuego
import com.example.caliente_frio_juego.model.EstadoTemperatura
import com.example.caliente_frio_juego.sensors.ProveedorOrientacion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val LIMITE_TIEMPO_MILLIS = 30_000L // cada grupo define el tiempo; 30s de ejemplo

// "cajita" que agrupa TODO lo que la pantalla necesita mostrar en un momento dado
data class EstadoUiJuego(
    val estadoJuego: EstadoJuego = EstadoJuego.INACTIVO,
    val temperatura: EstadoTemperatura = EstadoTemperatura.FRIO,
    val tiempoRestanteMillis: Long = LIMITE_TIEMPO_MILLIS,
    val resultado: ResultadoJuego? = null
)

class JuegoViewModel(
    private val proveedorOrientacion: ProveedorOrientacion
) : ViewModel() {

    private val logicaJuego = LogicaJuego()

    private val _estadoUi = MutableStateFlow(EstadoUiJuego())
    val estadoUi: StateFlow<EstadoUiJuego> = _estadoUi.asStateFlow()

    private var tiempoInicioMillis = 0L
    private var azimutActual = 0f
    private var trabajoTemporizador: Job? = null
    private var trabajoSensor: Job? = null

    fun iniciarJuego() {
        logicaJuego.generarNuevoObjetivo()
        tiempoInicioMillis = System.currentTimeMillis()
        _estadoUi.value = EstadoUiJuego(estadoJuego = EstadoJuego.JUGANDO, tiempoRestanteMillis = LIMITE_TIEMPO_MILLIS)

        // Nos "suscribimos" al chorro de datos del sensor
        trabajoSensor?.cancel()
        trabajoSensor = viewModelScope.launch {
            proveedorOrientacion.flujoAzimut().collect { azimut ->
                azimutActual = azimut
                verificarProgreso()
            }
        }

        // Cuenta regresiva en paralelo
        trabajoTemporizador?.cancel()
        trabajoTemporizador = viewModelScope.launch {
            var restante = LIMITE_TIEMPO_MILLIS
            while (restante > 0 && _estadoUi.value.estadoJuego == EstadoJuego.JUGANDO) {
                delay(200)
                restante -= 200
                _estadoUi.value = _estadoUi.value.copy(tiempoRestanteMillis = restante.coerceAtLeast(0))
            }
            if (_estadoUi.value.estadoJuego == EstadoJuego.JUGANDO) {
                terminarJuego(gano = false)
            }
        }
    }

    private fun verificarProgreso() {
        if (_estadoUi.value.estadoJuego != EstadoJuego.JUGANDO) return

        val temperatura = logicaJuego.obtenerEstadoTemperatura(azimutActual)
        _estadoUi.value = _estadoUi.value.copy(temperatura = temperatura)

        if (logicaJuego.objetivoEncontrado(azimutActual)) {
            terminarJuego(gano = true)
        }
    }

    private fun terminarJuego(gano: Boolean) {
        trabajoSensor?.cancel()
        trabajoTemporizador?.cancel()

        val tiempoUsado = System.currentTimeMillis() - tiempoInicioMillis
        val distanciaAngular = logicaJuego.distanciaAngular(azimutActual, logicaJuego.azimutObjetivo)
        val puntaje = if (gano) CalculadoraPuntaje.calcular(tiempoUsado, distanciaAngular) else 0

        _estadoUi.value = _estadoUi.value.copy(
            estadoJuego = if (gano) EstadoJuego.GANADO else EstadoJuego.PERDIDO,
            resultado = ResultadoJuego(gano, tiempoUsado, distanciaAngular, puntaje)
        )
    }

    fun reiniciarJuego() = iniciarJuego()

    override fun onCleared() {
        super.onCleared()
        trabajoSensor?.cancel()
        trabajoTemporizador?.cancel()
    }
}