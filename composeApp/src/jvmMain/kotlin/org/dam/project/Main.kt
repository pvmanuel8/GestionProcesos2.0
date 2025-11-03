package org.example

import Consumidor
import ProdFrases
import org.dam.project.PantallaPrincipal
import java.io.PipedInputStream
import java.io.PipedOutputStream
import kotlin.concurrent.thread


fun main() {
    androidx.compose.ui.window.singleWindowApplication {
        PantallaPrincipal()
    }
     // Creamos los pipes y los conectamos
//        val pipeNumerosOut = PipedOutputStream()
//        val pipeNumerosIn = PipedInputStream(pipeNumerosOut)
//
//        val pipeTextoOut = PipedOutputStream()
//        val pipeTextoIn = PipedInputStream(pipeTextoOut)
//
//        val pipeFrasesOut = PipedOutputStream()
//        val pipeFrasesIn = PipedInputStream(pipeFrasesOut)
//
//        val productorFrase = ProdFrases(pipeFrasesOut)
//        val consumidorFrase = Consumidor(pipeFrasesIn)
//
        // Instanciamos los productores y el consumidor
 //       val productorNumeros = ProdNum(pipeNumerosOut)
 //       val productorTexto = ProdTxt(pipeTextoOut)
//        val consumidorNumeros = Consumidor(pipeNumerosIn)
//        val consumidorTexto = Consumidor(pipeTextoIn)
//
        // Thread para Productor de Números
//        thread {
 //           productorNumeros.enviarDatos()
 //       }
//
        // Thread para Productor de Texto
 //       thread {
//            productorNumeros.enviarDatos()
//        }
//
        // Thread para Consumidor de Números
//        thread {
 //           consumidorNumeros.consumirNumeros()
//        }

        // Thread para Consumidor de Texto
 //       thread {
 //           consumidorTexto.consumirTexto()
 //       }

        //Thread para prodcutor frase
//        thread {
//            productorFrase.enviaDatos()
 //       }

 //       thread {
 //           consumidorTexto.consumirNumeros()
 //       }
 //   }
}