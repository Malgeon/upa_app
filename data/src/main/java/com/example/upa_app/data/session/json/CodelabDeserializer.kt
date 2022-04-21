package com.example.upa_app.data.session.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Deserializer for Codelabs. Returns temporary Codelab objects, which are later normalized once
 * tags have also been parsed.
 */
class CodelabDeserializer : JsonDeserializer<CodelabTemp> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CodelabTemp {
        val obj = json?.asJsonObject!!
        return CodelabTemp(
            id = obj.get("id").asString,
            title = obj.get("title").asString,
            description = obj.get("description").asString,
            durationMinutes = obj.get("duration").asInt,
            iconUrl = obj.get("icon")?.asString,
            codelabUrl = obj.get("link").asString,
            sortPriority = obj.get("priority")?.asInt ?: 0,
            tagNames = getListFromJsonArray(obj, "tagNames")
        )
    }
}
