package org.dam.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.File

// Colores del tema oscuro
val DarkBackground = Color(0xFF2D3436)
val CardBackground = Color(0xFF3A3F41)
val AccentTeal = Color(0xFF4A9B9B)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B0)

data class ProcessOption(
    val id: Int,
    val name: String,
    val className: String
)

data class ProcessState(
    val isRunning: Boolean = false,
    val progress: Float = 0f,
    val statusText: String = "Listo para iniciar",
    val outputData: String = "",
    val executionTime: Float = 0f
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sistema de Comunicación entre Procesos (IPC)",
        state = rememberWindowState(width = 900.dp, height = 700.dp)
    ) {
        IPCApp()
    }
}

@Composable
fun IPCApp() {
    var selectedProcess by remember { mutableStateOf(1) }
    var inputParams by remember { mutableStateOf("") }
    var processState by remember { mutableStateOf(ProcessState()) }
    var producerProcess by remember { mutableStateOf<Process?>(null) }
    var consumerProcess by remember { mutableStateOf<Process?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val processes = listOf(
        ProcessOption(1, "Generador de números aleatorios", "org.dam.project.ProdNumKt"),
        ProcessOption(2, "Generador de texto aleatorio", "org.dam.project.ProdTxtKt")
    )

    MaterialTheme(
        colors = darkColors(
            primary = AccentTeal,
            background = DarkBackground,
            surface = CardBackground
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Título
                Text(
                    text = "SISTEMA DE COMUNICACIÓN ENTRE PROCESOS (IPC)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Procesos disponibles
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CardBackground,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Procesos disponibles:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        processes.forEach { process ->
                            ProcessButton(
                                text = "Proceso ${process.id}: ${process.name}",
                                isSelected = selectedProcess == process.id,
                                onClick = { selectedProcess = process.id }
                            )
                        }
                    }
                }

                // Parámetros de entrada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CardBackground,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Parámetros de entrada (opcional):",
                            fontSize = 16.sp,
                            color = TextPrimary
                        )

                        OutlinedTextField(
                            value = inputParams,
                            onValueChange = { inputParams = it },
                            placeholder = { Text("Ejemplo: cantidad de datos, texto base, etc.", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = TextPrimary,
                                cursorColor = AccentTeal,
                                focusedBorderColor = AccentTeal,
                                unfocusedBorderColor = TextSecondary
                            )
                        )
                    }
                }

                // Botones de control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            println("DEBUG: Botón presionado")
                            println("DEBUG: Proceso seleccionado: $selectedProcess")
                            println("DEBUG: Parámetros: ${inputParams.ifEmpty { "10" }}")

                            coroutineScope.launch {
                                try {
                                    val procs = executeProcess(
                                        processes.find { it.id == selectedProcess }!!,
                                        inputParams.ifEmpty { "10" },
                                        processState
                                    ) { newState ->
                                        println("DEBUG: Actualizando estado: ${newState.statusText}")
                                        processState = newState
                                    }
                                    producerProcess = procs.first
                                    consumerProcess = procs.second
                                } catch (e: Exception) {
                                    println("ERROR: ${e.message}")
                                    e.printStackTrace()
                                    processState = processState.copy(
                                        isRunning = false,
                                        statusText = "Error: ${e.message}",
                                        outputData = "ERROR: ${e.message}\n${e.stackTraceToString()}"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = AccentTeal),
                        enabled = !processState.isRunning,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Iniciar proceso", color = TextPrimary, fontSize = 16.sp)
                    }

                    OutlinedButton(
                        onClick = { processState = processState.copy(outputData = "") },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Limpiar salida", color = TextPrimary, fontSize = 16.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                producerProcess?.destroy()
                                consumerProcess?.destroy()
                                processState = processState.copy(
                                    isRunning = false,
                                    statusText = "Proceso detenido por el usuario"
                                )
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        enabled = processState.isRunning,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Detener", color = if (processState.isRunning) Color.Red else TextPrimary, fontSize = 16.sp)
                    }
                }

                // Estado del proceso
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CardBackground,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Estado del proceso:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        // Barra de progreso
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = processState.progress,
                                modifier = Modifier.weight(1f).height(24.dp),
                                color = AccentTeal,
                                backgroundColor = DarkBackground
                            )
                            Text(
                                text = "${(processState.progress * 100).toInt()}%",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Estado: ${processState.statusText}",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Salida del consumidor con scroll
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    backgroundColor = CardBackground,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Salida del consumidor:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        // ScrollState para hacer scroll automático
                        val scrollState = rememberScrollState()

                        // Auto-scroll al final cuando cambie el contenido
                        LaunchedEffect(processState.outputData) {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkBackground, RoundedCornerShape(8.dp))
                                .padding(16.dp)
                                .verticalScroll(scrollState)
                        ) {
                            Text(
                                text = processState.outputData.ifEmpty { "Sin datos aún..." },
                                fontSize = 14.sp,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) AccentTeal else CardBackground.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 16.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

suspend fun executeProcess(
    process: ProcessOption,
    params: String,
    currentState: ProcessState,
    onStateChange: (ProcessState) -> Unit
): Pair<Process?, Process?> = withContext(Dispatchers.IO) {
    println("DEBUG: Iniciando executeProcess")
    println("DEBUG: Proceso ID: ${process.id}")
    println("DEBUG: Parámetros: $params")

    val startTime = System.currentTimeMillis()

    withContext(Dispatchers.Main) {
        onStateChange(currentState.copy(
            isRunning = true,
            progress = 0.1f,
            statusText = "Iniciando procesos...",
            outputData = "Iniciando comunicación IPC...\n"
        ))
    }

    try {
        // Ruta a los JARs ejecutables (igual que PipeManager)
        val baseDir = File(
            System.getProperty("user.dir"),
            "composeApp/src/jvmMain/kotlin/org/dam/project/ejecutables"
        )

        println("DEBUG: BASE_DIR = ${baseDir.absolutePath}")

        if (!baseDir.exists()) {
            withContext(Dispatchers.Main) {
                onStateChange(currentState.copy(
                    isRunning = false,
                    statusText = "Error: No existe carpeta de ejecutables",
                    outputData = "ERROR: No existe el directorio de ejecutables\n" +
                            "Buscado en: ${baseDir.absolutePath}\n\n" +
                            "Asegúrate de tener los JARs compilados."
                ))
            }
            return@withContext Pair(null, null)
        }

        // Seleccionar el JAR del productor según el proceso
        val nombreJarProductor = when (process.id) {
            1 -> "ProdNum.jar"
            2 -> "ProdTxt.jar"
            else -> "ProdNum.jar"
        }

        val jarProductor = File(baseDir, nombreJarProductor)
        val jarConsumidor = File(baseDir, "Consumidor.jar")

        println("DEBUG: JAR Productor: ${jarProductor.absolutePath}")
        println("DEBUG: Existe productor? ${jarProductor.exists()}")
        println("DEBUG: JAR Consumidor: ${jarConsumidor.absolutePath}")
        println("DEBUG: Existe consumidor? ${jarConsumidor.exists()}")

        if (!jarProductor.exists()) {
            withContext(Dispatchers.Main) {
                onStateChange(currentState.copy(
                    isRunning = false,
                    statusText = "Error: JAR productor no encontrado",
                    outputData = "ERROR: No se encontró ${nombreJarProductor}\n" +
                            "Buscado en: ${jarProductor.absolutePath}\n\n" +
                            "Compila los JARs ejecutables primero."
                ))
            }
            return@withContext Pair(null, null)
        }

        if (!jarConsumidor.exists()) {
            withContext(Dispatchers.Main) {
                onStateChange(currentState.copy(
                    isRunning = false,
                    statusText = "Error: JAR consumidor no encontrado",
                    outputData = "ERROR: No se encontró Consumidor.jar\n" +
                            "Buscado en: ${jarConsumidor.absolutePath}\n\n" +
                            "Compila los JARs ejecutables primero."
                ))
            }
            return@withContext Pair(null, null)
        }

        withContext(Dispatchers.Main) {
            onStateChange(currentState.copy(
                isRunning = true,
                progress = 0.2f,
                statusText = "Ejecutando proceso productor...",
                outputData = "JARs encontrados\nIniciando productor: $nombreJarProductor\n"
            ))
        }

        // Crear comando del productor
        val cmdProductor = mutableListOf("java", "-jar", jarProductor.absolutePath)
        if (params.isNotEmpty()) {
            cmdProductor.add(params)
        }

        println("DEBUG: Comando productor: ${cmdProductor.joinToString(" ")}")

        // Crear proceso productor
        val producerProcess = ProcessBuilder(cmdProductor)
            .redirectErrorStream(true)
            .start()

        withContext(Dispatchers.Main) {
            onStateChange(currentState.copy(
                isRunning = true,
                progress = 0.3f,
                statusText = "Ejecutando proceso consumidor...",
                outputData = "Productor iniciado\nIniciando consumidor...\n"
            ))
        }

        // Crear proceso consumidor
        val consumerProcess = ProcessBuilder("java", "-jar", jarConsumidor.absolutePath)
            .redirectErrorStream(true)
            .start()

        withContext(Dispatchers.Main) {
            onStateChange(currentState.copy(
                isRunning = true,
                progress = 0.4f,
                statusText = "Conectando procesos mediante pipe...",
                outputData = "Consumidor iniciado\nEstableciendo comunicación via Pipe...\n"
            ))
        }

        // Conectar productor con consumidor mediante pipe (igual que PipeManager)
        val pipeJob = launch(Dispatchers.IO) {
            try {
                producerProcess.inputStream.use { inputStream ->
                    consumerProcess.outputStream.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                println("DEBUG: Pipe cerrado correctamente")
            } catch (e: Exception) {
                println("ERROR en pipe: ${e.message}")
                e.printStackTrace()
            }
        }

        withContext(Dispatchers.Main) {
            onStateChange(currentState.copy(
                isRunning = true,
                progress = 0.5f,
                statusText = "Comunicación establecida. Procesando datos...",
                outputData = "Comunicación establecida via Pipe\n\n--- Salida del Consumidor ---\n"
            ))
        }

        // Leer salida del consumidor
        val outputBuilder = StringBuilder()
        outputBuilder.append("Comunicación establecida via Pipe\n\n--- Salida del Consumidor ---\n")

        val reader = BufferedReader(InputStreamReader(consumerProcess.inputStream))

        var line: String?
        var progress = 0.5f
        var lineCount = 0
        while (reader.readLine().also { line = it } != null) {
            val currentLine = line!!
            println("CONSUMIDOR OUTPUT: $currentLine")
            outputBuilder.append(currentLine).append("\n")
            lineCount++
            progress = (0.5f + (lineCount * 0.05f)).coerceAtMost(0.95f)

            val elapsed = (System.currentTimeMillis() - startTime) / 1000f

            withContext(Dispatchers.Main) {
                onStateChange(currentState.copy(
                    isRunning = true,
                    progress = progress,
                    outputData = outputBuilder.toString(),
                    executionTime = elapsed,
                    statusText = "Procesando datos... ($lineCount líneas recibidas)"
                ))
            }
        }

        println("DEBUG: Esperando a que terminen los procesos")
        pipeJob.join()

        val producerExit = producerProcess.waitFor()
        val consumerExit = consumerProcess.waitFor()

        println("DEBUG: Productor terminó con código: $producerExit")
        println("DEBUG: Consumidor terminó con código: $consumerExit")

        val totalTime = (System.currentTimeMillis() - startTime) / 1000f

        outputBuilder.append("\n--- Proceso Completado ---\n")
        outputBuilder.append("Tiempo de ejecución: ${String.format("%.2f", totalTime)}s\n")
        outputBuilder.append("Código de salida productor: $producerExit\n")
        outputBuilder.append("Código de salida consumidor: $consumerExit\n")
        outputBuilder.append("Comunicación: Establecida via Pipe\n")

        withContext(Dispatchers.Main) {
            onStateChange(currentState.copy(
                isRunning = false,
                progress = 1f,
                outputData = outputBuilder.toString(),
                executionTime = totalTime,
                statusText = if (producerExit == 0 && consumerExit == 0)
                    "✓ Proceso completado exitosamente"
                else
                    "✗ Proceso terminó con errores"
            ))
        }

        return@withContext Pair(producerProcess, consumerProcess)

    } catch (e: Exception) {
        println("ERROR GENERAL: ${e.message}")
        e.printStackTrace()

        withContext(Dispatchers.Main) {
            onStateChange(currentState.copy(
                isRunning = false,
                statusText = "Error: ${e.message}",
                outputData = "ERROR: ${e.message}\n\nStack trace:\n${e.stackTraceToString()}"
            ))
        }

        return@withContext Pair(null, null)
    }
}