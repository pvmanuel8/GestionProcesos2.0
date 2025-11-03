package org.example

import java.io.IOException
import java.io.PipedOutputStream
import kotlin.random.Random


class ProdNum (private val pipeOut: PipedOutputStream) {
    // Genera una lista de números aleatorios según la cantidad y el rango indicados
    fun generarNumerosAleatorios(cantidad: Int, rango: IntRange = 1..100): List<Int> {
        return List(cantidad) { Random.nextInt(rango.first, rango.last + 1) }

    }

    fun enviarDatos(input: String){
        try {
            println("Ingresa numeros. Escribe una linea vacia para finalizar:")
            val numeros = mutableListOf<String>()
            while (true) {
                val linea = readLine()
                if (linea == null || linea.isBlank()) { break }
                numeros.add(linea.trim())
            }
            val cadenaParaEnviar = numeros.joinToString(",")
            pipeOut.write(cadenaParaEnviar.toByteArray())
            pipeOut.flush()
            pipeOut.close()
        }catch (e: IOException){
            println("Error: ${e.message}")
        }
    }

}
