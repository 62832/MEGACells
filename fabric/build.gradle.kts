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
            property("fabric-api.datagen.modid", rootProject.property("mod_id").toString())
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
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
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
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}+${property("minecraft_version")}")

    modImplementation("appeng:appliedenergistics2-fabric:${property("ae2_version")}")
    modImplementation("curse.maven:ae2wtlib-459929:${property("ae2wt_fileid")}")

    modImplementation("curse.maven:applied-botanics-addon-610632:${property("appbot_fileid")}") {
        exclude(group = "dev.emi", module = "emi")
    }

    modRuntimeOnly("vazkii.botania:Botania:${property("minecraft_version")}-${property("botania_version")}-FABRIC") {
        exclude(group = "dev.emi", module = "emi")
    }

    modRuntimeOnly("me.shedaniel.cloth:cloth-config-fabric:${property("cloth_version")}")

    modRuntimeOnly("com.terraformersmc:modmenu:${property("mod_menu_version")}")
    modRuntimeOnly("mezz.jei:jei-${property("minecraft_version")}-fabric:${property("jei_version")}")
    modRuntimeOnly("curse.maven:jade-324717:${property("jade_fileid")}")
}

tasks.remapJar {
    injectAccessWidener.set(true)
}
