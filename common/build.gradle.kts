architectury {
    val platforms: List<String> by rootProject.extra
    println("Platforms: $platforms")
    common(platforms)
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    modCompileOnly("appeng:appliedenergistics2-fabric:${property("ae2Version")}")
    modCompileOnly("curse.maven:ae2wtlib-459929:${project(":fabric").dependencyProject.property("ae2wtFile")}")
    modCompileOnly("curse.maven:applied-botanics-addon-610632:${project(":fabric").dependencyProject.property("appbotFile")}")
}
