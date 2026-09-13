package com.example.caliente_frio_juego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.caliente_frio_juego.sensors.ProveedorOrientacion
import com.example.caliente_frio_juego.ui.PantallaJuego
import com.example.caliente_frio_juego.ui.PantallaMenuPrincipal
import com.example.caliente_frio_juego.ui.PantallaExplicacionInstrucciones
import com.example.caliente_frio_juego.ui.theme.Caliente_Frio_JuegoTheme
import com.example.caliente_frio_juego.viewmodel.JuegoViewModel

// La fábrica declarada de forma correcta en el mismo paquete para evitar fallos de referencia
class FabricaJuegoViewModel(private val proveedorOrientacion: ProveedorOrientacion) :
    ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return JuegoViewModel(proveedorOrientacion) as T
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val proveedorOrientacion = ProveedorOrientacion(applicationContext)

        setContent {
            Caliente_Frio_JuegoTheme {
                val viewModel: JuegoViewModel = viewModel(
                    factory = FabricaJuegoViewModel(proveedorOrientacion)
                )
                val estadoUi by viewModel.estadoUi.collectAsState()

                var pantallaActual by remember { mutableStateOf("MENU") }

                when (pantallaActual) {
                    "MENU" -> PantallaMenuPrincipal(
                        alAlcanzarJuego = {
                            viewModel.iniciarJuego()
                            pantallaActual = "JUEGO"
                        },
                        alAlcanzarInstrucciones = { pantallaActual = "INSTRUCCIONES" }
                    )
                    "INSTRUCCIONES" -> PantallaExplicacionInstrucciones(
                        alVolverAlMenu = { pantallaActual = "MENU" }
                    )
                    "JUEGO" -> PantallaJuego(
                        estadoUi = estadoUi,
                        alIniciar = { viewModel.iniciarJuego() },
                        alReiniciar = { viewModel.reiniciarJuego() }
                    )
                }
            }
        }
    }
}
