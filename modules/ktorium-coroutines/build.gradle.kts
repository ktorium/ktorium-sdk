import org.ktorium.kotlin.gradle.dsl.withCompilerArguments
import org.ktorium.kotlin.gradle.plugin.api

plugins {
    kotlin("multiplatform")

    id("build.base")
    id("build.publication")
}

configurations.all {
    resolutionStrategy {
        failOnNonReproducibleResolution()
    }
}

kotlin {
    explicitApi()

    sourceSets {
        all {
            languageSettings.apply {
                apiVersion = ktoriumBuild.kotlinApiVersion.get().version
                languageVersion = ktoriumBuild.kotlinLanguageVersion.get().version
                progressiveMode = true

                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            kotlin {
                srcDirs( "src/commonMain/kotlinX")
            }
            dependencies {
                api(project.dependencies.platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.7.3"))
                api("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
            }
        }

        val commonTest by getting {
            kotlin {
                srcDirs("src/commonTest/kotlinX")
            }
            dependencies {
                api("org.jetbrains.kotlinx", "kotlinx-coroutines-test")
                implementation(kotlin("test"))
            }
        }
    }

    jvm {
        compilations.all {
            compilerOptions.configure {
                withCompilerArguments {
                    requiresOptIn()
                    requiresJsr305()
                }
            }
        }
    }

    withSourcesJar()
}