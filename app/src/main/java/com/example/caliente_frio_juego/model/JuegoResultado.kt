package com.example.caliente_frio_juego.model
data class ResultadoJuego(
    val gano: Boolean,
    val tiempoUsadoMillis: Long,
    val distanciaAngularAlEncontrar: Float,
    val puntaje: Int
)
