@file:JvmName("MyScript")

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File

val input = File("output/tokens/color/base.json")
val gsonTree = Gson().fromJson(input.inputStream().reader(), JsonElement::class.java)

val colorsOutputTextBuilder = StringBuilder()
colorsOutputTextBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>")
val attrsOutputTextBuilder = StringBuilder()
attrsOutputTextBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>")
val themeOutputTextBuilder = StringBuilder()
themeOutputTextBuilder.append(
    "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n" +
            "    <style name=\"Theme.Superbet.CoreUi.Light\" parent=\"Theme.MaterialComponents.NoActionBar\">"
)

val baseColors: JsonObject =
    gsonTree.asJsonObject.get("color").asJsonObject.get("base").asJsonObject

baseColors.asMap().filterKeys {
    !it.startsWith("base")
}.mapValues {
    it.value.asJsonObject.get("value").asJsonPrimitive.toString()
}.toSortedMap().forEach { (key, value) ->
    colorsOutputTextBuilder.append("\n    <color name=\"${key}Light\">$value</color>")
    attrsOutputTextBuilder.append("\n    <attr name=\"$key\" format=\"color\" />")
    themeOutputTextBuilder.append("\n        <item name=\"$key\">@color/${key}Light</item>")
}

colorsOutputTextBuilder.append("\n</resources>")
attrsOutputTextBuilder.append("\n</resources>")
themeOutputTextBuilder.append("\n    </style>\n</resources>")
File("attrs.xml").writeText(attrsOutputTextBuilder.toString())
File("colors_light.xml").writeText(colorsOutputTextBuilder.toString())
File("theme_light.xml").writeText(themeOutputTextBuilder.toString())
