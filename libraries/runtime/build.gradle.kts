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
        description = "Runtime library for Embed - embed static resources into Kotlin binaries"
        inceptionYear = "2026"
        url = "https://github.com/zxhhyj/embed"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
                distribution = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "zxhhyj"
                name = "zxhhyj"
                url = "https://github.com/zxhhyj"
            }
        }
        scm {
            url = "https://github.com/zxhhyj/embed"
            connection = "scm:git:git://github.com/zxhhyj/embed.git"
            developerConnection = "scm:git:ssh://github.com/zxhhyj/embed.git"
        }
    }
}