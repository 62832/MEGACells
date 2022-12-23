architectury {
    common(property("enabled_platforms").toString().split(','))
}

loom {
    accessWidenerPath.set(file("src/main/resources/megacells.accesswidener"))
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    modCompileOnly("appeng:appliedenergistics2-fabric:${property("ae2_version")}")
    modCompileOnly("curse.maven:ae2wtlib-459929:${project(":fabric").dependencyProject.property("ae2wt_fileid")}")
    modCompileOnly("curse.maven:applied-botanics-addon-610632:${project(":fabric").dependencyProject.property("appbot_fileid")}")
}

publishing {
    publications {
        create<MavenPublication>("mavenCommon") {
            artifactId = property("mod_id").toString()
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
    }
}
