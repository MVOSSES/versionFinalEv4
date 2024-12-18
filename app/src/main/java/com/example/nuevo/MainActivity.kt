package com.example.nuevo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var consumoTextView: TextView
    private lateinit var costTextView: TextView  // Nuevo TextView para mostrar el coste
    private lateinit var refreshButton: Button
    private var consumoList = mutableListOf<Int>()  // Lista para almacenar los valores de consumo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        consumoTextView = findViewById(R.id.consumoTextView)
        costTextView = findViewById(R.id.costTextView)  // Inicializamos el nuevo TextView
        refreshButton = findViewById(R.id.refreshButton)

        // Cargar datos de Firebase al inicio
        fetchConsumoData()

        refreshButton.setOnClickListener {
            // Mostrar un dato aleatorio cada vez que se presiona el botón
            if (consumoList.isNotEmpty()) {
                val randomIndex = Random.nextInt(consumoList.size)
                val randomConsumo = consumoList[randomIndex]

                // Mostrar el valor de consumo
                consumoTextView.text = "Consumo: $randomConsumo"

                // Calcular el coste y mostrarlo
                val coste = calculateCost(randomConsumo)
                costTextView.text = "Coste: $coste"
            } else {
                consumoTextView.text = "No hay datos disponibles"
                costTextView.text = "Coste: 0"
            }
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

                // Limpiar la lista antes de llenarla
                consumoList.clear()

                // Recorrer todas las claves y agregar sus valores a la lista consumoList
                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next()  // Obtiene cada clave (por ejemplo, OEP30b9Ai3koPiRO0dE)
                    val consumo = jsonObject.getInt(key)  // Obtiene el valor asociado a esa clave
                    consumoList.add(consumo)  // Agrega el valor de consumo a la lista
                }

                // Si la lista tiene datos, se actualiza la UI
                runOnUiThread {
                    if (consumoList.isNotEmpty()) {
                        val randomIndex = Random.nextInt(consumoList.size)
                        val randomConsumo = consumoList[randomIndex]
                        consumoTextView.text = "Consumo: $randomConsumo"

                        // Calcular el coste basado en el consumo
                        val coste = calculateCost(randomConsumo)

                        // Mostrar el coste calculado
                        costTextView.text = "Coste: $coste"
                    } else {
                        consumoTextView.text = "No hay datos disponibles"
                        costTextView.text = "Coste: 0"
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    consumoTextView.text = "Error al obtener datos"
                    costTextView.text = "Coste: 0"
                }
            }
        }.start()
    }

    // Función para calcular el coste multiplicando el consumo por 150
    private fun calculateCost(consumo: Int): Int {
        return consumo * 150  // Multiplica el consumo por 150
    }
}
