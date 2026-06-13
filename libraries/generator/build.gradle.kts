plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    jvmToolchain(25)

    jvm()

    applyDefaultHierarchyTemplate()

    linuxX64()
    linuxArm64()
    mingwX64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":runtime"))
            implementation(libs.okio)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "embed-generator", version.toString())
    pom {
        name = "embed-generator"
        description = "Code generator for Embed - scans resource directories and emits Kotlin source (ResourceDirectory singleton + Base64 chunk lists) to be embedded into Kotlin Multiplatform binaries."
        inceptionYear = "2026"
        url = "https://github.com/kaus-io/embed"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
                distribution = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "WY7XY"
                name = "WY7XY"
                url = "https://github.com/WY7XY"
            }
        }
        scm {
            url = "https://github.com/kaus-io/embed"
            connection = "scm:git:git://github.com/kaus-io/embed.git"
            developerConnection = "scm:git:ssh://github.com/kaus-io/embed.git"
        }
        issueManagement {
            url = "https://github.com/kaus-io/embed/issues"
        }
        ciManagement {
            url = "https://github.com/kaus-io/embed/actions"
        }
    }
}