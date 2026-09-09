package com.example.caliente_frio_juego.logic

/**
 * Formula de puntuacion para luis y miguel:
 *
 * puntaje = max(0, PUNTAJE_BASE - (segundosUsados * PENALIZACION_POR_SEGUNDO)) + bonificacionPrecision
 *
 * - PUNTAJE_BASE = 1000 puntos de partida
 * - PENALIZACION_POR_SEGUNDO = 20 puntos por cada segundo transcurrido
 * - bonificacionPrecision según qué tan exacta fue la orientación al encontrar:
 *     <= 3°  -> +200 (precisión excelente)
 *     <= 7°  -> +100 (precisión buena)
 *     <= 15° -> +0   (precisión aceptable, límite de "encontrado")
 */
object CalculadoraPuntaje {
    private const val PUNTAJE_BASE = 1000
    private const val PENALIZACION_POR_SEGUNDO = 20

    fun calcular(tiempoUsadoMillis: Long, distanciaAngularAlEncontrar: Float): Int {
        val segundos = tiempoUsadoMillis / 1000.0
        val puntajePorTiempo = (PUNTAJE_BASE - segundos * PENALIZACION_POR_SEGUNDO).coerceAtLeast(0.0)

        val bonificacionPrecision = when {
            distanciaAngularAlEncontrar <= 3f -> 200
            distanciaAngularAlEncontrar <= 7f -> 100
            else -> 0
        }

        return (puntajePorTiempo + bonificacionPrecision).toInt()
    }
}
