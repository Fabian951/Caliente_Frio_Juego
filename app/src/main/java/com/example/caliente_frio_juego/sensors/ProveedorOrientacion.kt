package com.example.caliente_frio_juego.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Combina acelerómetro + magnetómetro para obtener el azimut (0-360°)
 * de forma estable. Expone un Flow para que el ViewModel lo consuma.
 */
class ProveedorOrientacion(contexto: Context) {

    private val administradorSensores =
        contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val acelerometro = administradorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometro = administradorSensores.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val gravedad = FloatArray(3)
    private val geomagnetico = FloatArray(3)

    fun flujoAzimut(): Flow<Float> = callbackFlow {
        val oyente = object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent) {
                when (evento.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER ->
                        System.arraycopy(evento.values, 0, gravedad, 0, 3)
                    Sensor.TYPE_MAGNETIC_FIELD ->
                        System.arraycopy(evento.values, 0, geomagnetico, 0, 3)
                }

                val matrizRotacion = FloatArray(9)
                val exito = SensorManager.getRotationMatrix(
                    matrizRotacion, null, gravedad, geomagnetico
                )
                if (exito) {
                    val orientacion = FloatArray(3)
                    SensorManager.getOrientation(matrizRotacion, orientacion)
                    val azimutRadianes = orientacion[0]
                    var azimutGrados = Math.toDegrees(azimutRadianes.toDouble()).toFloat()
                    if (azimutGrados < 0) azimutGrados += 360f
                    trySend(azimutGrados)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, precision: Int) {}
        }

        administradorSensores.registerListener(oyente, acelerometro, SensorManager.SENSOR_DELAY_GAME)
        administradorSensores.registerListener(oyente, magnetometro, SensorManager.SENSOR_DELAY_GAME)

        awaitClose { administradorSensores.unregisterListener(oyente) }
    }
}