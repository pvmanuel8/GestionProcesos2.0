package org.dam.project

import Consumidor
import ProdFrases
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.concurrent.thread
import java.io.PipedInputStream
import java.io.PipedOutputStream
import org.example.*


@Composable
fun PantallaPrincipal() {
    var procesoSeleccionado by remember { mutableStateOf<String?>(null) }

    if (procesoSeleccionado == null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona un proceso:", style = MaterialTheme.typography.h6)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { procesoSeleccionado = "numeros" }, modifier = Modifier.fillMaxWidth()) {
                Text("Procesar Números")
            }
            Spacer(Modifier.height(4.dp))
            Button(onClick = { procesoSeleccionado = "texto" }, modifier = Modifier.fillMaxWidth()) {
                Text("Procesar Texto")
            }
            Spacer(Modifier.height(4.dp))
            Button(onClick = { procesoSeleccionado = "frases" }, modifier = Modifier.fillMaxWidth()) {
                Text("Procesar Frases")
            }
        }
    } else {
        when (procesoSeleccionado) {
            "numeros" -> ProcesoNumerosUI { procesoSeleccionado = null }
            "texto" -> ProcesoTextoUI { procesoSeleccionado = null }
            "frases" -> ProcesoFrasesUI { procesoSeleccionado = null }
        }
    }
}

@Composable
fun ProcesoNumerosUI(onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf<Int?>(null) }
    var procesando by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Introduce números separados por comas:")
        TextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Button(
                onClick = {
                    procesando = true
                    // Ejecutar productor/consumidor con pipes en hilo aparte
                    thread {
                        val pipeOut = PipedOutputStream()
                        val pipeIn = PipedInputStream(pipeOut)

                        val productor = ProdNum(pipeOut)
                        val consumidor = Consumidor(pipeIn)

                        productor.enviarDatos(input)
                        val res = consumidor.consumirNumeros()
                        resultado = res
                        procesando = false
                    }
                },
                enabled = !procesando
            ) {
                Text("Procesar")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onBack, enabled = !procesando) {
                Text("Volver")
            }
        }
        Spacer(Modifier.height(16.dp))
        if (procesando) {
            Text("Procesando...")
        } else if (resultado != null) {
            Text("Resultado: ${resultado}")
        }
    }
}

@Composable
fun ProcesoTextoUI(onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf<Int?>(null) }
    var procesando by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Introduce un texto:")
        TextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Button(
                onClick = {
                    procesando = true
                    thread {
                        val pipeOut = PipedOutputStream()
                        val pipeIn = PipedInputStream(pipeOut)

                        val productor = ProdTxt(pipeOut)
                        val consumidor = Consumidor(pipeIn)

                        productor.enviarTexto(input)
                        val res = consumidor.consumirTexto()
                        resultado = res
                        procesando = false
                    }
                },
                enabled = !procesando
            ) {
                Text("Procesar")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onBack, enabled = !procesando) {
                Text("Volver")
            }
        }
        Spacer(Modifier.height(16.dp))
        if (procesando) {
            Text("Procesando...")
        } else if (resultado != null) {
            Text("Cantidad de palabras: ${resultado}")
        }
    }
}

@Composable
fun ProcesoFrasesUI(onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf<String?> (null) }
    var procesando by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Introduce la cantidad de frases a generar:")
        TextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Button(
                onClick = {
                    procesando = true
                    thread {
                        val pipeOut = PipedOutputStream()
                        val pipeIn = PipedInputStream(pipeOut)

                        val productor = ProdFrases(pipeOut)
                        val consumidor = Consumidor(pipeIn)

                        productor.enviaDatos(input)
                        val buffer = ByteArray(1024)
                        val bytesLeidos = pipeIn.read(buffer)
                        val frasesGeneradas = if (bytesLeidos != -1) {
                            String(buffer, 0, bytesLeidos)
                        } else {
                            "No se recibieron frases"
                        }
                        resultado = frasesGeneradas
                        procesando = false
                    }
                },
                enabled = !procesando
            ) {
                Text("Generar frases")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onBack, enabled = !procesando) {
                Text("Volver")
            }
        }
        Spacer(Modifier.height(16.dp))
        if (procesando) {
            Text("Generando...")
        } else if (resultado != null) {
            Text("Frases:\n${resultado}")
        }
    }
}
