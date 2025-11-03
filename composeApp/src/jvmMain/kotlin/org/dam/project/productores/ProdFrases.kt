import java.io.IOException
import java.io.PipedOutputStream

class ProdFrases (private val pipeOut: PipedOutputStream) {
    private  val sujetos = listOf("El bicho", "Messi", "Jeff Bezos", "Amancio Ortega")
    private val verbos = listOf("mira" , "conduce", "juega en", "compra")
    private val objetos = listOf("un lamborghini", "Miami", "un rascacielos", "un lagarto")

    fun enviaDatos(input: String){
        try {
            println("Cuantas frases aleatorias quieres generar")
            val cantidad = readLine()?.toIntOrNull() ?: 3
            val frases = mutableListOf<String>()
            repeat(cantidad) {
                val frase = "${sujetos.random()} ${objetos.random()} ${objetos.random()}"
                frases.add(frase)
            }
            val cadenaEnviar = frases.joinToString("; ")
            println("Frases generadas:\n ${cadenaEnviar}")
            pipeOut.write(cadenaEnviar.toByteArray())
            pipeOut.flush()
            pipeOut.close()
        }catch (e: IOException) {
            println("Error en la escritura del productor de frases: ${e.message}")
        }
    }
}