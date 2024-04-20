pluginManagement {
    repositories {
        maven { url = uri("https://maven.neoforged.net/") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            plugin("neogradle", "net.neoforged.gradle.userdev").version("7.0.97")
            plugin("spotless", "com.diffplug.spotless").version("6.23.3")

            library("neoforge", "net.neoforged", "neoforge").version("20.4.196")

            version("ae2", "17.12.0-beta")
            library("ae2", "appeng", "appliedenergistics2-neoforge").versionRef("ae2")

            library("ae2wtlib", "maven.modrinth", "applied-energistics-2-wireless-terminals").version("6VxDDjI8")
            library("curios", "maven.modrinth", "curios").version("1aZiIHQO")
        }
    }
}

rootProject.name = "MEGACells"
