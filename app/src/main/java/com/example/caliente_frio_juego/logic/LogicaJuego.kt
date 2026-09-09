package com.example.caliente_frio_juego.logic

import com.example.caliente_frio_juego.model.EstadoTemperatura
import kotlin.random.Random

class LogicaJuego(
    private val margenEncontradoGrados: Float = 15f,
    private val umbralCaliente: Float = 30f,
    private val umbralTibio: Float = 90f
) {
    // "var...private set" significa: cualquiera puede LEER azimutObjetivo,
    var azimutObjetivo: Float = 0f
        private set

    fun generarNuevoObjetivo() {
        azimutObjetivo = Random.nextFloat() * 360f  // este es número aleatorio entre 0 y 360
    }

    fun distanciaAngular(actual: Float, objetivo: Float): Float {
        val diferencia = Math.abs(actual - objetivo) % 360f
        return if (diferencia > 180f) 360f - diferencia else diferencia
    }

    fun obtenerEstadoTemperatura(azimutActual: Float): EstadoTemperatura {
        val distancia = distanciaAngular(azimutActual, azimutObjetivo)
        return when {
            distancia <= umbralCaliente -> EstadoTemperatura.CALIENTE
            distancia <= umbralTibio -> EstadoTemperatura.TIBIO
            else -> EstadoTemperatura.FRIO
        }
    }

    fun objetivoEncontrado(azimutActual: Float): Boolean {
        return distanciaAngular(azimutActual, azimutObjetivo) <= margenEncontradoGrados
    }
}