package com.example.caliente_frio_juego.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaExplicacionInstrucciones(
    alVolverAlMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD8F3DC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Manual del Explorador", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B4332))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 24.dp)
                .fillMaxWidth()
        ) {
            // Añadido modificador verticalScroll para permitir bajar con el dedo y leer todo
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "1. El objetivo se oculta aleatoriamente en un rumbo del mapa.", fontSize = 14.sp, color = Color.DarkGray)
                Text(text = "2. Gira el dispositivo de forma física en círculos para escanear el bioma.", fontSize = 14.sp, color = Color.DarkGray)
                Text(text = "3. El anillo de temperatura central flotará guiándote en tiempo real:\n\n • Izquierda: Rumbo incorrecto.\n\n • Derecha (Tibio): Te estás acercando.\n\n • Centrado: Vas directo hacia él.", fontSize = 14.sp, color = Color.DarkGray)
                Text(text = "4. Al alinearte por completo en el área caliente, el personaje se revelará ganando la partida.", fontSize = 14.sp, color = Color.DarkGray)
                Text(text = "5. Recuerda controlar el reloj superior: si la cuenta regresiva llega a cero antes de hallar el objetivo, perderás de forma automática la partida actual.", fontSize = 14.sp, color = Color.DarkGray)
                Text(text = "6. Consigue bonificaciones especiales completando el mapa con la mayor velocidad y exactitud angular posibles.", fontSize = 14.sp, color = Color.DarkGray)
            }
        }

        Button(
            onClick = alVolverAlMenu,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("ENTENDIDO, VOLVER", fontWeight = FontWeight.Bold)
        }
    }
}
