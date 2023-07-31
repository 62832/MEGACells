architectury {
    val platforms: List<String> by rootProject.extra
    println("Platforms: $platforms")
    common(platforms)
}

dependencies {
    annotationProcessor(variantOf(libs.mixin) { classifier("processor") })

    modCompileOnly(libs.fabric.api)
    modCompileOnly(libs.ae2.fabric)
    modCompileOnly(libs.ae2wtlib.fabric)
    modCompileOnly(libs.appbot.fabric)
}
