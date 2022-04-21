package com.example.upa_app.data.session.json

import com.example.upa_app.model.Speaker
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Deserializer for [Speaker]s.
 */
class SpeakerDeserializer : JsonDeserializer<Speaker> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Speaker {
        val obj = json?.asJsonObject!!
        val social = obj.getAsJsonObject("socialLinks")
        return Speaker(
            id = obj.get("id").asString,
            name = obj.get("name").asString,
            imageUrl = obj.get("thumbnailUrl")?.asString ?: "",
            company = obj.get("company")?.asString ?: "",
            biography = obj.get("bio")?.asString ?: "",
            websiteUrl = social?.get("Website")?.asString,
            twitterUrl = social?.get("Twitter")?.asString,
            githubUrl = social?.get("GitHub")?.asString,
            linkedInUrl = social?.get("LinkedIn")?.asString
        )
    }
}