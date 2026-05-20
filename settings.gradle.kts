pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "embed"

val projectTypeDirs = listOf("libraries", "integrations")

projectTypeDirs.forEach { type ->
    file(type).list()?.forEach { project ->
        include(project)
        project(":$project").projectDir = file("$type/$project")
    }
}