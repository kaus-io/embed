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
    macosArm64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.okio)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "embed-runtime", version.toString())
    pom {
        name = "embed-runtime"
        description = "Runtime library for Embed - embed static resources into Kotlin Multiplatform binaries as Base64 chunks, accessible via Resources / Resource / ResourceDirectory APIs."
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