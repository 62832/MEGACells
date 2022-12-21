import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    base
    java
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.0-SNAPSHOT" apply false
    id("io.github.juuxel.loom-quiltflower") version "1.7.1" apply false
    id("me.shedaniel.unified-publishing") version "0.1.+" apply false
    id("com.diffplug.spotless") version "6.4.1"
}

val mcVersion = property("minecraft_version").toString()
val modId = property("mod_id").toString()

architectury {
    minecraft = mcVersion
}

tasks {
    val collectJars by registering(Copy::class) {
        val tasks = subprojects.filter { it.path != ":common" }.map { it.tasks.named("remapJar") }
        dependsOn(tasks)
        from(tasks)
        into(buildDir.resolve("libs"))
    }

    assemble {
        dependsOn(collectJars)
    }
}

allprojects {
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.diffplug.spotless")

    spotless {
        java {
            target("src/**/java/**/*.java")
            endWithNewline()
            indentWithSpaces()
            removeUnusedImports()
            toggleOffOn()
            eclipse().configFile(rootProject.file("codeformat/codeformat.xml"))
            importOrderFile(rootProject.file("codeformat/mega.importorder"))

            // courtesy of diffplug/spotless#240
            // https://github.com/diffplug/spotless/issues/240#issuecomment-385206606
            custom("noWildcardImports") {
                if (it.contains("*;\n")) {
                    throw Error("No wildcard imports allowed")
                }
                it
            }
            bumpThisNumberIfACustomStepChanges(1)
        }

        json {
            target("src/*/resources/**/*.json")
            targetExclude("src/generated/resources/**")
            prettier().config(mapOf("parser" to "json"))
        }
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "io.github.juuxel.loom-quiltflower")
    apply(plugin = "me.shedaniel.unified-publishing")

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "ModMaven (K4U-NL)"
            url = uri("https://modmaven.dev/")
            content {
                includeGroup("appeng")
                includeGroup("mekanism")
            }
        }

        maven {
            name = "CurseMaven"
            url = uri("https://cursemaven.com")
            content {
                includeGroup("curse.maven")
            }
        }

        maven {
            name = "Modrinth Maven"
            url = uri("https://api.modrinth.com/maven")
            content {
                includeGroup("maven.modrinth")
            }
        }

        maven {
            name = "Progwml6"
            url = uri("https://dvs1.progwml6.com/files/maven/")
            content {
                includeGroup("mezz.jei")
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
    }

    base.archivesName.set("$modId-$project.name")
    version = "${(System.getenv("MEGA_VERSION") ?: "v0.0.0").substring(1)}-$mcVersion"
    group = "${property("maven_group")}-$modId"

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
        mixin {
            defaultRefmapName.set("${base.archivesName}-refmap.json")
        }
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:$mcVersion")
        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").officialMojangMappings())
    }

    architectury {
        injectInjectables = false
    }

    tasks {
        jar {
            from("LICENSE") {
                rename { "${it}_${base.archivesName}"}
            }
        }

        withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(17)
        }
    }

    java {
        withSourcesJar()
    }
}