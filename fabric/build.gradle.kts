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

    maven {
        name = "Progwml6"
        url = uri("https://dvs1.progwml6.com/files/maven/")
        content {
            includeGroup("mezz.jei")
        }
    }
}

dependencies {
    val minecraftVersion: String by project

    modImplementation("net.fabricmc:fabric-loader:${property("fabricLoaderVersion")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabricApiVersion")}+$minecraftVersion")

    modImplementation("appeng:appliedenergistics2-fabric:${property("ae2Version")}")
    // modImplementation("curse.maven:ae2wtlib-459929:${property("ae2wtFile")}")

    modImplementation("curse.maven:applied-botanics-addon-610632:${property("appbotFile")}") {
        exclude(group = "dev.emi", module = "emi")
    }

    modRuntimeOnly("vazkii.botania:Botania:$minecraftVersion-${property("botaniaVersion")}-FABRIC") {
        exclude(group = "dev.emi", module = "emi")
    }

    modRuntimeOnly("me.shedaniel.cloth:cloth-config-fabric:${property("clothVersion")}")

    modRuntimeOnly("com.terraformersmc:modmenu:${property("modMenuVersion")}")
    modRuntimeOnly("mezz.jei:jei-$minecraftVersion-fabric:${property("jeiVersion")}")
    modRuntimeOnly("curse.maven:jade-324717:${property("jadeFile")}")
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
