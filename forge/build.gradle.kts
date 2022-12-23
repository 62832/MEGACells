plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    runs {
        create("data") {
            data()
            programArgs("--existing", project(":common").file("src/main/resources").absolutePath)
            programArgs("--existing", file("src/main/resources").absolutePath)
        }
    }

    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

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

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    named("developmentForge").get().extendsFrom(common)
}

dependencies {
    val mcVersion = property("minecraft_version").toString()

    forge("net.minecraftforge:forge:$mcVersion-${property("forge_version")}")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionForge")) { isTransitive = false }

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

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        archiveClassifier.set(null as String?)
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":common").tasks.sourcesJar
        dependsOn(commonSources)
        from(commonSources.get().archiveFile.map { zipTree(it) })
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

publishing {
    publications {
        create<MavenPublication>("mavenForge") {
            artifactId = "${property("mod_id")}-$project.name"
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
    }
}
