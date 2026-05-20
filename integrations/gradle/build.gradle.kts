plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
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