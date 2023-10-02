val generated = file("src/generated/resources")

loom {
    val modId: String by project

    runs {
        create("data") {
            data()
            programArgs("--all", "--mod", modId)
            programArgs("--output", generated.absolutePath)
            programArgs("--existing", file("src/main/resources").absolutePath)
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
    modRuntimeOnly(libs.cloth.forge)
    modRuntimeOnly(libs.architectury.forge)
    modRuntimeOnly(libs.curios)

    modRuntimeOnly(libs.jei.forge) { isTransitive = false }
    modRuntimeOnly(libs.jade.forge)
}

sourceSets {
    main {
        resources {
            srcDir(generated)
            exclude("**/.cache")
        }
    }
}

tasks.processResources {
    filesMatching("META-INF/mods.toml") {
        val commonProps: Map<String, *> by extra
        expand(commonProps + mapOf(
                "loaderVersion" to forgeVersion.substringBefore('.'),
                "ae2VersionEnd" to ae2Version.substringBefore('.').toInt() + 1
        ))
    }
}
