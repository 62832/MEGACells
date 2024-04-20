plugins {
    alias(libs.plugins.neogradle)
    alias(libs.plugins.spotless)
}

val modId = "megacells"

base.archivesName = modId
version = System.getenv("MEGA_VERSION") ?: "0.0.0"
group = "gripe.90"

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "ModMaven (K4U-NL)"
        url = uri("https://modmaven.dev/")
        content {
            includeGroup("appeng")
            includeGroup("mekanism")
        }
    }

    maven {
        name = "Modrinth Maven"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    implementation(libs.neoforge)
    implementation(libs.ae2)

    implementation(libs.ae2wtlib)
    runtimeOnly(libs.curios)

    implementation(libs.appmek)
    compileOnly(libs.mekanism)
    compileOnly(variantOf(libs.mekanism) { classifier("generators") })
    runtimeOnly(variantOf(libs.mekanism) { classifier("all") })
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

runs {
    configureEach {
        workingDirectory(file("run"))
        systemProperty("forge.logging.console.level", "info")

        modSource(sourceSets.main.get())
    }

    create("client")
    create("server")

    create("data") {
        programArguments.addAll(
            "--mod", modId,
            "--all",
            "--output", file("src/generated/resources/").absolutePath,
            "--existing", file("src/main/resources/").absolutePath
        )

        modSource(sourceSets.getByName("data"))
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

        val props = mapOf(
            "version" to version,
            "ae2Version" to libs.versions.ae2.get(),
            "ae2VersionEnd" to libs.versions.ae2.get().substringBefore('.').toInt() + 1,
        )

        inputs.properties(props)
        filesMatching("META-INF/mods.toml") {
            expand(props)
        }
    }
}

spotless {
    kotlinGradle {
        target("*.kts")
        diktat()
    }

    java {
        target("/src/**/java/**/*.java")
        endWithNewline()
        indentWithSpaces(4)
        removeUnusedImports()
        palantirJavaFormat()
        importOrderFile(file("mega.importorder"))

        // courtesy of diffplug/spotless#240
        // https://github.com/diffplug/spotless/issues/240#issuecomment-385206606
        custom("noWildcardImports") {
            if (it.contains("*;\n")) {
                throw Error("No wildcard imports allowed")
            }

            it
        }

        bumpThisNumberIfACustomStepChanges(1)
    }

    json {
        target("src/**/resources/**/*.json")
        targetExclude("src/generated/resources/**")
        biome()
        indentWithSpaces(2)
        endWithNewline()
    }
}
