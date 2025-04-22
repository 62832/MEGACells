plugins {
    eclipse
    idea
    id("net.neoforged.moddev")
    id("com.diffplug.spotless")
}

val modId = "megacells"

base.archivesName = modId
version = if (System.getenv("GITHUB_REF_TYPE") == "tag") System.getenv("GITHUB_REF_NAME") else "0.0.0"
group = "gripe.90"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

sourceSets {
    main {
        resources.srcDir(file("src/generated/resources"))
    }

    val addons = create("addons") {
        val main = main.get()
        compileClasspath += main.compileClasspath + main.output
        runtimeClasspath += main.runtimeClasspath + main.output
    }

    create("data") {
        compileClasspath += addons.compileClasspath + addons.output
        runtimeClasspath += addons.runtimeClasspath + addons.output
    }
}

dependencies {
    api(core.ae2)

    compileOnly(integration.ae2wtlibapi)
    "addonsRuntimeOnly"(integration.ae2wtlib)

    compileOnly(integration.appmek)
    compileOnly(integration.mekanism)
    "addonsRuntimeOnly"(integration.appmek)
    "dataCompileOnly"(variantOf(integration.mekanism) { classifier("generators") })
    "addonsRuntimeOnly"(variantOf(integration.mekanism) { classifier("all") })

    compileOnly(integration.arseng)
    "addonsRuntimeOnly"(integration.arseng)

    "dataCompileOnly"(integration.arsnouveau) { exclude("mezz.jei") }
    "addonsRuntimeOnly"(integration.arsnouveau) { exclude("mezz.jei") }

    compileOnly(integration.appflux)
    "addonsRuntimeOnly"(integration.appflux)
    "addonsRuntimeOnly"(integration.glodium)

    compileOnly(integration.appex)
    "addonsRuntimeOnly"(integration.appex)
    "addonsRuntimeOnly"(integration.explib)

    compileOnly(integration.appliede)
    "addonsRuntimeOnly"(integration.appliede)
    "addonsRuntimeOnly"(integration.projecte)

    compileOnly(integration.appbot)
    "addonsCompileOnly"(integration.botania)

    testImplementation(testlibs.junit.jupiter)
    testImplementation(testlibs.assertj)
    testImplementation(testlibs.neoforge.test)
    testRuntimeOnly(testlibs.junit.platform)
}

neoForge {
    version = core.versions.neoforge.get()

    parchment {
        minecraftVersion = core.versions.minecraft.get()
        mappingsVersion = core.versions.parchment.get()
    }
    
    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("data"))
        }
    }

    runs {
        val main = file("src/main/resources").absolutePath

        configureEach {
            logLevel = org.slf4j.event.Level.DEBUG
            sourceSet = sourceSets.getByName("addons")
        }

        create("client") {
            client()
            gameDirectory = file("run/client")
            systemProperty("guideme.ae2.guide.sources", "$main/assets/$modId/ae2guide")
            systemProperty("guideme.ae2.guide.sourcesNamespace", modId)
        }

        create("server") {
            server()
            gameDirectory = file("run/server")
        }

        create("data") {
            data()
            gameDirectory = file("run/data")
            logLevel = org.slf4j.event.Level.INFO
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", main,
                "--existing", "$main/optional_cell_colours",
                "--existing-mod", "ae2"
            )
            sourceSet = sourceSets.getByName("data")
        }
    }

    unitTest {
        enable()
        testedMod = mods.getByName(modId)
    }
}

tasks {
    jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_$modId" }
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        exclude("**/.cache")

        val props = mapOf("version" to version)
        inputs.properties(props)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(props)
        }
    }

    test {
        useJUnitPlatform()
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

spotless {
    kotlinGradle {
        target("*.kts")
        diktat()
        leadingTabsToSpaces(4)
        endWithNewline()
    }

    java {
        target("/src/**/java/**/*.java")
        endWithNewline()
        leadingTabsToSpaces(4)
        removeUnusedImports()
        palantirJavaFormat()
        importOrderFile(file("mega.importorder"))
        toggleOffOn()
        trimTrailingWhitespace()

        // courtesy of diffplug/spotless#240
        // https://github.com/diffplug/spotless/issues/240#issuecomment-385206606
        // also, ew (7.x): https://github.com/diffplug/spotless/issues/2387#issuecomment-2576459901
        custom("noWildcardImports", object : java.io.Serializable, com.diffplug.spotless.FormatterFunc {
            override fun apply(input: String): String {
                if (input.contains("*;\n")) {
                    throw GradleException("No wildcard imports allowed.")
                }

                return input
            }
        })

        bumpThisNumberIfACustomStepChanges(1)
    }

    json {
        target("src/**/resources/**/*.json")
        targetExclude("src/generated/resources/**")
        biome()
        leadingTabsToSpaces(2)
        endWithNewline()
    }
}
