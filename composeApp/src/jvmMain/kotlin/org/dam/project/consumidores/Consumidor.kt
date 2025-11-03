import java.io.IOException
import java.io.PipedInputStream

class Consumidor(private val pipeIn: PipedInputStream) {

    // Método para consumir y procesar números enviados por el pipe
    fun consumirNumeros(): Int {
        return try {
            val buffer = ByteArray(1024)
            val bytesLeidos = pipeIn.read(buffer) // Lee los datos del pipe
            if (bytesLeidos == -1){
                println("Conexion cerrada por el productor")
                0
            }else {
                val recibido = String(buffer, 0, bytesLeidos) // Convierte los bytes a String
                val numeros = recibido.split(",").mapNotNull { it.trim().toIntOrNull() }
                val suma = numeros.sum() // Suma los números recibidos
                println("Consumidor recibio numeros: $recibido")
                println("Suma de números: $suma")
                return suma
            }
        } catch (e : IOException){
            println("Error en la lectura de numeros: ${e.message}")
            0
        }
    }

    //Método para consumir y procesar texto enviado por el pipe
    fun consumirTexto(): Int {
        return  try {
            val buffer = ByteArray(1024)
            val bytesLeidos = pipeIn.read(buffer) // Lee los datos del pipe
            if (bytesLeidos == -1){
                println("Conexion cerrada por el productor")
                0
            }else{
                val recibido = String(buffer, 0, bytesLeidos) // Convierte los bytes a String
                val palabraCount = recibido.trim().split("\\s+".toRegex()).size // Cuenta palabras
                println("Consumidor recibio texto: $recibido")
                println("Cantidad de palabras: $palabraCount")
                return palabraCount

            }
        } catch (e: IOException) {
            println("Error en la lectura de texto: ${e.message}")
            0
        }
    }
}