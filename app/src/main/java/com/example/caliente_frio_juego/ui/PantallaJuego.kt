package com.example.caliente_frio_juego.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.caliente_frio_juego.model.EstadoJuego
import com.example.caliente_frio_juego.model.EstadoTemperatura
import com.example.caliente_frio_juego.viewmodel.EstadoUiJuego

@Composable
fun PantallaJuego(
    estadoUi: EstadoUiJuego,
    alIniciar: () -> Unit,
    alReiniciar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Tiempo: ${estadoUi.tiempoRestanteMillis / 1000}s", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        val (etiqueta, color) = when (estadoUi.temperatura) {
            EstadoTemperatura.FRIO -> "FRÍO" to Color(0xFF2196F3)
            EstadoTemperatura.TIBIO -> "TIBIO" to Color(0xFFFF9800)
            EstadoTemperatura.CALIENTE -> "CALIENTE" to Color(0xFFF44336)
        }

        Text(etiqueta, style = MaterialTheme.typography.displayMedium, color = color)

        Spacer(Modifier.height(32.dp))

        when (estadoUi.estadoJuego) {
            EstadoJuego.INACTIVO -> Button(onClick = alIniciar) { Text("Iniciar búsqueda") }
            EstadoJuego.JUGANDO -> Text("¡Gira el teléfono para buscar!")
            EstadoJuego.GANADO -> {
                Text("¡Encontrado! Puntaje: ${estadoUi.resultado?.puntaje}")
                Button(onClick = alReiniciar) { Text("Reiniciar") }
            }
            EstadoJuego.PERDIDO -> {
                Text("Se acabó el tiempo, no lo encontraste")
                Button(onClick = alReiniciar) { Text("Reintentar") }
            }
        }
    }
}
