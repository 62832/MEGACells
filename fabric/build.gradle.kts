loom {
    runs {
        create("data") {
            client()
            name("Minecraft Data")
            source("data")

            property("fabric-api.datagen")
            property("fabric-api.datagen.modid", "${project.property("modId")}-data")
            property("fabric-api.datagen.output-dir", file("src/generated/resources").absolutePath)
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
    modImplementation(libs.cloth.fabric)
    modImplementation(libs.modmenu)

    modCompileOnly(libs.appbot.fabric) { exclude(group = "dev.emi", module = "emi-fabric") }
    modRuntimeOnly(libs.botania.fabric) { exclude(group = "dev.emi", module = "emi-fabric") }

    // modRuntimeOnly(libs.ae2wtlib.fabric)

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
}
