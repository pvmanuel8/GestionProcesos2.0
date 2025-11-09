import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.dam.project.ui.IPCAppKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.dam.project"
            packageVersion = "1.0.0"
        }
    }
}

// Tarea para crear JAR del productor de números
tasks.register<Jar>("createProdNumJar") {
    group = "ipc"
    archiveFileName.set("ProdNum.jar")
    destinationDirectory.set(file("src/jvmMain/kotlin/org/dam/project/ejecutables"))

    val jvmMain = kotlin.jvm().compilations.getByName("main")
    from(jvmMain.output)

    manifest {
        attributes["Main-Class"] = "org.dam.project.ProdNumKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Incluir dependencias de runtime de JVM
    from({
        jvmMain.runtimeDependencyFiles.map { if (it.isDirectory) it else zipTree(it) }
    })
}

// Tarea para crear JAR del productor de texto
tasks.register<Jar>("createProdTxtJar") {
    group = "ipc"
    archiveFileName.set("ProdTxt.jar")
    destinationDirectory.set(file("src/jvmMain/kotlin/org/dam/project/ejecutables"))

    val jvmMain = kotlin.jvm().compilations.getByName("main")
    from(jvmMain.output)

    manifest {
        attributes["Main-Class"] = "org.dam.project.ProdTxtKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from({
        jvmMain.runtimeDependencyFiles.map { if (it.isDirectory) it else zipTree(it) }
    })
}

// Tarea para crear JAR del consumidor
tasks.register<Jar>("createConsumidorJar") {
    group = "ipc"
    archiveFileName.set("Consumidor.jar")
    destinationDirectory.set(file("src/jvmMain/kotlin/org/dam/project/ejecutables"))

    val jvmMain = kotlin.jvm().compilations.getByName("main")
    from(jvmMain.output)

    manifest {
        attributes["Main-Class"] = "org.dam.project.ConsumidorKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from({
        jvmMain.runtimeDependencyFiles.map { if (it.isDirectory) it else zipTree(it) }
    })
}

// Tarea para crear todos los JARs de una vez
tasks.register("createAllJars") {
    group = "ipc"
    dependsOn("createProdNumJar", "createProdTxtJar", "createConsumidorJar")
}
