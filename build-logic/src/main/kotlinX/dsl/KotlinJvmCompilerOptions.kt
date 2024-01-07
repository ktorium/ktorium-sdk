@file:Suppress("PackageDirectoryMismatch")

package org.ktorium.kotlin.gradle.dsl

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

fun KotlinJvmCompilerOptions.withCompilerArguments(configure: KotlinCompilerArgumentsBuilder.() -> Unit) {
    val arguments = KotlinCompilerArgumentsBuilder().apply(configure).build()

    freeCompilerArgs.addAll(arguments)
}