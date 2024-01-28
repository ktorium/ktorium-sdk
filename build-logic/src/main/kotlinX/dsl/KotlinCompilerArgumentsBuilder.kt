@file:Suppress("PackageDirectoryMismatch")

package org.ktorium.kotlin.gradle.dsl

class KotlinCompilerArgumentsBuilder {
    private val arguments: MutableList<String> = mutableListOf()

    fun add(arg: String) = arguments.add(arg)
    fun requiresOptIn() = arguments.add("-opt-in=kotlin.RequiresOptIn")
    fun requiresJsr305(value: String = "strict") = arguments.add("-Xjsr305=$value")
    fun suppressExpectActualClasses() = arguments.add("-Xexpect-actual-classes")
    fun suppressVersionWarnings() = arguments.add("-Xsuppress-version-warnings")

    fun build(): List<String> = arguments
}
