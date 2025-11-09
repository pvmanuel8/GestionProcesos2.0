package org.dam.project

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Proceso Consumidor
 * Recibe datos del flujo de entrada estándar, procesa números (suma) y palabras (cuenta letras)
 */
fun main(args: Array<String>) {
    val reader = BufferedReader(InputStreamReader(System.`in`))

    var sumaNumeros = 0
    var contadorNumeros = 0
    var totalLetras = 0
    var contadorPalabras = 0

    try {
        println("CONSUMIDOR: Esperando datos...")

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val dato = line!!.trim()

            if (dato == "FIN") {
                println("CONSUMIDOR: Señal de fin recibida")
                break
            }

            if (dato.isEmpty()) continue

            // Intentar procesar como número
            val numero = dato.toIntOrNull()
            if (numero != null) {
                sumaNumeros += numero
                contadorNumeros++
                println("CONSUMIDOR: Recibido numero $numero | Suma acumulada: $sumaNumeros")
            } else {
                // Procesar como palabra
                val letras = dato.length
                totalLetras += letras
                contadorPalabras++
                println("CONSUMIDOR: Recibida palabra '$dato' ($letras letras) | Total letras: $totalLetras")
            }
        }

        // Mostrar resultados finales
        println("\n" + "=".repeat(50))
        println("RESUMEN FINAL DEL CONSUMIDOR")
        println("=".repeat(50))
        println("Numeros procesados: $contadorNumeros")
        println("Suma total de numeros: $sumaNumeros")
        println("Palabras procesadas: $contadorPalabras")
        println("Total de letras: $totalLetras")
        println("=".repeat(50))

    } catch (e: Exception) {
        System.err.println("ERROR en consumidor: ${e.message}")
        e.printStackTrace(System.err)
    } finally {
        reader.close()
    }
}