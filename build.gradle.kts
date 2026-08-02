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
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}

sourceSets {
    main {
        java {
            // AppEx, AppSoul and ArsEng's own storage cell classes (ExperienceStorageCell,
            // SoulCellItem, SourceCellItem) directly override Item#use(), whose signature changed too
            // much between MC versions (old InteractionResultHolder<ItemStack> return type doesn't
            // exist in 26.1 at all) for javac to even verify the class enough to construct it, let
            // alone override anything on it - unlike the others, this isn't fixable by just avoiding
            // the specific broken member. Stay excluded until each ships a real 26.1 build.
            exclude("gripe/_90/megacells/integration/appex/**")
            exclude("gripe/_90/megacells/integration/appsoul/**")
            exclude("gripe/_90/megacells/integration/arseng/**")

            // AppliedE's own MEGAEMCInterfaceBlockEntity/MEGAEMCInterfacePart directly `extends`
            // AppliedE's own classes (not just reference them), so the JVM eagerly resolves that
            // superclass the moment MEGA's own classes are loaded - crashing the WHOLE mod's
            // construction at runtime, not just this integration, regardless of Addons#isLoaded
            // gating. Unlike the others, this isn't just a compile-time stub problem, so it stays
            // fully excluded (not just disabled) until AppliedE ships a real 26.1 build.
            exclude("gripe/_90/megacells/integration/appliede/**")
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

    create("data") {
        java {
            // Same four add-ons excluded from `main` (see above) don't have datagen counterparts
            // to compile against either.
            exclude("gripe/_90/megacells/datagen/integration/AppExIntegrationData.java")
            exclude("gripe/_90/megacells/datagen/integration/AppSoulIntegrationData.java")
            exclude("gripe/_90/megacells/datagen/integration/ArsEngIntegrationData.java")
            exclude("gripe/_90/megacells/datagen/integration/AppliedEIntegrationData.java")
        }
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
    // "addonsRuntimeOnly"(integration.appmek)
    "dataCompileOnly"(variantOf(integration.mekanism) { classifier("generators") })
    // "addonsRuntimeOnly"(variantOf(integration.mekanism) { classifier("all") })

    // compileOnly(integration.arseng)
    // "addonsRuntimeOnly"(integration.arseng)
    // "dataCompileOnly"(integration.arsnouveau) { exclude("mezz.jei") }
    // "addonsRuntimeOnly"(integration.arsnouveau) { exclude("mezz.jei") }

    // compileOnly(integration.appflux)
    // "addonsRuntimeOnly"(integration.appflux)
    // "addonsRuntimeOnly"(integration.glodium)

    // compileOnly(integration.appex)
    // "addonsRuntimeOnly"(integration.appex)
    // "addonsRuntimeOnly"(integration.explib)

    // compileOnly(integration.appliede)
    // "addonsRuntimeOnly"(integration.appliede)
    // "addonsRuntimeOnly"(integration.projecte)

    compileOnly(integration.appbot)
    "addonsCompileOnly"(integration.botania)

    // compileOnly(integration.appsoul)
    // "dataCompileOnly"(integration.titanium)
    // "dataCompileOnly"(integration.industrialforegoing)
    // "addonsRuntimeOnly"(integration.appsoul)
    // "addonsRuntimeOnly"(integration.titanium)
    // "addonsRuntimeOnly"(integration.industrialforegoing)
    // "addonsRuntimeOnly"(integration.ifsouls)
    // "addonsRuntimeOnly"(integration.soulpliedenergistics)

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
            clientData()
            gameDirectory = file("run/data")
            logLevel = org.slf4j.event.Level.INFO
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", main,
                "--existing", "$main/optional_cell_colours",
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
