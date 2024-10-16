pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "1.0.11"
        id("net.neoforged.moddev.repositories") version "1.0.11"
        id("com.diffplug.spotless") version "6.25.0"
    }
}

plugins {
    id("net.neoforged.moddev.repositories")
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

run {
    @Suppress("UnstableApiUsage")
    dependencyResolutionManagement {
        repositoriesMode = RepositoriesMode.PREFER_SETTINGS
        rulesMode = RulesMode.PREFER_SETTINGS

        repositories {
            mavenCentral()

            maven {
                name = "ModMaven (K4U-NL)"
                url = uri("https://modmaven.dev/")
                content {
                    includeGroup("appeng")
                    includeGroup("mekanism")
                    includeGroup("de.mari_023")
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

        versionCatalogs {
            create("libs") {
                val mc = "1.21.1"
                version("minecraft", mc)

                val nf = mc.substringAfter('.')
                version("neoforge", "${nf + (if (!nf.contains('.')) ".0" else "")}.66")
                version("parchment", "2024.07.28")

                version("ae2", "19.0.23-beta")
                library("ae2", "appeng", "appliedenergistics2").versionRef("ae2")

                version("ae2wtlib", "19.1.3-beta")
                library("ae2wtlib", "de.mari_023", "ae2wtlib").versionRef("ae2wtlib")
                library("ae2wtlibapi", "de.mari_023", "ae2wtlib_api").versionRef("ae2wtlib")

                library("appmek", "maven.modrinth", "applied-mekanistics").version("GCScYwwI")
                library("mekanism", "mekanism", "Mekanism").version("$mc-10.7.4.60")
            }
        }
    }
}
