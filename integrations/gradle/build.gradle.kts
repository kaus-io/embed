plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    jvmToolchain(25)
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":generator"))
    compileOnly(gradleApi())
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.okio)
}

gradlePlugin {
    plugins {
        create("embedPlugin") {
            id = "${project.group}.${rootProject.name}"
            displayName = "Embed Gradle Plugin"
            description = "Runs the code generation process for Embed"
            implementationClass = "com.zxhhyj.embed.gradle.EmbedPlugin"
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "embed-gradle-plugin", version.toString())
    pom {
        name = "embed-gradle-plugin"
        description = "Gradle plugin for Embed - integrates resource generation into the build"
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