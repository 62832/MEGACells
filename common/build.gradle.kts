architectury {
    val platforms: List<String> by rootProject.extra
    println("Platforms: $platforms")
    common(platforms)
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    modCompileOnly("appeng:appliedenergistics2-fabric:${property("ae2_version")}")
    modCompileOnly("curse.maven:ae2wtlib-459929:${project(":fabric").dependencyProject.property("ae2wt_fileid")}")
    modCompileOnly("curse.maven:applied-botanics-addon-610632:${project(":fabric").dependencyProject.property("appbot_fileid")}")
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
            exclude("**/.cache")
        }
    }
}
