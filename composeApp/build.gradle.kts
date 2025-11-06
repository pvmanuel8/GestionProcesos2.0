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
        mainClass = "org.dam.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.dam.project"
            packageVersion = "1.0.0"
        }
    }
}

// Tarea para ejecutar el productor de números
tasks.register<JavaExec>("runProdNum") {
    group = "ipc"
    mainClass.set("org.dam.project.productores.ProdNumKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`

    // Pasar argumentos
    if (project.hasProperty("cantidad")) {
        args(project.property("cantidad"))
    }
}

// Tarea para ejecutar el productor de palabras
tasks.register<JavaExec>("runProdTxt") {
    group = "ipc"
    mainClass.set("org.dam.project.productores.ProdTxtKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`

    if (project.hasProperty("cantidad")) {
        args(project.property("cantidad"))
    }
}

// Tarea para ejecutar el consumidor
tasks.register<JavaExec>("runConsumidor") {
    group = "ipc"
    mainClass.set("org.dam.project.consumidores.ConsumidorKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}

tasks.register<JavaExec>("runIPC") {
    group = "ipc"
    mainClass.set("org.dam.project.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}
