plugins {
    eclipse
    idea
    id("net.neoforged.moddev")
    id("com.diffplug.spotless")
}

val modId = "megacells"

base.archivesName = modId
version = if (System.getenv("GITHUB_REF_TYPE") == "tag") System.getenv("GITHUB_REF_NAME") else "4.12.0"
group = "gripe.90"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}

sourceSets {
    main {
        java {
            // These integrations depend on add-ons that do not have Minecraft 26.1 releases yet.
            exclude("gripe/_90/megacells/integration/appex/**")
            exclude("gripe/_90/megacells/integration/appliede/**")
            exclude("gripe/_90/megacells/integration/appsoul/**")
            exclude("gripe/_90/megacells/integration/arseng/**")
        }
        resources.srcDir(file("src/generated/resources"))
    }

    // Add-ons that are actually available for 26.1 get their real API on the compile classpath here,
    // and their runtime jar added below purely so `runs` can load them for local testing; the shipped
    // mod jar itself never bundles or requires them (see Addons#isLoaded). The other six don't have
    // Minecraft 26.1 releases yet, so their integration code only ever compiles against last-known
    // 1.21.1 jars (never on the runtime classpath) until each ships its own 26.1 port.
    val addons = create("addons") {
        val main = main.get()
        compileClasspath += main.compileClasspath + main.output
        runtimeClasspath += main.runtimeClasspath + main.output
    }
}

dependencies {
    api(core.ae2)

    compileOnly(integration.ae2wtlibapi)
    "addonsRuntimeOnly"(integration.ae2wtlib)

    compileOnly(integration.appmek)
    compileOnly(integration.mekanism)

    compileOnly(integration.appbot)
    compileOnly(integration.botania)

    testImplementation(testlibs.junit.jupiter)
    testImplementation(testlibs.assertj)
    testImplementation(testlibs.neoforge.test)
    testRuntimeOnly(testlibs.junit.platform)
}

neoForge {
    version = core.versions.neoforge.get()

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
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
        // Keep recipes for unavailable third-party integrations out of the release JAR.
        exclude(
            "data/megacells/recipe/**/*experience*",
            "data/megacells/recipe/**/*source*",
            "data/megacells/recipe/**/*soul*",
            "data/megacells/recipe/**/*emc*",
        )

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
        // Default palantir-java-format bundled with spotless 7.0.1 reflects into javac internals that
        // changed shape in JDK 25 (DeferredDiagnosticHandler::getDiagnostics now returns a List, not a
        // Queue), so spotlessJavaCheck crashes with a NoSuchMethodError on every file. 2.71.0+ fixes this.
        palantirJavaFormat("2.96.0")
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
