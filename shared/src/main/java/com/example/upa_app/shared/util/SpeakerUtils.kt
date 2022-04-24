package com.example.upa_app.shared.util

import com.example.upa_app.model.Speaker

object SpeakerUtils {

    fun alphabeticallyOrderedSpeakerList(speakerSet: Set<Speaker>) =
        ArrayList<Speaker>(speakerSet).sortedBy { it.name }
}
