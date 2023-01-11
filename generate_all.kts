@file:JvmName("MyScript")

import java.io.File
import java.util.SortedMap

private val RESOURCES_START = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>"
private val RESOURCES_END = "\n</resources>"

val lightColorsMap = File("output/light/color/base.json").getColors()
val darkColorsMap = File("output/dark/color/base.json").getColors()

val attrsTextBuilder = StringBuilder(RESOURCES_START)
val lightColorsTextBuilder = StringBuilder(RESOURCES_START)
val darkColorsTextBuilder = StringBuilder(RESOURCES_START)
val lightThemeTextBuilder = StringBuilder(RESOURCES_START)
    .append(
        "\n    <style name=\"Theme.Superbet.CoreUi.Light\" parent=\"Theme.MaterialComponents.NoActionBar\">"
    )
val darkThemeTextBuilder = StringBuilder(RESOURCES_START)
    .append(
        "\n    <style name=\"Theme.Superbet.CoreUi.Dark\" parent=\"Theme.MaterialComponents.NoActionBar\">"
    )

val attrsSet = HashSet<String>()
// Append all light colors to colors and theme string builder.
lightColorsMap.forEach { (key, value) ->
    attrsSet.add(key)
    lightColorsTextBuilder.append("\n    <color name=\"${key}Light\">${value}</color>")
    lightThemeTextBuilder.append("\n        <item name=\"$key\">@color/${key}Light</item>")
}

// Append all dark colors to colors and theme string builder.
darkColorsMap.forEach { (key, value) ->
    attrsSet.add(key)
    darkColorsTextBuilder.append("\n    <color name=\"${key}Dark\">${value}</color>")
    darkThemeTextBuilder.append("\n        <item name=\"$key\">@color/${key}Dark</item>")
}

// Append all attrs to attrs string builder.
attrsSet.forEach {
    attrsTextBuilder.append("\n    <attr name=\"$it\" format=\"color\" />")
}

// Append end tags to all string builders
attrsTextBuilder.append(RESOURCES_END)
lightColorsTextBuilder.append(RESOURCES_END)
darkColorsTextBuilder.append(RESOURCES_END)
lightThemeTextBuilder.append("\n    </style>").append(RESOURCES_END)
darkThemeTextBuilder.append("\n    </style>").append(RESOURCES_END)

// Write all strings builders to files.
val resPath = "src/main/res/values"
File(resPath).mkdirs()
File("${resPath}/colors_attrs.xml").writeText(attrsTextBuilder.toString())
File("${resPath}/colors_light.xml").writeText(lightColorsTextBuilder.toString())
File("${resPath}/colors_dark.xml").writeText(darkColorsTextBuilder.toString())
File("${resPath}/theme_light.xml").writeText(lightThemeTextBuilder.toString())
File("${resPath}/theme_dark.xml").writeText(darkThemeTextBuilder.toString())

/**
 * Parses receiver json file and return all the colors as key value map.
 */
fun File.getColors(): SortedMap<String, String> {
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
}
