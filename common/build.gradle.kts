architectury {
    val platforms = property("enabledPlatforms").toString().split(',')
    println("Platforms: $platforms")
    common(platforms)
}

dependencies {
    modImplementation(libs.fabric.loader)
    modCompileOnly(libs.fabric.api)
    modCompileOnly(libs.ae2.fabric)
    modCompileOnly(libs.cloth.fabric)
    modCompileOnly(libs.ae2wtlib.fabric)
    modCompileOnly(libs.appbot.fabric)
}
