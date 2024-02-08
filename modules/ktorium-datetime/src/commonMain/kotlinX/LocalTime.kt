@file:Suppress("PackageDirectoryMismatch")

package org.ktorium.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import org.ktorium.kotlin.ExperimentalKtorium
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

@ExperimentalKtorium
public val LocalTime.Companion.MIN: LocalTime
    get() = LocalTime(0, 0)

@ExperimentalKtorium
public val LocalTime.Companion.MAX: LocalTime
    get() = LocalTime(23, 59, 59, 999_999_999)

@ExperimentalKtorium
public fun LocalTime.Companion.parseOrNull(isoString: String): LocalTime? = runCatching { parse(isoString) }.getOrNull()

/**
 * Returns true if [time] is a valid time string
 */
@ExperimentalKtorium
public fun LocalTime.Companion.isValid(time: String): Boolean = parseOrNull(time) != null

@ExperimentalKtorium
public fun LocalTime.Companion.now(timeZone: TimeZone): LocalTime = LocalDateTime.now(timeZone).time

/**
 * Create a [LocalDateTime] with the specified [time]
 */
@ExperimentalKtorium
public fun LocalDate.atTime(time: LocalTime): LocalDateTime = atTime(time.hour, time.minute, time.second)

/**
 * Create a new [LocalDateTime] using given [date]
 */
@ExperimentalKtorium
public fun LocalTime.atDate(date: LocalDate): LocalDateTime = LocalDateTime(date, this)

/**
 * Returns an integer that represents this time, like a string, but without the ":"
 *
 * Truncates nanoseconds.
 *
 * Examples:
 * - 17:12:45 -> 171245
 * - 00:00:00 -> 0
 * - 05:00:00 -> 50000
 */
@ExperimentalKtorium
public fun LocalTime.toInt(): Int = hour * 10000 + minute * 100 + second

/**
 * Calculate distance to the [other] between [LocalTime.MIN] and [LocalTime.MAX]
 */
@ExperimentalKtorium
public infix fun LocalTime.distanceTo(other: LocalTime): LocalTime = LocalTime.fromSecondOfDay(
    (other.toSecondOfDay() - toSecondOfDay()).absoluteValue
)

/**
 * Create a new [LocalDateTime] with the given [time] and [nanos]
 * Preserves [nanos] of previous time
 */
@ExperimentalKtorium
public fun LocalDateTime.withTime(time: LocalTime, nanos: Int = nanosecond): LocalDateTime =
    LocalDateTime(year, month, dayOfMonth, time.hour, time.minute, time.second, nanos)

/**
 * Returns this object as [kotlin.time.Duration]
 */
@ExperimentalKtorium
public val LocalTime.asDuration: Duration get() = toNanosecondOfDay().nanoseconds

/**
 * @return the amount of seconds from this object's value [to]
 * The amount is always positive
 */
@ExperimentalKtorium
public infix fun LocalTime.distanceInSeconds(to: LocalTime): Int =
    abs(to.hour - hour) * 60 * 60 + abs(to.minute - minute) * 60 + abs(to.second - second)

/** * Get the amount of minutes since midnight.
 */
@ExperimentalKtorium
public val LocalTime.totalMinutes: Double
    get() = hour * 60 + minute + second.toDouble() / 60

/**
 * Get the amount of seconds since midnight. Convenient for storing [LocalTime] as an [Int] value
 */
@ExperimentalKtorium
public val LocalTime.totalSeconds: Int
    get() = hour * 3600 + minute * 60 + second

/**
 * Create a new [LocalTime] from [duration].
 * If the [duration] is larger than 24 hours, it will wrap around
 */
@ExperimentalKtorium
public operator fun LocalTime.Companion.invoke(duration: Duration): LocalTime =
    fromNanosecondOfDay(duration.inWholeNanoseconds)

/**
 * Parse a new time object using the int representation of it.
 * @see LocalTime.toInt
 */
@ExperimentalKtorium
public fun LocalTime.Companion.ofInt(hms: Int): LocalTime = LocalTime(hms / 10000, hms / 100 % 100, hms % 100)

/**
 * Create a time from milliseconds since midnight.
 * **[millis] is NOT a timestamp**
 */
@ExperimentalKtorium
public fun LocalTime.Companion.ofMillis(millis: Int): LocalTime = fromMillisecondOfDay(millis)

/**
 * Get current time value using specified seconds **since midnight**
 * [seconds] is NOT a timestamp
 * @see totalSeconds
 */
@ExperimentalKtorium
public fun LocalTime.Companion.ofSeconds(seconds: Int): LocalTime = fromSecondOfDay(seconds)

/**
 * If [this] is [LocalTime.MIN] return null, [this] otherwise
 */
@ExperimentalKtorium
public fun LocalTime?.takeIfNotZero(): LocalTime? = takeIf { it != LocalTime.MIN }

/** example: 12:45:00, 4:30, 7:00 AM, 24 or 12h format, word separator is " ".
 * On a value that is not a valid time, will throw.
 * **/
@ExperimentalKtorium
public fun LocalTime.Companion.parseAs12H(s: String): LocalTime {
    try {
        require(s.isNotBlank()) { "Empty time string" }

        val words = s.split(" ")
        require(words.size in 1..2) { "Not a time" }

        val parts = words.first().split(':', '.', '-', ' ', ',', '_', ignoreCase = true)
        require(parts.size in 2..3) { "Invalid delimiter count" }

        val hours = parts[0].toInt() + if (words.size == 2 && words[1] == "PM") 12 else 0

        val minutes = parts[1].toInt()

        val seconds = if (parts.size == 3) parts[2].toInt() else 0

        return LocalTime(hours, minutes, seconds)
    } catch (expected: Exception) {
        throw IllegalArgumentException("Couldn't parse time", expected)
    }
}

/**
 * Returns the same time with [LocalTime.nanosecond] set to 0.
 */
@ExperimentalKtorium
public fun LocalTime.truncateNanos(): LocalTime = LocalTime(hour, minute, second, 0)

/**
 * Returns the same time with both nanos and seconds set to 0.
 */
@ExperimentalKtorium
public fun LocalTime.truncateSeconds(): LocalTime = LocalTime(hour, minute, 0, 0)

/**
 * Returns the same time with all of nanos, seconds and minutes set to 0
 */
@ExperimentalKtorium
public fun LocalTime.truncateMinutes(): LocalTime = LocalTime(hour, 0, 0, 0)