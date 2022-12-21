plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    runs {
        create("data") {
            client()
            name("Minecraft Data")
            property("fabric-api.datagen")
            property("fabric-api.datagen.modid", property("mod_id").toString())
            property("fabric-api.datagen.output-dir", file("src/generated/resources").absolutePath)
            property("fabric-api.datagen.strict-validation")
        }
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    named("developmentFabric").get().extendsFrom(common)
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}+${property("minecraft_version")}")

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }

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

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
            exclude("**/.cache")
        }
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        archiveClassifier.set("")
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
        create<MavenPublication>("mavenFabric") {
            artifactId = "${property("mod_id")}-$project.name"
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
    }
}
