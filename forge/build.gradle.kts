loom {
    val modId: String by project

    runs {
        create("data") {
            data()
            name("Minecraft Data")
            source("data")

            programArgs("--all", "--mod", modId)
            programArgs("--output", file("src/generated/resources").absolutePath)
            programArgs("--existing", project(":common").file("src/main/resources").absolutePath)
            programArgs("--existing", file("src/main/resources").absolutePath)
            programArgs("--mixin.config", "$modId.data.mixins.json")
            programArgs("--mixin.config", "$modId.data.forge.mixins.json")

            @Suppress("UnstableApiUsage")
            mods {
                create(modId) {
                    sourceSet("main")
                    sourceSet("data")
                    sourceSet(project(":common").sourceSets.getByName("data"))
                }
            }
        }
    }

    forge {
        mixinConfig("$modId.mixins.json")
        mixinConfig("$modId.forge.mixins.json")
    }

    @Suppress("UnstableApiUsage")
    mixin.add(sourceSets.main.get(), "$modId.forge.refmap.json")
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

    modImplementation(libs.ae2.forge)
    modImplementation(libs.cloth.forge)

    modImplementation(libs.appmek)
    modCompileOnly(libs.mekanism)
    modCompileOnly(variantOf(libs.mekanism) { classifier("generators") })
    modRuntimeOnly(variantOf(libs.mekanism) { classifier("all") })

    modCompileOnly(libs.appbot.forge)
    modRuntimeOnly(libs.botania.forge)
    modRuntimeOnly(libs.patchouli.forge)

    modImplementation(libs.ae2wtlib.forge)
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
