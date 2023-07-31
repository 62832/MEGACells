loom {
    val modId: String by project

    runs {
        create("data") {
            data()
            name("Minecraft Data")

            programArgs("--all", "--mod", modId)
            programArgs("--output", file("src/generated/resources").absolutePath)
            programArgs("--existing", project(":common").file("src/main/resources").absolutePath)
            programArgs("--existing", file("src/main/resources").absolutePath)

            /*
            mods {
                create(modId) {
                    sourceSet(sourceSets.create("data") {
                        val main = sourceSets.main.get()
                        compileClasspath += main.compileClasspath + main.output
                        runtimeClasspath += main.runtimeClasspath + main.output
                    })
                }
            }
             */
        }
    }

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("$modId-common.mixins.json")
        mixinConfig("$modId.mixins.json")
    }
}

repositories {
    maven {
        name = "ModMaven (K4U-NL)"
        url = uri("https://modmaven.dev/")
        content {
            includeGroup("mekanism")
        }
    }

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
        name = "TheIllusiveC4"
        url = uri("https://maven.theillusivec4.top/")
        content {
            includeGroup("top.theillusivec4.curios")
        }
    }
}

val forgeVersion: String by project
val ae2Version: String by project

dependencies {
    val minecraftVersion: String by project

    forge("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    modImplementation("appeng:appliedenergistics2-forge:$ae2Version")
    modImplementation("curse.maven:ae2wtlib-459929:${property("ae2wtFile")}")
    modCompileOnly("curse.maven:appmek-574300:${property("appmekFile")}")
    modCompileOnly("curse.maven:applied-botanics-addon-610632:${property("appbotFile")}")

    val mekanismVersion: String by project
    modCompileOnly("mekanism:Mekanism:1.19.2-$mekanismVersion")
    modCompileOnly("mekanism:Mekanism:1.19.2-$mekanismVersion:generators")
    // modRuntimeOnly("mekanism:Mekanism:$minecraftVersion-$mekanismVersion:all")

    modRuntimeOnly("vazkii.botania:Botania:$minecraftVersion-${property("botaniaVersion")}-FORGE-SNAPSHOT")
    modRuntimeOnly("vazkii.patchouli:Patchouli:$minecraftVersion-${property("patchouliVersion")}-FORGE")

    modRuntimeOnly("dev.architectury:architectury-forge:${property("architecturyVersion")}")
    modRuntimeOnly("me.shedaniel.cloth:cloth-config-forge:${property("clothVersion")}")
    modRuntimeOnly("top.theillusivec4.curios:curios-forge:${property("curiosVersion")}+$minecraftVersion")

    modRuntimeOnly("mezz.jei:jei-$minecraftVersion-forge:${property("jeiVersion")}") {
        isTransitive = false
    }

    modRuntimeOnly("curse.maven:jade-324717:${property("jadeFile")}")
}

tasks.processResources {
    val commonProps: Map<String, *> by extra
    val forgeProps = mapOf(
            "appmekVersion" to project.extra.get("appmekVersion"),
            "loaderVersion" to forgeVersion.substringBefore('.'),
            "ae2VersionEnd" to ae2Version.substringBefore('.').toInt() + 1
    )

    inputs.properties(commonProps)
    inputs.properties(forgeProps)

    filesMatching("META-INF/mods.toml") {
        expand(commonProps + forgeProps)
    }
}
