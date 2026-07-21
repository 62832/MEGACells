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

        versionCatalogs {
            val nf = "26.1.2.80"

            create("core") {
                version("minecraft", "26.1.2")
                version("neoforge", nf)
                version("ae2", "26.1.10-beta")
                library("ae2", "org.appliedenergistics", "appliedenergistics2").versionRef("ae2")
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
