pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "2.0.141"
        id("net.neoforged.moddev.repositories") version "2.0.141"
        id("com.diffplug.spotless") version "7.0.1"
    }
}

plugins {
    id("net.neoforged.moddev.repositories")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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
                    includeGroup("de.mari_023")
                    includeGroup("mekanism")
                }
            }

            maven {
                name = "BlameJared"
                url = uri("https://maven.blamejared.com")
                content {
                    includeGroup("com.hollingsworth.ars_nouveau")
                    includeGroup("com.hollingsworth.nuggets")
                    includeGroup("vazkii.botania")
                    includeGroup("vazkii.patchouli")
                }
            }

            maven {
                name = "GeckoLib"
                url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
                content { includeGroup("software.bernie.geckolib") }
            }

            maven {
                name = "Illusive Soulworks"
                url = uri("https://maven.theillusivec4.top/")
                content {
                    includeGroup("com.illusivesoulworks.caelus")
                    includeGroup("top.theillusivec4.curios")
                }
            }

            maven {
                name = "Minecraft Forge"
                url = uri("https://maven.minecraftforge.net/")
                content { includeGroup("com.github.glitchfiend") }
            }

            maven {
                name = "CurseMaven"
                url = uri("https://cursemaven.com")
                content { includeGroup("curse.maven") }
            }
        }

        versionCatalogs {
            val mc = "26.1.2"
            val nf = "$mc.80"
            val maj = mc.substringBeforeLast('.')

            create("core") {
                version("minecraft", mc)
                version("neoforge", nf)
                version("ae2", "$maj.10-beta")
                library("ae2", "org.appliedenergistics", "appliedenergistics2").versionRef("ae2")
            }

            create("integration") {
                // AE2WTLib is the only add-on currently shipping a Minecraft 26.1 build.
                version("ae2wtlib", "$maj.1-beta")
                library("ae2wtlib", "de.mari_023", "ae2wtlib").versionRef("ae2wtlib")
                library("ae2wtlibapi", "de.mari_023", "ae2wtlib_api").versionRef("ae2wtlib")

                // The rest don't have Minecraft 26.1 releases yet. Their integration code compiles
                // against these last-known 1.21.1 jars as compile-only stubs (never on the runtime
                // classpath, gated off by Addons#isLoaded) until each ships its own 26.1 port.
                version("appmek", "1.6.2")
                library("appmek", "curse.maven", "applied-mekanistics-574300").version("5978711")
                library("mekanism", "mekanism", "Mekanism").version("1.21.1-10.7.9.72")

                library("appbot", "curse.maven", "applied-botanics-addon-610632").version("7234122")
                library("botania", "vazkii.botania", "botania-neoforge-1.21.1").version("451-SNAPSHOT")

                // version("arseng", "2.0.5-beta")
                // library("arseng", "curse.maven", "ars-energistique-905641").version("6021072")
                // library("arsnouveau", "com.hollingsworth.ars_nouveau", "ars_nouveau-1.21.1").version("5.10.0.1183")
                // 
                // library("appflux", "curse.maven", "applied-flux-965012").version("5946853")
                // library("glodium", "curse.maven", "glodium-957920").version("5821676")
                // 
                // library("appex", "curse.maven", "applied-experienced-1157608").version("6112629")
                // library("explib", "curse.maven", "experiencelib-1156551").version("5992832")
                // 
                // library("appliede", "curse.maven", "appliede-1009940").version("6430033")
                // library("projecte", "curse.maven", "projecte-226410").version("6301953")
                // 
                // library("appsoul", "curse.maven", "applied-soul-1337114").version("7653184")
                // library("industrialforegoing", "curse.maven", "industrial-foregoing-266515").version("6626624")
                // library("ifsouls", "curse.maven", "industrial-foregoing-souls-904394").version("6235883")
                // library("titanium", "curse.maven", "titanium-287342").version("6875285")
                // library("soulpliedenergistics", "curse.maven", "soulplied-energistics-1143614").version("6771121")

                // ArsEng, AppEx and Applied Soul each have their own storage cell item class
                // (SourceCellItem, ExperienceStorageCell, SoulCellItem) that directly overrides
                // Item#use() with a signature that no longer exists in 26.1, which javac can't route
                // around even as a compile-only stub (see build.gradle.kts).
                // 
                // AppliedE compiles fine as a compile-only stub, but MEGA's own
                // MEGAEMCInterfaceBlockEntity/MEGAEMCInterfacePart directly `extends` AppliedE's own
                // classes, which crashes the whole mod at runtime regardless of Addons#isLoaded gating
                // (see build.gradle.kts/Addons.java), so it stays fully excluded too.
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
