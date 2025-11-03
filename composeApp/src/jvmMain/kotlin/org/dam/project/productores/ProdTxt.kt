package org.example

import java.io.IOException
import java.io.PipedOutputStream

class ProdTxt(private val pipeOut: PipedOutputStream) {
    fun enviarTexto(input: String){
        try {
            println("Ingresa un texto:")
            val input = readLine() ?: ""
            pipeOut.write(input.toByteArray())
            pipeOut.flush()
            pipeOut.close()
        } catch (e: IOException) {
            println("Error en la escritura: ${e.message}")
        }
    }


}