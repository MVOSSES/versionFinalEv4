package com.example.nuevo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var consumoTextView: TextView
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        consumoTextView = findViewById(R.id.consumoTextView)
        refreshButton = findViewById(R.id.refreshButton)

        refreshButton.setOnClickListener {
            fetchConsumoData()
        }
    }

    // Método para obtener los datos desde Firebase
    private fun fetchConsumoData() {
        // Aquí usamos un hilo de fondo para hacer la petición a Firebase y evitar bloquear el hilo principal
        Thread {
            try {
                // URL de tu base de datos Firebase
                val url = "https://energysmart-eed14-default-rtdb.firebaseio.com/consumo.json"
                val result = URL(url).readText()  // Realizamos la lectura

                // Imprime los datos que recibes de Firebase para verificar su formato
                Log.d("FirebaseResponse", result)

                // Convertimos el JSON recibido en un objeto
                val jsonObject = JSONObject(result)

                // Accede al primer valor de consumo (la estructura es dinámica, así que necesitamos acceder a una de las claves)
                val keys = jsonObject.keys()
                if (keys.hasNext()) {
                    val key = keys.next()  // Obtiene el primer identificador (por ejemplo, OEP30b9Ai3koPiRO0dE)
                    val consumo = jsonObject.getInt(key)  // Obtiene el valor asociado a esa clave

                    // Actualizamos la UI en el hilo principal
                    runOnUiThread {
                        consumoTextView.text = "Consumo: $consumo"
                    }
                } else {
                    runOnUiThread {
                        consumoTextView.text = "No hay datos disponibles"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    consumoTextView.text = "Error al obtener datos"
                }
            }
        }.start()
    }
}
