loom {
    runs {
        create("data") {
            data()
            programArgs("--existing", file("src/main/resources").absolutePath)
        }
    }

    forge {
        val modId = property("mod_id").toString()

        dataGen {
            mod(modId)
        }

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

    maven {
        name = "Progwml6"
        url = uri("https://dvs1.progwml6.com/files/maven/")
        content {
            includeGroup("mezz.jei")
        }
    }
}

dependencies {
    val mcVersion = property("minecraft_version").toString()

    forge("net.minecraftforge:forge:$mcVersion-${property("forge_version")}")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    modImplementation("appeng:appliedenergistics2-forge:${property("ae2_version")}")
    modImplementation("curse.maven:ae2wtlib-459929:${property("ae2wt_fileid")}")
    modImplementation("curse.maven:appmek-574300:${property("appmek_fileid")}")
    modImplementation("curse.maven:applied-botanics-addon-610632:${property("appbot_fileid")}")

    val mekVersion = property("mekanism_version").toString()
    modCompileOnly("mekanism:Mekanism:$mcVersion-$mekVersion")
    modCompileOnly("mekanism:Mekanism:$mcVersion-$mekVersion:generators")
    modRuntimeOnly("mekanism:Mekanism:$mcVersion-$mekVersion:all")

    modRuntimeOnly("vazkii.botania:Botania:$mcVersion-${property("botania_version")}-FORGE")
    modRuntimeOnly("vazkii.patchouli:Patchouli:$mcVersion-${property("patchouli_version")}")

    modRuntimeOnly("dev.architectury:architectury-forge:${property("architectury_version")}")
    modRuntimeOnly("me.shedaniel.cloth:cloth-config-forge:${property("cloth_version")}")
    modRuntimeOnly("top.theillusivec4.curios:curios-forge:$mcVersion-${property("curios_version")}")

    modRuntimeOnly("mezz.jei:jei-$mcVersion-forge:${property("jei_version")}") { isTransitive = false }
    modRuntimeOnly("curse.maven:jade-324717:${property("jade_fileid")}")
}

sourceSets {
    main {
        resources {
            exclude("**/.cache")
        }
    }
}
