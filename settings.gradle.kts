pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://maven.minecraftforge.net/") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            plugin("loom", "dev.architectury.loom").version("1.3-SNAPSHOT")
            plugin("architectury", "architectury-plugin").version("3.4-SNAPSHOT")
            plugin("vineflower", "io.github.juuxel.loom-vineflower").version("1.11.0")
            plugin("shadow", "com.github.johnrengelman.shadow").version("8.1.1")
            plugin("spotless", "com.diffplug.spotless").version("6.20.0")

            val minecraftVersion = "1.20.1"
            version("minecraft", minecraftVersion)
            library("minecraft", "com.mojang", "minecraft").versionRef("minecraft")

            library("fabric-loader", "net.fabricmc", "fabric-loader").version("0.14.21")
            library("fabric-api", "net.fabricmc.fabric-api", "fabric-api").version("0.83.1+$minecraftVersion")
            library("forge", "net.minecraftforge", "forge").version("$minecraftVersion-47.1.3")

            version("ae2", "15.0.11")
            library("ae2-fabric", "appeng", "appliedenergistics2-fabric").versionRef("ae2")
            library("ae2-forge", "appeng", "appliedenergistics2-forge").versionRef("ae2")

            version("cloth-config", "11.1.106")
            library("cloth-fabric", "me.shedaniel.cloth", "cloth-config-fabric").versionRef("cloth-config")
            library("cloth-forge", "me.shedaniel.cloth", "cloth-config-forge").versionRef("cloth-config")

            // AE2WTLib
            val ae2wtVersion = "15.0.1-beta"
            version("ae2wtlib", ae2wtVersion)
            library("ae2wtlib-fabric", "maven.modrinth", "applied-energistics-2-wireless-terminals").version("$ae2wtVersion-fabric")
            library("ae2wtlib-forge", "maven.modrinth", "applied-energistics-2-wireless-terminals").version("${ae2wtVersion}g-forge")

            library("architectury-forge", "dev.architectury", "architectury-forge").version("9.1.10")
            library("curios", "top.theillusivec4.curios", "curios-forge").version("5.2.0-beta.3+$minecraftVersion")

            // Applied Botanics
            val appbotVersion = "1.4.2"
            version("appbot", appbotVersion)
            library("appbot-fabric", "maven.modrinth", "applied-botanics").version("$appbotVersion-fabric")
            library("appbot-forge", "maven.modrinth", "applied-botanics").version("$appbotVersion-forge")

            val botaniaVersion = "441"
            val botaniaSnapshot = true
            library("botania-fabric", "vazkii.botania", "Botania").version("$minecraftVersion-$botaniaVersion-FABRIC${if (botaniaSnapshot) "-SNAPSHOT" else ""}")
            library("botania-forge", "vazkii.botania", "Botania").version("$minecraftVersion-$botaniaVersion-FORGE${if (botaniaSnapshot) "-SNAPSHOT" else ""}")

            library("patchouli-forge", "vazkii.patchouli", "Patchouli").version("$minecraftVersion-81-FORGE")

            // Applied Mekanistics
            version("appmek", "1.3.4")
            library("appmek", "maven.modrinth", "applied-mekanistics").versionRef("appmek")
            library("mekanism", "mekanism", "Mekanism").version("1.19.2-10.3.5.474")

            // Runtime mods
            version("jei", "15.2.0.23")
            library("jei-fabric", "mezz.jei", "jei-$minecraftVersion-fabric").versionRef("jei")
            library("jei-forge", "mezz.jei", "jei-$minecraftVersion-forge").versionRef("jei")

            library("jade-fabric", "curse.maven", "jade-324717").version("4653227")
            library("jade-forge", "curse.maven", "jade-324717").version("4654448")

            library("modmenu", "com.terraformersmc", "modmenu").version("7.2.1")
        }
    }
}

include("common")

for (platform in providers.gradleProperty("enabledPlatforms").get().split(',')) {
    include(platform)
}

val modName: String by settings
rootProject.name = modName
