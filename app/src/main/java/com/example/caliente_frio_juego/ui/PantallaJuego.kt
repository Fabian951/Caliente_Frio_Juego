package com.example.caliente_frio_juego.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.caliente_frio_juego.viewmodel.EstadoUiJuego
import com.example.caliente_frio_juego.model.EstadoJuego
import com.example.caliente_frio_juego.model.EstadoTemperatura
import androidx.compose.foundation.verticalScroll


@Composable
fun PantallaJuego(
    estadoUi: EstadoUiJuego,
    alIniciar: () -> Unit,
    alReiniciar: () -> Unit
) {
    val segundosRestantes = (estadoUi.tiempoRestanteMillis / 1000).coerceAtLeast(0)
    var mapaSeleccionado by remember { mutableStateOf("BOSQUE") }
    var mostrarPantallaAyuda by remember { mutableStateOf(false) }

    val (puntoCardinal, destinoX) = when (estadoUi.temperatura) {
        EstadoTemperatura.FRIO -> Pair("SUR O ESTE 🧭\n(Busca otra dirección)", -100f)
        EstadoTemperatura.TIBIO -> Pair("ALINEANDO RUMBO 🧭\n(Te estás acercando)", 45f)
        EstadoTemperatura.CALIENTE -> Pair("¡OBJETIVO AL NORTE! 🔥\n(¡Dirección Correcta!)", 0f)
    }

    val desplazamientoSuaveRadarX by animateFloatAsState(
        targetValue = destinoX,
        animationSpec = tween(durationMillis = 600)
    )

    val (textoTemp, colorFondoRadar, colorTexto) = when (estadoUi.temperatura) {
        EstadoTemperatura.FRIO -> Triple("FRÍO ❄️", Color(0x4400B4D8), Color(0xFF0284C7))
        EstadoTemperatura.TIBIO -> Triple("TIBIO ☀️", Color(0x44FFB703), Color(0xFFD97706))
        EstadoTemperatura.CALIENTE -> Triple("¡CALIENTE! 🔥", Color(0x44EF233C), Color(0xFFDC2626))
    }

    val colorSueloContenedor = when (mapaSeleccionado) {
        "CIUDAD" -> Color(0xFF475569)
        "DESIERTO" -> Color(0xFFE9C46A)
        "MONTAÑAS" -> Color(0xFF3D5A80)
        else -> Color(0xFF1B4332)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD8F3DC))
            .padding(top = 36.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "⏱️ Tiempo: 00:${segundosRestantes.toString().padStart(2, '0')}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B4332),
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.width(85.dp)
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Puntos", color = Color.Gray, fontSize = 11.sp)
                    Text(
                        text = "${estadoUi.resultado?.puntaje ?: 0}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B4332),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { mostrarPantallaAyuda = true },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White),
                modifier = Modifier.size(44.dp)
            ) {
                Text(text = "❓", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val mapas = listOf("BOSQUE" to "🌲 Pinos", "CIUDAD" to "🏙️ Urbano", "DESIERTO" to "🏜️ Arena", "MONTAÑAS" to "🏔️ Picos")
            mapas.forEach { (idMapa, textoEtiqueta) ->
                val esActivo = mapaSeleccionado == idMapa
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .clickable { mapaSeleccionado = idMapa },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = if (esActivo) Color(0xFF1B4332) else Color.White)
                ) {
                    Text(
                        text = textoEtiqueta,
                        fontWeight = FontWeight.Bold,
                        color = if (esActivo) Color.White else Color(0xFF1B4332),
                        modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(colorSueloContenedor, shape = RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ancho = size.width
                val alto = size.height

                when (mapaSeleccionado) {
                    "CIUDAD" -> {
                        drawRect(brush = Brush.verticalGradient(colors = listOf(Color(0xFF2C1A4D), Color(0xFFBA55D3), Color(0xFFF39A59))), size = Size(ancho, alto * 0.55f))
                        drawRect(Color(0xFF1E293B), Offset(ancho * 0.05f, alto * 0.25f), Size(90f, alto * 0.3f))
                        drawRect(Color(0xFF1E293B), Offset(ancho * 0.25f, alto * 0.20f), Size(110f, alto * 0.35f))
                        drawRect(Color(0xFF1E293B), Offset(ancho * 0.60f, alto * 0.30f), Size(100f, alto * 0.25f))
                        drawRect(Color(0xFF334155), Offset(ancho * 0.15f, alto * 0.22f), Size(100f, alto * 0.33f))
                        drawRect(Color(0xFF475569), Offset(ancho * 0.40f, alto * 0.12f), Size(140f, alto * 0.43f))
                        drawRect(Color(0xFF334155), Offset(ancho * 0.78f, alto * 0.26f), Size(95f, alto * 0.29f))
                        drawRect(Color(0xFF0F172A), Offset(ancho * 0.48f, alto * 0.05f), Size(6f, 50f))

                        val posicionesVentanas = listOf(
                            Offset(ancho * 0.43f, alto * 0.16f), Offset(ancho * 0.48f, alto * 0.16f), Offset(ancho * 0.52f, alto * 0.16f),
                            Offset(ancho * 0.43f, alto * 0.24f), Offset(ancho * 0.52f, alto * 0.24f),
                            Offset(ancho * 0.48f, alto * 0.32f), Offset(ancho * 0.43f, alto * 0.40f), Offset(ancho * 0.52f, alto * 0.40f),
                            Offset(ancho * 0.18f, alto * 0.26f), Offset(ancho * 0.22f, alto * 0.34f), Offset(ancho * 0.82f, alto * 0.30f)
                        )
                        posicionesVentanas.forEach { pos ->
                            drawRect(Color(0xFFFEF08A), pos, Size(16f, 16f))
                        }
                    }
                    "DESIERTO" -> {
                        drawRect(brush = Brush.verticalGradient(colors = listOf(Color(0xFFD946EF), Color(0xFFF97316), Color(0xFFFFD166))), size = Size(ancho, alto * 0.5f))
                        drawPath(Path().apply {
                            moveTo(0f, alto * 0.5f); quadraticTo(ancho * 0.3f, alto * 0.35f, ancho * 0.7f, alto * 0.48f)
                            quadraticTo(ancho * 0.85f, alto * 0.52f, ancho, alto * 0.45f); lineTo(ancho, alto); lineTo(0f, alto); close()
                        }, color = Color(0xFFC07A50))
                        drawPath(Path().apply {
                            moveTo(0f, alto * 0.46f); quadraticTo(ancho * 0.6f, alto * 0.32f, ancho, alto * 0.52f)
                            lineTo(ancho, alto); lineTo(0f, alto); close()
                        }, color = Color(0xFFE29578))
                        drawPath(Path().apply {
                            moveTo(0f, alto * 0.55f); quadraticTo(ancho * 0.4f, alto * 0.48f, ancho, alto * 0.6f)
                            lineTo(ancho, alto); lineTo(0f, alto); close()
                        }, color = Color(0xFFF4A261))

                        val cX = ancho * 0.18f
                        val cY = alto * 0.42f
                        drawRect(Color(0xFF283618), Offset(cX, cY), Size(22f, 90f))
                        drawRect(Color(0xFF283618), Offset(cX - 25f, cY + 25f), Size(30f, 16f))
                        drawRect(Color(0xFF283618), Offset(cX - 25f, cY + 5f), Size(16f, 30f))
                        drawRect(Color(0xFF283618), Offset(cX + 15f, cY + 40f), Size(25f, 16f))
                        drawRect(Color(0xFF283618), Offset(cX + 24f, cY + 15f), Size(16f, 35f))
                    }
                    "MONTAÑAS" -> {
                        drawRect(brush = Brush.verticalGradient(colors = listOf(Color(0xFF457B9D), Color(0xFFA8DADC), Color(0xFFF1FAEE))), size = Size(ancho, alto * 0.45f))
                        drawPath(Path().apply {
                            moveTo(0f, alto * 0.45f); lineTo(ancho * 0.25f, alto * 0.15f); lineTo(ancho * 0.5f, alto * 0.38f)
                            lineTo(ancho * 0.78f, alto * 0.10f); lineTo(ancho, alto * 0.42f); lineTo(ancho, alto); lineTo(0f, alto); close()
                        }, color = Color(0xFF1D3557))
                        drawPath(Path().apply { moveTo(ancho * 0.25f, alto * 0.15f); lineTo(ancho * 0.18f, alto * 0.23f); lineTo(ancho * 0.32f, alto * 0.23f); close() }, color = Color.White)
                        drawPath(Path().apply { moveTo(ancho * 0.78f, alto * 0.10f); lineTo(ancho * 0.71f, alto * 0.20f); lineTo(ancho * 0.85f, alto * 0.20f); close() }, color = Color.White)
                        drawPath(Path().apply {
                            moveTo(0f, alto * 0.42f); lineTo(ancho * 0.48f, alto * 0.22f); lineTo(ancho * 0.85f, alto * 0.45f)
                            lineTo(ancho, alto * 0.36f); lineTo(ancho, alto); lineTo(0f, alto); close()
                        }, color = Color(0xFF457B9D))
                        drawPath(Path().apply { moveTo(ancho * 0.48f, alto * 0.22f); lineTo(ancho * 0.41f, alto * 0.30f); lineTo(ancho * 0.54f, alto * 0.30f); close() }, color = Color(0xFFF1FAEE))
                    }
                    else -> {
                        drawRect(brush = Brush.verticalGradient(colors = listOf(Color(0xFF4EA8DE), Color(0xFF90E0EF), Color(0xFFF1FAEE))), size = Size(ancho, alto * 0.42f))
                        drawOval(Color(0xFF132A13), Offset(-100f, alto * 0.32f), Size(ancho * 0.8f, 200f))
                        drawOval(Color(0xFF132A13), Offset(ancho * 0.4f, alto * 0.35f), Size(ancho * 0.8f, 200f))

                        val arboles = listOf(
                            Triple(ancho * 0.15f, alto * 0.32f, 0.7f), Triple(ancho * 0.30f, alto * 0.35f, 0.6f),
                            Triple(ancho * 0.72f, alto * 0.30f, 0.8f), Triple(ancho * 0.85f, alto * 0.34f, 0.65f)
                        )
                        arboles.forEach { (x, y, escala) ->
                            drawRect(Color(0xFF582F0E), Offset(x - (10f * escala), y), Size(20f * escala, 80f * escala))
                            drawPath(Path().apply {
                                moveTo(x, y - (40f * escala)); lineTo(x - (45f * escala), y + (20f * escala))
                                lineTo(x + (45f * escala), y + (20f * escala)); close()
                            }, color = Color(0xFF31572C))
                        }
                        drawOval(Color(0xFF3F7D20), Offset(ancho * 0.25f, alto * 0.44f), Size(180f, 110f))
                        drawOval(Color(0xFF4C934C), Offset(ancho * 0.42f, alto * 0.42f), Size(210f, 130f))
                    }
                }

                if (estadoUi.temperatura == EstadoTemperatura.CALIENTE || estadoUi.estadoJuego == EstadoJuego.GANADO) {
                    val centroX = ancho * 0.5f
                    val centroY = alto * 0.42f
                    drawCircle(Color(0xFF5C4033), radius = 32f, center = Offset(centroX, centroY))
                    drawCircle(Color(0xFF5C4033), radius = 10f, center = Offset(centroX - 20f, centroY - 20f))
                    drawCircle(Color(0xFF5C4033), radius = 10f, center = Offset(centroX + 20f, centroY - 20f))
                    drawCircle(Color.Black, radius = 3.5f, center = Offset(centroX - 10f, centroY - 4f))
                    drawCircle(Color.Black, radius = 3.5f, center = Offset(centroX + 10f, centroY - 4f))
                }
            }

            // Visor óptico del radar flotante
            Box(
                modifier = Modifier
                    .offset(x = desplazamientoSuaveRadarX.dp, y = 60.dp)
                    .size(170.dp)
                    .background(colorFondoRadar, shape = RoundedCornerShape(85.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = textoTemp, fontWeight = FontWeight.Black, color = Color.Black, fontSize = 20.sp)
                    Text(text = puntoCardinal, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center, fontSize = 11.sp)
                }
            }

            if (estadoUi.estadoJuego == EstadoJuego.GANADO || estadoUi.estadoJuego == EstadoJuego.PERDIDO) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xEEFFFFFF)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(16.dp).fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (estadoUi.estadoJuego == EstadoJuego.GANADO) "¡LO ENCONTRASTE! 🎉" else "¡SE ACABÓ EL TIEMPO! 😢",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B4332),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (estadoUi.estadoJuego == EstadoJuego.GANADO) "Puntuación: ${estadoUi.resultado?.puntaje ?: 0} pts" else "El objetivo logró escapar.",
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = alReiniciar,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF40916C)),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Jugar de nuevo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            } else if (estadoUi.estadoJuego == EstadoJuego.INACTIVO) {
                Button(
                    onClick = alIniciar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332))
                ) {
                    Text("Iniciar Búsqueda", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF1B4332)).padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "TERMÓMETRO DE DISTANCIA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF00B4D8), Color(0xFFFFB703), Color(0xFFEF233C))
                        ),
                        shape = RoundedCornerShape(5.dp)
                    )
            )
        }
    }

    // DIÁLOGO APARTE DE AYUDA RÁPIDA CON SCROLL INCLUIDO
    if (mostrarPantallaAyuda) {
        Dialog(onDismissRequest = { mostrarPantallaAyuda = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .heightIn(max = 450.dp) // Define un tamaño máximo para forzar el scroll interno
                        .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🧭 GUÍA GENERAL DE EXPLORACIÓN", fontWeight = FontWeight.Bold, color = Color(0xFF1B4332), fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "1. El personaje oculto se encuentra en una coordenada secreta.\n\n" +
                                "2. Gira despacio el celular de forma física para rastrear el terreno.\n\n" +
                                "3. El círculo del radar se moverá de forma fluida indicándote hacia dónde ir:\n" +
                                "   • Si el círculo se desliza a la izquierda, estás en rumbo incorrecto.\n" +
                                "   • Cuando el círculo se centre por completo, estarás apuntando de forma exacta.\n\n" +
                                "4. ¡Encuéntralo antes de que el cronómetro llegue a cero!\n\n" +
                                "5. El termómetro inferior te dará una referencia estática del calor o frío emitido por el objetivo.",
                        color = Color.DarkGray,
                        textAlign = TextAlign.Start,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { mostrarPantallaAyuda = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332))
                    ) {
                        Text("Entendido, Volver al Juego", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

