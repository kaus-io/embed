import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import org.gradle.api.tasks.bundling.Jar

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    val javadocJar by tasks.registering(Jar::class) {
        description = "Generate javadoc JAR for Maven Central publishing"
        archiveClassifier.set("javadoc")
    }

    val isRelease = !version.toString().endsWith("-SNAPSHOT")

    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                name = "sonatype"
                url = if (isRelease) {
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                } else {
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                }
                credentials {
                    username = providers.gradleProperty("sonatypeUsername").getOrElse("")
                    password = providers.gradleProperty("sonatypePassword").getOrElse("")
                }
            }
        }

        publications.withType(MavenPublication::class.java) {
            artifact(javadocJar)

            pom {
                name.set("embed")
                description.set("Embed static resources directly into your Kotlin binaries")
                url.set("https://github.com/zxhhyj/embed")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("zxhhyj")
                        name.set("zxhhyj")
                    }
                }

                scm {
                    url.set("https://github.com/zxhhyj/embed")
                    connection.set("scm:git:git://github.com/zxhhyj/embed.git")
                    developerConnection.set("scm:git:ssh://github.com/zxhhyj/embed.git")
                }
            }
        }
    }

    extensions.configure<SigningExtension> {
        val signingKey = providers.gradleProperty("signingKey").orNull
        val signingPassword = providers.gradleProperty("signingPassword").orNull
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        sign(extensions.getByType(PublishingExtension::class.java).publications)
    }
}