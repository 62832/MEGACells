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

dependencies {
    forge(libs.forge)
    annotationProcessor(variantOf(libs.mixin) { classifier("processor") })

    modImplementation(libs.ae2.forge)

    modCompileOnly(libs.appmek)
    modCompileOnly(libs.mekanism)
    modCompileOnly(variantOf(libs.mekanism) { classifier("generators") })
    // modRuntimeOnly(variantOf(libs.mekanism) { classifier("all") })

    modCompileOnly(libs.appbot.forge)
    modRuntimeOnly(libs.botania.forge)
    modRuntimeOnly(libs.patchouli.forge)

    modImplementation(libs.ae2wtlib.forge)
    modRuntimeOnly(libs.cloth.forge)
    modRuntimeOnly(libs.architectury.forge)
    modRuntimeOnly(libs.curios)

    modRuntimeOnly(libs.jei.forge) { isTransitive = false }
    modRuntimeOnly(libs.jade.forge)
}

tasks.processResources {
    val commonProps: Map<String, *> by extra
    val forgeProps = mapOf(
            "appmekVersion" to libs.versions.appmek.get(),
            "loaderVersion" to libs.forge.get().version!!.substringAfter('-').substringBefore('.'),
            "ae2VersionEnd" to libs.versions.ae2.get().substringBefore('.').toInt() + 1
    )

    inputs.properties(commonProps)
    inputs.properties(forgeProps)

    filesMatching("META-INF/mods.toml") {
        expand(commonProps + forgeProps)
    }
}
