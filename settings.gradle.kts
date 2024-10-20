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
                    includeGroup("mezz.jei")
                }
            }

            maven {
                name = "BlameJared"
                url = uri("https://maven.blamejared.com")
                content {
                    includeGroup("com.hollingsworth.ars_nouveau")
                    includeGroup("vazkii.patchouli")
                }
            }

            maven {
                name = "GeckoLib"
                url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
                content {
                    includeGroup("software.bernie.geckolib")
                }
            }

            maven {
                name = "Illusive Soulworks"
                url = uri("https://maven.theillusivec4.top/")
                content {
                    includeGroup("com.illusivesoulworks.caelus")
                }
            }

            maven {
                name = "Minecraft Forge"
                url = uri("https://maven.minecraftforge.net/")
                content {
                    includeGroup("com.github.glitchfiend")
                }
            }

            maven {
                name = "OctoStudios"
                url = uri("https://maven.octo-studios.com/releases")
                content {
                    includeGroup("top.theillusivec4.curios")
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

                library("arseng", "maven.modrinth", "ars-energistique").version("smOxCSOc")
                library("arsnouveau", "com.hollingsworth.ars_nouveau", "ars_nouveau-1.21.0").version("5.1.0.736")
            }
        }
    }
}
