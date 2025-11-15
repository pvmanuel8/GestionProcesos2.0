---
### Componentes Clave

- **ProcessBuilder**: Gestión de procesos del sistema
- **Pipes (stdin/stdout)**: Comunicación entre procesos
- **Compose Desktop**: UI declarativa multiplataforma
- **Kotlin Coroutines**: Programación asíncrona
- **Java ImageIO**: Procesamiento de imágenes

---

## 📦 Requisitos

### Software Necesario

- ✅ **Java JDK 17 o superior** ([Descargar](https://www.oracle.com/java/technologies/downloads/))
- ✅ **Gradle 8.0+** (incluido con wrapper)
- ✅ **Git** ([Descargar](https://git-scm.com/))

### Requisitos del Sistema

- **SO:** Windows 10/11, Linux, macOS
- **RAM:** Mínimo 4 GB
- **Espacio:** 500 MB libres

---

## 🚀 Instalación

### Opción 1: Clonar desde GitHub

```bash
# 1. Clonar el repositorio
git clone https://github.com/TU_USUARIO/GestionProcesos.git
cd GestionProcesos

### Uso de la Interfaz

#### 1. Generar 20 números aleatorios
```
Proceso 1 → Parámetros: 20 → Iniciar
```

#### 2. Generar 50 palabras
```
Proceso 2 → Parámetros: 50 → Iniciar
```

#### 3. Procesar una imagen
```
Proceso 3 → Parámetros: C:\ruta\imagen.jpg → Iniciar
```

---

## 🏗️ Arquitectura

### Diagrama de Comunicación

```
┌─────────────────────────────────────────────────┐
│          INTERFAZ GRÁFICA (Compose)             │
│                  IPCApp.kt                      │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│         GESTOR DE PROCESOS                      │
│           ProcessBuilder                        │
└────┬────────────────────────────────────┬───────┘
     │                                     │
     ▼                                     ▼
┌──────────────────┐              ┌──────────────────┐
│   PRODUCTORES    │              │   CONSUMIDOR     │
│                  │   PIPES      │                  │
│  • ProdNum.jar   ├──────────────►│ Consumidor.jar │
│  • ProdTxt.jar   │   (stdin/    │                  │
│  • ProdImg.jar   │    stdout)   │  • Suma números  │
│                  │              │  • Cuenta letras │
│                  │              │  • Proc. imágenes│
└──────────────────┘              └──────────────────┘
```

### Flujo de Ejecución

1. **Usuario** selecciona proceso en UI
2. **UI** lanza ProcessBuilder con el JAR correspondiente
3. **Productor** genera datos y los envía por stdout
4. **Pipe** conecta stdout del productor con stdin del consumidor
5. **Consumidor** recibe, procesa y muestra resultados
6. **UI** actualiza en tiempo real el progreso

---

## 📁 Estructura del Proyecto

```
GestionProcesos/
│
├── composeApp/
│   ├── src/
│   │   └── jvmMain/
│   │       └── kotlin/
│   │           └── org/dam/project/
│   │               ├── ui/
│   │               │   └── IPCApp.kt          # Interfaz gráfica principal
│   │               │
│   │               ├── productores/
│   │               │   ├── ProdNum.kt         # Productor de números
│   │               │   ├── ProdTxt.kt         # Productor de texto
│   │               │   └── ProdImg.kt         # Productor de imágenes
│   │               │
│   │               ├── Consumidor.kt          # Consumidor universal
│   │               │
│   │               └── ejecutables/
│   │                   ├── ProdNum.jar        # 🔄 Sistema de Comunicación entre Procesos (IPC)


> **Proyecto 4: Comunicación entre Procesos usando ProcessBuilder**  
> Módulo: Programación de Servicios y Procesos  
> Ciclo: Desarrollo de Aplicaciones Multiplataforma (DAM)

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Cómo Funciona (Guía Rápida)](#-cómo-funciona-guía-rápida)
- [Tecnologías](#️-tecnologías)
- [Requisitos](#-requisitos)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [Arquitectura](#-arquitectura)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Documentación](#-documentación)
- [Autor](#-autor)

---

## 🎯 Descripción

Sistema de **comunicación entre procesos (IPC)** que demuestra el uso de **ProcessBuilder** y **pipes** para la comunicación interproceso en Java/Kotlin. El proyecto implementa múltiples procesos productores que generan diferentes tipos de datos (números, texto e imágenes) que son procesados por un único proceso consumidor universal.

La aplicación cuenta con una **interfaz gráfica moderna** desarrollada con Compose Desktop que permite:
- Seleccionar y configurar diferentes tipos de procesos productores
- Visualizar en tiempo real el progreso y resultados de la comunicación
- Controlar la ejecución (iniciar, detener, limpiar)
- Procesar y almacenar imágenes

### 🎓 Contexto Académico

Este proyecto fue desarrollado como parte del módulo de **Programación de Servicios y Procesos** del ciclo formativo de grado superior en **Desarrollo de Aplicaciones Multiplataforma (DAM)**, con el objetivo de comprender y aplicar los mecanismos de comunicación entre procesos en sistemas operativos modernos.

---

## ✨ Características

### Procesos Productores

- **🔢 Generador de Números Aleatorios**: Genera números entre 1-100
- **📝 Generador de Texto**: Produce palabras aleatorias de un diccionario
- **🖼️ Procesador de Imágenes**: Procesa archivos de imagen (JPG, PNG, BMP)

### Proceso Consumidor Universal

- **Detección automática** del tipo de dato recibido
- **Suma acumulativa** de números
- **Conteo de letras** en palabras
- **Procesamiento de imágenes** con guardado automático
- **Estadísticas en tiempo real**

### Interfaz Gráfica

- 🎨 **Diseño moderno** con tema oscuro
- 📊 **Barra de progreso** en tiempo real
- 🔄 **Actualización dinámica** de resultados
- 🛑 **Control de procesos**: iniciar, detener, limpiar
- 📜 **Scroll automático** en panel de salida

---

## 🚀 Cómo Funciona (Guía Rápida)



```
1️⃣ ELIGE  → 3️⃣ EJECUTA
```

### Paso 1: Elegir un Proceso

Cuando abras la aplicación verás **3 botones grandes**:

```
┌──────────────────────────────────────┐
│  Proceso 1: Números aleatorios       │ ← Click aquí
└──────────────────────────────────────┘
┌──────────────────────────────────────┐
│  Proceso 2: Texto aleatorio          │
└──────────────────────────────────────┘
┌──────────────────────────────────────┐
│  Proceso 3: Procesador de imágenes   │
└──────────────────────────────────────┘
```

**Haz click en UNO** de ellos. Se pondrá de color **azul turquesa** 🟦

```

### Paso 3: Iniciar

Haz click en el botón verde **"Iniciar proceso"**

```
┌─────────────────┐
│ Iniciar proceso │ ← Click aquí
└─────────────────┘
```

**¿Qué pasa después?**

1. Verás una **barra de progreso** moviéndose (0% → 100%)
2. En la caja grande de abajo aparecerán **resultados en tiempo real**
3. Al final verás un **resumen** con estadísticas

### Ejemplo de Resultado

```
CONSUMIDOR: Recibido número 42 | Suma acumulada: 42
CONSUMIDOR: Recibido número 78 | Suma acumulada: 120
CONSUMIDOR: Recibido número 23 | Suma acumulada: 143

==================================================
RESUMEN FINAL DEL CONSUMIDOR
==================================================
Números procesados: 3
Suma total de números: 143
==================================================
```

### Botones Adicionales

- **Limpiar salida**: Borra lo que hay en pantalla
- **Detener**: Para el proceso si está en ejecución

---

## 🛠️ Tecnologías