import java.io.BufferedWriter
import java.io.OutputStreamWriter
import kotlin.random.Random

/**
 * Proceso Productor de Números Aleatorios
 * Genera números aleatorios y los envía a través del flujo de salida estándar
 */
fun main(args: Array<String>) {
    val writer = BufferedWriter(OutputStreamWriter(System.out))

    try {
        // Determinar cantidad de números a generar
        val count = if (args.isNotEmpty()) args[0].toInt() else 10

        println("PRODUCTOR DE NÚMEROS: Iniciando generación de $count números...")

        repeat(count) { i ->
            val number = Random.nextInt(1, 100)
            writer.write("$number\n")
            writer.flush()

            // Simular trabajo
            Thread.sleep(200)
            System.err.println("PRODUCTOR DE NÚMEROS: Enviado número $number (${i + 1}/$count)")
        }

        // Señal de finalización
        writer.write("FIN\n")
        writer.flush()
        System.err.println("PRODUCTOR DE NÚMEROS: Finalizado")

    } catch (e: Exception) {
        System.err.println("ERROR en productor de números: ${e.message}")
        e.printStackTrace(System.err)
    } finally {
        writer.close()
    }
}