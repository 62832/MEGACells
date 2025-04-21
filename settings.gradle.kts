pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "2.0.74"
        id("net.neoforged.moddev.repositories") version "2.0.74"
        id("com.diffplug.spotless") version "7.0.1"
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
            maven {
                name = "ModMaven (K4U-NL)"
                url = uri("https://modmaven.dev/")
                content {
                    includeGroup("mekanism")
                    includeGroup("de.mari_023")
                }
            }

            maven {
                name = "BlameJared"
                url = uri("https://maven.blamejared.com")
                content {
                    includeGroup("com.hollingsworth.ars_nouveau")
                    includeGroup("vazkii.botania")
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
        }

        versionCatalogs {
            val mc = "1.21.1"
            val maj = mc.substringAfter('.')
            val nf = "${maj + (if (!maj.contains('.')) ".0" else "")}.119"

            create("core") {
                version("minecraft", mc)

                version("neoforge", nf)
                version("parchment", "2024.11.17")

                version("ae2", "19.2.9")
                library("ae2", "org.appliedenergistics", "appliedenergistics2").versionRef("ae2")
            }

            create("integration") {
                version("ae2wtlib", "19.2.2")
                library("ae2wtlib", "de.mari_023", "ae2wtlib").versionRef("ae2wtlib")
                library("ae2wtlibapi", "de.mari_023", "ae2wtlib_api").versionRef("ae2wtlib")

                version("appmek", "1.6.2")
                library("appmek", "curse.maven", "applied-mekanistics-574300").version("5978711")
                library("mekanism", "mekanism", "Mekanism").version("$mc-10.7.9.72")

                library("appbot", "curse.maven", "applied-botanics-addon-610632").version("4904185")
                val botaniaVersion = "446"
                val botaniaSnapshot = false
                library("botania", "vazkii.botania", "Botania").version(
                    "1.20.1-$botaniaVersion-FORGE${if (botaniaSnapshot) "-SNAPSHOT" else ""}"
                )

                version("arseng", "2.0.5-beta")
                library("arseng", "curse.maven", "ars-energistique-905641").version("6021072")
                library("arsnouveau", "com.hollingsworth.ars_nouveau", "ars_nouveau-1.21.1").version("5.3.5.844")

                library("appflux", "curse.maven", "applied-flux-965012").version("5946853")
                library("glodium", "curse.maven", "glodium-957920").version("5821676")

                library("appex", "curse.maven", "applied-experienced-1157608").version("6112629")
                library("explib", "curse.maven", "experiencelib-1156551").version("5992832")

                library("appliede", "curse.maven", "appliede-1009940").version("6430033")
                library("projecte", "curse.maven", "projecte-226410").version("6301953")
            }

            create("testlibs") {
                library("neoforge-test", "net.neoforged", "testframework").version(nf)
                library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").version("5.7.1")
                library("junit-platform", "org.junit.platform", "junit-platform-launcher").version("1.11.4")
                library("assertj", "org.assertj", "assertj-core").version("3.26.0")
            }
        }
    }
}
