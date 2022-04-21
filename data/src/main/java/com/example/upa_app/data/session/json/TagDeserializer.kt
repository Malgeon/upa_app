package com.example.upa_app.data.session.json

import com.example.upa_app.model.Tag
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Deserializer for [Tag]s.
 */
class TagDeserializer : JsonDeserializer<Tag> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Tag {
        val obj = json?.asJsonObject!!
        return Tag(
            id = obj.get("id").asString,
            category = obj.get("category").asString,
            tagName = obj.get("tag").asString,
            orderInCategory = obj.get("order_in_category")?.asInt ?: 999,
            color = parseColor(obj.get("color")?.asString),
            fontColor = parseColor(obj.get("fontColor")?.asString),
            displayName = obj.get("name").asString
        )
    }
}
