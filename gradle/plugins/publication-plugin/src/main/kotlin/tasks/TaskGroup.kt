@file:Suppress("PackageDirectoryMismatch")

package org.ktorium.kotlin.gradle.plugins.publication.tasks

import java.util.*

internal enum class TaskGroup {
    BUILD,
    DOCUMENTATION,
    HELP;

    override fun toString(): String = name.lowercase(Locale.getDefault())
}