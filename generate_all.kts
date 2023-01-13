@file:JvmName("MyScript")

import java.io.File
import java.util.*
import kotlin.collections.HashSet

private val RESOURCES_START = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>"
private val RESOURCES_END = "\n</resources>"

val lightColorsMap = File("output/light/color/base.json").getColors()
val darkColorsMap = File("output/dark/color/base.json").getColors()

val attrsTextBuilder = StringBuilder(RESOURCES_START)
val lightThemeTextBuilder: StringBuilder = StringBuilder(RESOURCES_START)
    .append(
        "\n    <style name=\"Theme.Superbet.CoreUi.Light\" parent=\"Theme.MaterialComponents.NoActionBar\">"
    )
val darkThemeTextBuilder: StringBuilder = StringBuilder(RESOURCES_START)
    .append(
        "\n    <style name=\"Theme.Superbet.CoreUi.Dark\" parent=\"Theme.MaterialComponents.NoActionBar\">"
    )

val attrsSet = HashSet<Pair<String, String>>()
// Append all light colors to theme string builder.
lightColorsMap.forEach { (key, value) ->
    attrsSet.add(key to "color")
    lightThemeTextBuilder.append("\n        <item name=\"$key\">${value}</item>")
}

// Append all dark colors to colors and theme string builder.
darkColorsMap.forEach { (key, value) ->
    attrsSet.add(key to "color")
    darkThemeTextBuilder.append("\n        <item name=\"$key\">${value}</item>")
}

// Add all multicolor vectors to attrs and themes.
File("output/vectors/").listFiles()?.forEach {
    val nameWithoutExtension = it.nameWithoutExtension
    if (nameWithoutExtension.endsWith(suffix = "_light")) {
        val fileNameWithoutSuffix = nameWithoutExtension.removeSuffix("_light")
        val attrName = fileNameWithoutSuffix.snakeToLowerCamelCase()
        attrsSet.add(attrName to "reference")

        lightThemeTextBuilder.append("\n        <item name=\"$attrName\">@drawable/${fileNameWithoutSuffix}_light</item>")
        darkThemeTextBuilder.append("\n        <item name=\"$attrName\">@drawable/${fileNameWithoutSuffix}_dark</item>")
    }
}

// Append all attrs to attrs string builder.
attrsSet.forEach { (key, format) ->
    attrsTextBuilder.append("\n    <attr name=\"$key\" format=\"$format\" />")
}

// Append end tags to all string builders
attrsTextBuilder.append(RESOURCES_END)
lightThemeTextBuilder.append("\n    </style>").append(RESOURCES_END)
darkThemeTextBuilder.append("\n    </style>").append(RESOURCES_END)

// Write all strings builders to files.
val resPath = "src/main/res/values"
File(resPath).mkdirs()
File("${resPath}/attrs.xml").writeText(attrsTextBuilder.toString())
File("${resPath}/theme_light.xml").writeText(lightThemeTextBuilder.toString())
File("${resPath}/theme_dark.xml").writeText(darkThemeTextBuilder.toString())

/**
 * Parses receiver json file and return all the colors as key value map.
 */
fun File.getColors(): SortedMap<String, String> = runCatching {
    return readText()
        .trimIndent()
        .replace(oldValue = "\n", newValue = "")
        .replace(oldValue = " ", newValue = "")
        .removeSurrounding(prefix = "{\"color\":{\"base\":{", suffix = "}}}")
        .split(",")
        .associate {
            val keyAndValue = it.split(":")
            val key = keyAndValue.first().removeSurrounding("\"")
            val value = keyAndValue.last().removeSurrounding(prefix = "\"", suffix = "\"}")
            key to value
        }.filterKeys {
            !it.startsWith("base")
        }.toSortedMap()
}.getOrNull() ?: sortedMapOf()

/**
 * Converts receiver string from snake case to camel case.
 */
fun String.snakeToLowerCamelCase(): String {
    return split("_").joinToString("") {
        it.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else it
        }
    }.replaceFirstChar {
        it.lowercase()
    }
}
