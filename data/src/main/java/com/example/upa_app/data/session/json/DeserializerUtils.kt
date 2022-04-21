package com.example.upa_app.data.session.json

import com.google.gson.JsonObject

internal fun getListFromJsonArray(obj: JsonObject, key: String): List<String> {
    val array = obj.get(key).asJsonArray
    return array.map { it.asString }
}
