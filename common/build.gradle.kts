architectury {
    val platforms: List<String> by rootProject.extra
    println("Platforms: $platforms")
    common(platforms)
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    val fabricProject = project(":fabric").dependencyProject

    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${fabricProject.property("fabricApiVersion")}+${property("minecraftVersion")}")
    modCompileOnly("appeng:appliedenergistics2-fabric:${property("ae2Version")}")
    modCompileOnly("curse.maven:ae2wtlib-459929:${fabricProject.property("ae2wtFile")}")
    modCompileOnly("curse.maven:applied-botanics-addon-610632:${fabricProject.property("appbotFile")}")
}
