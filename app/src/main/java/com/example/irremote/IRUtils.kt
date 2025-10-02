package com.example.irremote

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Helper utilities for parsing and formatting patterns.
 */
object IRUtils {

    fun parsePatternCsv(csv: String): IntArray {
        val cleaned = csv.trim().replace("[\\[\\]]".toRegex(), "")
        if (cleaned.isBlank()) return intArrayOf()
        return cleaned.split(",").map { it.trim().toInt() }.toIntArray()
    }

    fun parsePatternCsvSafe(csv: String): IntArray? {
        return try {
            parsePatternCsv(csv)
        } catch (e: Exception) {
            null
        }
    }

    fun intArrayToJson(ints: IntArray): String {
        return Json.encodeToString(ints.toList())
    }

    fun parsePatternJson(json: String): IntArray {
        return try {
            val list = Json.decodeFromString(ListSerializer(Int.serializer()), json)
            list.toIntArray()
        } catch (e: Exception) {
            // fallback: try simple parse
            val cleaned = json.trim().replace("[\\[\\]]".toRegex(), "")
            if (cleaned.isBlank()) return intArrayOf()
            return cleaned.split(",").mapNotNull { it.trim().toIntOrNull() }.toIntArray()
        }
    }
}
