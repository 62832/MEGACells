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
    "dataCompileOnly"(variantOf(integration.mekanism) { classifier("generators") })

    compileOnly(integration.appbot)
    compileOnly(integration.botania)

    testImplementation(testlibs.junit.jupiter)
    testImplementation(testlibs.assertj)
    testImplementation(testlibs.neoforge.test)
    testRuntimeOnly(testlibs.junit.platform)
}

val generatedResourcesClient = layout.buildDirectory.dir("generatedResources/client")
val generatedResourcesServer = layout.buildDirectory.dir("generatedResources/server")

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

        create("clientData") {
            clientData()
            gameDirectory = file("run/data")
            logLevel = org.slf4j.event.Level.INFO
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", generatedResourcesClient.get().asFile.absolutePath,
                "--existing", main,
                "--existing", "$main/optional_cell_colours",
            )
            sourceSet = sourceSets.getByName("data")
        }

        create("serverData") {
            serverData()
            gameDirectory = file("run/data")
            logLevel = org.slf4j.event.Level.INFO
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", generatedResourcesServer.get().asFile.absolutePath,
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
    // clientData and serverData run as separate JVM processes, each with its own HashCache that
    // only knows about the providers it registered. Pointing both directly at the same --output
    // (the old approach) meant each run's cleanup pass deleted whatever the other run's providers
    // had written, since neither cache manifest recognised the sibling's files as "still wanted".
    // Routing each run into its own scratch directory and merging with a Sync task afterwards keeps
    // that cleanup logic scoped to files it actually knows about.
    register<Sync>("syncGeneratedResources") {
        group = "megacells"
        description = "Merges clientData/serverData datagen output into src/generated/resources."
        from(generatedResourcesClient) { exclude(".cache/**") }
        from(generatedResourcesServer) { exclude(".cache/**") }
        into("src/generated/resources")
        dependsOn("runClientData", "runServerData")

        // Content that no provider run in this repo can currently regenerate, but that's still
        // correct and still ships in the jar, so it must survive a sync even though it's absent
        // from both `from()` sources:
        preserve {
            // Static ae2:composite/ae2:status_indicator part models (see MEGAEMCInterfacePart.java)
            // aren't produced by any datagen provider at all.
            include("assets/megacells/ae2/**")

            // OverrideModelProvider only generates optional_cell_colours overrides for AE2's own
            // cells. The AppMek/ArsEng/AppEx/Applied Soul portions come from the same add-ons
            // excluded/unloadable elsewhere in this file, so they can't regenerate either.
            include("optional_cell_colours/assets/appmek/**")
            include("optional_cell_colours/assets/arseng/**")
            include("optional_cell_colours/assets/appex/**")
            include("optional_cell_colours/assets/appliedsoul/**")

            // Cell Dock and Decompression Module use hand-authored Blockbench models (see
            // src/main/resources/assets/megacells/models/item), so MEGAModelProvider doesn't
            // generate anything for them; their item definitions are static too.
            include("assets/megacells/items/cell_dock.json")
            include("assets/megacells/items/decompression_module.json")

            // AppMek's Radioactive Chemical Cell recipe is only written when Addons#isLoaded is
            // true, which requires Mekanism/AppMek's real jar on the runtime classpath - never the
            // case here, since they're compile-only stubs (see build.gradle.kts dependencies).
            include("data/megacells/recipe/cells/standard/radioactive_chemical_cell.json")
            include("data/megacells/recipe/crafting/radioactive_cell_component.json")
            include("data/megacells/advancement/recipes/misc/cells/standard/radioactive_chemical_cell.json")
            include("data/megacells/advancement/recipes/misc/crafting/radioactive_cell_component.json")

            // ArsEng's and Applied Soul's housing recipes come from ArsEngIntegrationData/
            // AppSoulIntegrationData, which are excluded from compilation entirely (see the `data`
            // source set above), so nothing can regenerate these until either addon ships a 26.1 build.
            include("data/megacells/recipe/cells/mega_source_cell_housing.json")
            include("data/megacells/recipe/mega_soul_cell_housing.json")
        }
    }

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
