@file:Suppress("PackageDirectoryMismatch")

package org.ktorium.kotlin.gradle.plugins.publication

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import java.util.*

public class PublicationPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = with(project) {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")

        val signing: SigningExtension = project.extensions.getByType()
        signing.run {
            val signingKey = findProperty("GPG_PRIVATE_KEY")?.toString()
            val signingPassword = findProperty("GPG_PASSPHRASE")?.toString()

            if (signingKey != null && signingPassword != null) {
                useInMemoryPgpKeys(String(Base64.getDecoder().decode(signingKey.toByteArray())), signingPassword)
            }
        }

        val publishing: PublishingExtension = project.extensions.getByType()
        publishing.repositories {
            maven {
                name = "LocalRepository"
                url = uri(layout.buildDirectory.dir("local-repository"))
            }
        }

        publishing.publications {
            withType<MavenPublication>().configureEach {
                configurePom()

                signing.sign(this)
            }
        }

        val cleanMavenLocal = tasks.creating {
            group = "build"
            doLast {
                val home = System.getProperty("user.home")
                val artifactPath = project.group.toString().replace(".", "/")
                val groupRepo = file("$home/.m2/repository/$artifactPath")

                publishing.publications.filterIsInstance<MavenPublication>().forEach {
                    groupRepo.resolve(it.artifactId).deleteRecursively()
                }
            }
        }
        tasks.named("clean") {
            dependsOn(cleanMavenLocal)
        }
    }
}

private fun MavenPublication.configurePom(): Unit = pom {
    name.set(this.name)
    description.set(this.description)

    val gitRepository = "https://github.com/ktorium/ktorium-kotlin"
    url.set(gitRepository)

    licenses {
        license {
            name.set("Apache-2.0 License")
            url.set("$gitRepository/blob/main/LICENSE")
        }
    }

    issueManagement {
        system.set("GitHub")
        url.set("$gitRepository/issues")
    }

    scm {
        connection.set("scm:git:$gitRepository.git")
        developerConnection.set("scm:git:$gitRepository.git")
        url.set(gitRepository)
    }
}
