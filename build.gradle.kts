import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
    java
    `maven-publish`
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.architectury)
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotless)
}

val modId: String by project
val modVersion = (System.getenv("MEGA_VERSION") ?: "v0.0.0").substring(1)
val minecraftVersion = libs.versions.minecraft.get()

val platforms by extra {
    property("enabledPlatforms").toString().split(',')
}

tasks {
    register("releaseInfo") {
        doLast {
            val output = System.getenv("GITHUB_OUTPUT")

            if (!output.isNullOrEmpty()) {
                val outputFile = File(output)
                outputFile.appendText("MOD_VERSION=$modVersion\n")
                outputFile.appendText("MINECRAFT_VERSION=$minecraftVersion\n")
            }
        }
    }

    withType<Jar> {
        enabled = false
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = rootProject.libs.plugins.architectury.get().pluginId)
    apply(plugin = rootProject.libs.plugins.loom.get().pluginId)
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    base.archivesName.set("$modId-${project.name}")
    version = "$modVersion-$minecraftVersion"
    group = property("mavenGroup").toString()
    
    val javaVersion: String by project

    java {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
        targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")

        withSourcesJar()
    }

    architectury {
        minecraft = minecraftVersion
        injectInjectables = false
    }

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()

        @Suppress("UnstableApiUsage")
        mixin.defaultRefmapName.set("$modId.refmap.json")
    }

    repositories {
        maven {
            name = "ModMaven (K4U-NL)"
            url = uri("https://modmaven.dev/")
            content {
                includeGroup("appeng")
                includeGroup("mezz.jei")
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
            name = "Modrinth Maven"
            url = uri("https://api.modrinth.com/maven")
            content {
                includeGroup("maven.modrinth")
            }
        }
    }

    dependencies {
        "minecraft"(rootProject.libs.minecraft)
        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").officialMojangMappings())
    }

    sourceSets {
        create("data") {
            val main = sourceSets.main.get()
            compileClasspath += main.compileClasspath + main.output
            runtimeClasspath += main.runtimeClasspath + main.output
        }
    }

    tasks {
        jar {
            from(rootProject.file("LICENSE")) {
                rename { "${it}_$modId"}
            }
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(javaVersion.toInt())
        }
    }

    spotless {
        kotlinGradle {
            target("*.kts")
            diktat()
        }

        java {
            target("src/**/java/**/*.java")
            endWithNewline()
            indentWithSpaces(4)
            removeUnusedImports()
            palantirJavaFormat()
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
            target("src/**/resources/**/*.json")
            targetExclude("src/generated/resources/**")
            biome()
            indentWithSpaces(2)
            endWithNewline()
        }
    }
}

for (platform in platforms) {
    project(":$platform") {
        apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)

        architectury {
            platformSetupLoomIde()
            loader(platform)
        }

        fun capitalise(str: String): String {
            return str.replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase()
                } else {
                    it.toString()
                }
            }
        }

        val common: Configuration by configurations.creating
        val shadowCommon: Configuration by configurations.creating

        configurations {
            compileClasspath.get().extendsFrom(common)
            runtimeClasspath.get().extendsFrom(common)
            getByName("development${capitalise(platform)}").extendsFrom(common)
        }

        dependencies {
            common(project(path = ":common", configuration = "namedElements")) {
                isTransitive = false
            }

            shadowCommon(project(path = ":common", configuration = "transformProduction${capitalise(platform)}")) {
                isTransitive = false
            }
        }

        sourceSets {
            main {
                resources {
                    srcDir(file("src/generated/resources"))
                    exclude("**/.cache")
                }
            }

            getByName("data") {
                val commonData = project(":common").sourceSets.getByName("data")
                compileClasspath += commonData.output
                runtimeClasspath += commonData.output
            }
        }

        tasks {
            processResources {
                val commonProps by extra { mapOf(
                        "version"           to project.version,
                        "minecraftVersion"  to minecraftVersion,
                        "ae2Version"        to rootProject.libs.versions.ae2.get(),
                        "ae2wtVersion"      to rootProject.libs.versions.ae2wtlib.get(),
                        "appbotVersion"     to rootProject.libs.versions.appbot.get())
                }

                inputs.properties(commonProps)
            }

            shadowJar {
                exclude("architectury.common.json")
                configurations = listOf(shadowCommon)
                archiveClassifier.set("dev-shadow")
            }

            withType<RemapJarTask> {
                inputFile.set(shadowJar.get().archiveFile)
                dependsOn(shadowJar)
                archiveClassifier.set(null as String?)
            }

            jar {
                archiveClassifier.set("dev")
            }

            getByName<Jar>("sourcesJar") {
                val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
                dependsOn(commonSources)
                from(commonSources.archiveFile.map { zipTree(it) })
            }
        }

        val javaComponent = components["java"] as AdhocComponentWithVariants
        javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
            skip()
        }
    }
}
