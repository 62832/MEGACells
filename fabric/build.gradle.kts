loom {
    runs {
        create("data") {
            client()
            name("Minecraft Data")

            source(sourceSets.create("data") {
                val main = sourceSets.main.get()
                compileClasspath += main.compileClasspath + main.output
                runtimeClasspath += main.runtimeClasspath + main.output
            })

            property("fabric-api.datagen")
            property("fabric-api.datagen.modid", rootProject.property("modId").toString())
            property("fabric-api.datagen.output-dir", project(":common").file("src/generated/resources").absolutePath)
            property("fabric-api.datagen.strict-validation")
        }
    }
}

repositories {
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
        content {
            includeGroup("me.shedaniel.cloth")
            includeGroup("dev.architectury")
        }
    }

    maven {
        name = "BlameJared"
        url = uri("https://maven.blamejared.com")
        content {
            includeGroup("vazkii.botania")
            includeGroup("vazkii.patchouli")
        }
    }

    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
        content {
            includeGroup("com.terraformersmc")
            includeGroup("dev.emi")
        }
    }

    maven {
        name = "LadySnake Libs"
        url = uri("https://maven.ladysnake.org/releases")
        content {
            includeGroup("dev.onyxstudios.cardinal-components-api")
        }
    }

    maven {
        name = "JamiesWhiteShirt"
        url = uri("https://maven.jamieswhiteshirt.com/libs-release/")
        content {
            includeGroup("com.jamieswhiteshirt")
        }
    }

    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io/")
        content {
            includeGroup("com.github.emilyploszaj")
        }
    }
}

dependencies {
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    modImplementation(libs.ae2.fabric)

    modCompileOnly(libs.appbot.fabric) { exclude(group = "dev.emi", module = "emi") }
    modRuntimeOnly(libs.botania.fabric) { exclude(group = "dev.emi", module = "emi") }

    // modRuntimeOnly(libs.ae2wtlib.fabric)
    // modRuntimeOnly(libs.cloth.fabric)

    modRuntimeOnly(libs.modmenu)
    modRuntimeOnly(libs.jei.fabric)
    modRuntimeOnly(libs.jade.fabric)
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            val commonProps: Map<String, *> by extra
            expand(commonProps)
        }
    }

    remapJar {
        injectAccessWidener.set(true)
    }
}
