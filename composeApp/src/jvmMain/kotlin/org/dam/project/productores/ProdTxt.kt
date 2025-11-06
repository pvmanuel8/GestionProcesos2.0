import java.io.BufferedWriter
import java.io.OutputStreamWriter
import kotlin.random.Random

/**
 * Proceso Productor de Palabras
 * Genera palabras aleatorias y las envía a través del flujo de salida estándar
 */
fun main(args: Array<String>) {
    val writer = BufferedWriter(OutputStreamWriter(System.out))

    val palabras = listOf(
        "comunicacion", "proceso", "kotlin", "programacion", "datos",
        "sistema", "aplicacion", "desarrollo", "software", "tecnologia",
        "computadora", "interaccion", "mensaje", "informacion", "codigo"
    )

    try {
        // Determinar cantidad de palabras a generar
        val count = if (args.isNotEmpty()) args[0].toInt() else 10

        println("PRODUCTOR DE PALABRAS: Iniciando generación de $count palabras...")

        repeat(count) { i ->
            val palabra = palabras.random()
            writer.write("$palabra\n")
            writer.flush()

            // Simular trabajo
            Thread.sleep(300)
            System.err.println("PRODUCTOR DE PALABRAS: Enviada palabra '$palabra' (${i + 1}/$count)")
        }

        // Señal de finalización
        writer.write("FIN\n")
        writer.flush()
        System.err.println("PRODUCTOR DE PALABRAS: Finalizado")

    } catch (e: Exception) {
        System.err.println("ERROR en productor de palabras: ${e.message}")
        e.printStackTrace(System.err)
    } finally {
        writer.close()
    }
}