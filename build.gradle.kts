import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
    java
    `maven-publish`
    alias(libs.plugins.archLoom) apply false
    alias(libs.plugins.architectury)
    alias(libs.plugins.shadow)
    alias(libs.plugins.unifiedPublishing)
    alias(libs.plugins.spotless)
}

val modId: String by project
val minecraftVersion: String = libs.versions.minecraft.get()

val platforms by extra {
    property("enabledPlatforms").toString().split(',')
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

tasks {
    val collectJars by registering(Copy::class) {
        val tasks = subprojects.filter { it.name in platforms }.map { it.tasks.named("remapJar") }
        dependsOn(tasks)
        from(tasks)
        into(buildDir.resolve("libs"))
    }

    assemble {
        dependsOn(collectJars)
    }

    withType<Jar> {
        enabled = false
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = rootProject.libs.plugins.architectury.get().pluginId)
    apply(plugin = rootProject.libs.plugins.archLoom.get().pluginId)
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    base.archivesName.set("$modId-${project.name}")
    version = "${(System.getenv("MEGA_VERSION") ?: "v0.0.0").substring(1)}-$minecraftVersion"
    group = "${property("mavenGroup")}.$modId"
    
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

        val accessWidenerFile = project(":common").file("src/main/resources/$modId.accesswidener")

        if (accessWidenerFile.exists()) {
            accessWidenerPath.set(accessWidenerFile)
        }

        mixin {
            defaultRefmapName.set("$modId-refmap.json")
        }
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
    }

    dependencies {
        "minecraft"(rootProject.libs.minecraft)
        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").officialMojangMappings())
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
            target("src/**/resources/**/*.json")
            targetExclude("src/generated/resources/**")
            prettier().config(mapOf("parser" to "json"))
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven${capitalise(project.name)}") {
                groupId = project.group.toString()
                artifactId = project.base.archivesName.get()
                version = project.version.toString()

                from(components["java"])
            }
        }

        // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
        repositories {
            // Add repositories to publish to here.
        }
    }
}

for (platform in platforms) {
    project(":$platform") {
        apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)
        apply(plugin = rootProject.libs.plugins.unifiedPublishing.get().pluginId)

        architectury {
            platformSetupLoomIde()
            loader(platform)
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

        if (project.version != "0.0.0") {
            unifiedPublishing {
                project {
                    val modVersion = project.version.toString()

                    gameVersions.set(listOf(minecraftVersion))
                    gameLoaders.set(listOf(platform))
                    version.set("$platform-$modVersion")

                    var releaseChannel = "release"
                    var changes = System.getenv("CHANGELOG") ?: "No changelog provided?"

                    if (modVersion.lowercase().contains("alpha")) {
                        releaseChannel = "alpha"
                        changes = "THIS IS AN ALPHA RELEASE, MAKE A BACKUP BEFORE INSTALLING AND FREQUENTLY WHILE PLAYING, AND PLEASE REPORT ANY ISSUE YOU MAY FIND ON THE ISSUE TRACKER.\n\n$changes"
                    } else if (modVersion.lowercase().contains("beta")) {
                        releaseChannel = "beta"
                        changes = "This is a beta release. It is expected to be mostly stable, but in any case please report any issue you may find.\n\n$changes"
                    }

                    releaseType.set(releaseChannel)
                    changelog.set(changes)
                    displayName.set(String.format("%s (%s %s)",
                            modVersion.substring(0, modVersion.lastIndexOf("-")),
                            capitalise(platform),
                            minecraftVersion))

                    mainPublication(project.tasks.getByName("remapJar"))

                    relations {
                        depends {
                            curseforge.set("applied-energistics-2")
                        }

                        optional {
                            curseforge.set("applied-energistics-2-wireless-terminals")
                        }

                        optional {
                            curseforge.set("applied-botanics-addon")
                        }

                        if (platform == "forge") {
                            optional {
                                curseforge.set("applied-mekanistics")
                            }
                        }
                    }

                    val cfToken = System.getenv("CURSEFORGE_TOKEN")

                    if (cfToken != null) {
                        curseforge {
                            token.set(cfToken)
                            id.set("622112")
                        }
                    }
                }
            }
        }
    }
}