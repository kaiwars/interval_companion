package com.example.intervalcompanion.data.model

import java.util.UUID

data class Round(
    val id: String = UUID.randomUUID().toString(),
    val checked: Boolean = true,
    val interval1: Int? = null,
    val interval2: Int? = null,
    val interval3: Int? = null
) {
    /** Returns list of (durationSeconds, intervalIndex 0-2) for all non-null positive intervals. */
    fun activeIntervals(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        interval1?.let { if (it > 0) result.add(it to 0) }
        interval2?.let { if (it > 0) result.add(it to 1) }
        interval3?.let { if (it > 0) result.add(it to 2) }
        return result
    }

    fun hasAnyInterval(): Boolean = activeIntervals().isNotEmpty()
}
