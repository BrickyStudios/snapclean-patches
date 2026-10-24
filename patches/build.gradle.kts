group = "app.snapclean"

patches {
    about {
        name = "SnapClean Patches"
        description = "Ad-free, nag-free Snapchat with a free custom location. For use with Morphe."
        source = "git@github.com:BrickyStudios/snapclean-patches.git"
        author = "BrickyStudios"
        contact = "https://github.com/BrickyStudios/snapclean-patches/issues"
        website = "https://github.com/BrickyStudios/snapclean-patches"
        license = "GPLv3"
    }
}

// Separate configuration so gson is available at runtime for the
// generatePatchesList task but never bundled into the APK.
val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesList")
    }
}
