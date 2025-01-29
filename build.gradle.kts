plugins {
    id("net.neoforged.moddev")
    id("com.diffplug.spotless")
}

val modId = "megacells"

base.archivesName = modId
version = if (System.getenv("GITHUB_REF_TYPE") == "tag") System.getenv("GITHUB_REF_NAME") else "0.0.0"
group = "gripe.90"

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

dependencies {
    implementation(libs.ae2)

    compileOnly(libs.ae2wtlibapi)
    runtimeOnly(libs.ae2wtlib)

    implementation(libs.appmek)
    compileOnly(libs.mekanism)
    compileOnly(variantOf(libs.mekanism) { classifier("generators") })
    runtimeOnly(variantOf(libs.mekanism) { classifier("all") })

    implementation(libs.arseng)
    implementation(libs.arsnouveau)

    implementation(libs.appflux)
    runtimeOnly(libs.glodium)

    implementation(libs.appex)
    runtimeOnly(libs.explib)

    compileOnly(libs.appbot)
    compileOnly(libs.botania)

    testImplementation(testlibs.junit.jupiter)
    testImplementation(testlibs.assertj)
    testImplementation(testlibs.neoforge.test)
    testRuntimeOnly(testlibs.junit.platform)
}

sourceSets {
    main {
        resources.srcDir(file("src/generated/resources"))
    }

    create("data") {
        val main = main.get()
        compileClasspath += main.compileClasspath + main.output
        runtimeClasspath += main.runtimeClasspath + main.output
    }
}

neoForge {
    version = libs.versions.neoforge.get()

    parchment {
        // minecraftVersion = libs.versions.minecraft.get()
        minecraftVersion = "1.21"
        mappingsVersion = libs.versions.parchment.get()
    }
    
    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("data"))
        }
    }

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.DEBUG
            gameDirectory = file("run")
        }

        create("client") {
            client()
        }

        create("server") {
            server()
            gameDirectory = file("run/server")
        }

        create("data") {
            data()
            logLevel = org.slf4j.event.Level.INFO
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath,
                "--existing", file("src/main/resources/optional_cell_colours").absolutePath,
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
