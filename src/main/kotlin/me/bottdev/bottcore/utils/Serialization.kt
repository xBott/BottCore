package me.bottdev.bottcore.utils

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Suppress("unused")
inline fun <reified T> toJson(value: T): String {
    return Json.encodeToString(value)
}

@Suppress("unused")
inline fun <reified T> String.fromJson(): T {
    return Json.decodeFromString(this)
}

@Suppress("unused")
inline fun <reified T> toYaml(value: T): String {
    return Yaml.default.encodeToString(value)
}

@Suppress("unused")
inline fun <reified T> String.fromYaml(): T {
    return Yaml.default.decodeFromString(this)
}
