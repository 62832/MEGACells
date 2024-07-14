pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "0.1.112"
        id("net.neoforged.moddev.repositories") version "0.1.112"
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
                val mc = "1.21"
                version("minecraft", mc)
                version("neoforge", "21.0.87-beta")
                version("parchment", "2024.06.23")
                
                version("ae2", "19.0.12-alpha")
                library("ae2", "appeng", "appliedenergistics2").versionRef("ae2")

                library("ae2wtlib", "maven.modrinth", "applied-energistics-2-wireless-terminals").version("WyPbb8sE")

                library("appmek", "maven.modrinth", "applied-mekanistics").version("yh6fz02r")
                library("mekanism", "mekanism", "Mekanism").version("$mc-10.6.5.52")
            }
        }
    }
}
