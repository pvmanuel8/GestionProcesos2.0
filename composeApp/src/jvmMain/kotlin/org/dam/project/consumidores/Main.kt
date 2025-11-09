package org.dam.project

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.File

/**
 * Aplicación Principal - Sistema de Comunicación entre Procesos (IPC)
 * Coordina la ejecución de procesos productores y consumidor usando ProcessBuilder
 */

data class ProducerOption(
    val id: Int,
    val name: String,
    val mainClass: String,
    val description: String
)

fun main() {
    println("""
             SISTEMA DE COMUNICACION ENTRE PROCESOS (IPC)                               
        
    """.trimIndent())

    val producers = listOf(
        ProducerOption(1, "Numeros Aleatorios", "org.dam.project.ProdNumKt", "Genera numeros aleatorios (1-100)"),
        ProducerOption(2, "Palabras", "org.dam.project.ProdTxtKt", "Genera palabras aleatorias")
    )

    // Mostrar opciones
    println("\nProductores disponibles:")
    producers.forEach { p ->
        println("  ${p.id}. ${p.name} - ${p.description}")
    }

    // Seleccionar productor
    print("\nSeleccione un productor (1-${producers.size}): ")
    val selection = readLine()?.toIntOrNull() ?: 1
    val selectedProducer = producers.find { it.id == selection } ?: producers[0]

    println("\nProductor seleccionado: ${selectedProducer.name}")

    // Solicitar cantidad de elementos
    print("Cantidad de elementos a generar (por defecto 10): ")
    val cantidad = readLine()?.toIntOrNull() ?: 10

    println("\nIniciando comunicacion entre procesos...")
    println("-".repeat(60))

    try {
        // Obtener el classpath actual y construir uno completo
        val projectDir = File(System.getProperty("user.dir"))
        val isWindows = System.getProperty("os.name").lowercase().contains("windows")
        val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
        val pathSeparator = if (isWindows) ";" else ":"

        // Buscar el directorio de clases compiladas
        val possibleClassDirs = listOf(
            File(projectDir, "composeApp/build/classes/kotlin/jvm/main"),
            File(projectDir, "composeApp/build/classes/kotlin/main"),
            File(projectDir, "build/classes/kotlin/jvm/main"),
            File(projectDir, "build/classes/kotlin/main")
        )

        val classDir = possibleClassDirs.firstOrNull { it.exists() }

        if (classDir == null) {
            println("✗ ERROR: No se encontro el directorio de clases compiladas")
            println("Directorios buscados:")
            possibleClassDirs.forEach { println("  - ${it.absolutePath}") }
            return
        }

        println("\nSistema operativo: ${System.getProperty("os.name")}")
        println("Java: $javaBin")
        println("Directorio de clases: ${classDir.absolutePath}")

        // Construir classpath completo
        val currentClasspath = System.getProperty("java.class.path")
        val fullClasspath = "${classDir.absolutePath}$pathSeparator$currentClasspath"

        // Verificar que las clases existan
        val producerClassPath = selectedProducer.mainClass.replace(".", "/") + ".class"
        val producerClassFile = File(classDir, producerClassPath)
        val consumerClassFile = File(classDir, "org/dam/project/ConsumidorKt.class")

        println("Verificando clases:")
        println("  Productor: ${if (producerClassFile.exists()) "✓" else "✗"} ${producerClassFile.absolutePath}")
        println("  Consumidor: ${if (consumerClassFile.exists()) "✓" else "✗"} ${consumerClassFile.absolutePath}")

        if (!producerClassFile.exists() || !consumerClassFile.exists()) {
            println("\n✗ ERROR: Algunas clases no estan compiladas")
            println("Ejecuta: ./gradlew build")
            return
        }

        // Determinar la clase principal según el productor seleccionado
        val producerClass = selectedProducer.mainClass

        // Crear proceso productor usando java directamente
        val producerProcess = ProcessBuilder(
            javaBin,
            "-cp",
            fullClasspath,
            producerClass,
            cantidad.toString()
        ).redirectErrorStream(false)
            .start()

        // Crear proceso consumidor usando java directamente
        val consumerProcess = ProcessBuilder(
            javaBin,
            "-cp",
            fullClasspath,
            "org.dam.project.ConsumidorKt"
        ).redirectErrorStream(false)
            .start()

        // Thread para redirigir salida del productor al consumidor
        val pipeThread = Thread {
            try {
                val producerOutput = BufferedReader(InputStreamReader(producerProcess.inputStream))
                val consumerInput = BufferedWriter(OutputStreamWriter(consumerProcess.outputStream))

                var line: String?
                while (producerOutput.readLine().also { line = it } != null) {
                    consumerInput.write(line!! + "\n")
                    consumerInput.flush()
                }
                consumerInput.close()
            } catch (e: Exception) {
                System.err.println("Error en pipe: ${e.message}")
            }
        }
        pipeThread.start()

        // Thread para mostrar errores del productor
        val producerErrorThread = Thread {
            try {
                val errorReader = BufferedReader(InputStreamReader(producerProcess.errorStream))
                var line: String?
                while (errorReader.readLine().also { line = it } != null) {
                    println("[PRODUCTOR] $line")
                }
            } catch (e: Exception) {
                // Ignorar errores de lectura al cerrar
            }
        }
        producerErrorThread.start()

        // Thread para mostrar salida del consumidor
        val consumerOutputThread = Thread {
            try {
                val outputReader = BufferedReader(InputStreamReader(consumerProcess.inputStream))
                var line: String?
                while (outputReader.readLine().also { line = it } != null) {
                    println("[CONSUMIDOR] $line")
                }
            } catch (e: Exception) {
                // Ignorar errores de lectura al cerrar
            }
        }
        consumerOutputThread.start()

        // Thread para mostrar errores del consumidor
        val consumerErrorThread = Thread {
            try {
                val errorReader = BufferedReader(InputStreamReader(consumerProcess.errorStream))
                var line: String?
                while (errorReader.readLine().also { line = it } != null) {
                    println("[CONSUMIDOR ERROR] $line")
                }
            } catch (e: Exception) {
                // Ignorar errores de lectura al cerrar
            }
        }
        consumerErrorThread.start()

        // Esperar a que terminen los procesos
        val producerExitCode = producerProcess.waitFor()
        pipeThread.join()
        val consumerExitCode = consumerProcess.waitFor()

        // Esperar a que terminen los threads de lectura
        producerErrorThread.join(1000)
        consumerOutputThread.join(1000)
        consumerErrorThread.join(1000)

        println("\n" + "-".repeat(60))
        println("Proceso productor finalizado con codigo: $producerExitCode")
        println("Proceso consumidor finalizado con codigo: $consumerExitCode")

        if (producerExitCode == 0 && consumerExitCode == 0) {
            println(" Comunicacion completada exitosamente")
        } else {
            println(" Hubo errores durante la comunicación")
        }

    } catch (e: Exception) {
        println("\n✗ ERROR: ${e.message}")
        e.printStackTrace()
        println("\nAsegúrese de:")
        println("  1. Compilar el proyecto con './gradlew build'")
        println("  2. Ejecutar desde la tarea de Gradle: './gradlew runIPC'")
        println("  3. Verificar que todas las clases estén en el classpath")
    }
}

// Función auxiliar para manejo de errores
fun handleProcessError(process: Process, processName: String) {
    try {
        val errorReader = BufferedReader(InputStreamReader(process.errorStream))
        val errors = errorReader.readText()
        if (errors.isNotEmpty()) {
            println("Errores en $processName:")
            println(errors)
        }
    } catch (e: Exception) {
        println("No se pudieron leer los errores de $processName")
    }
}