package ru.netology.maps.initialization

import android.content.Context
import com.yandex.mapkit.MapKitFactory

private var initialized = false

object MapKitInitializer {
    fun initialize(apiKey: String, context: Context) {
        if (initialized) {
            return
        }

        MapKitFactory.setApiKey(apiKey)
        MapKitFactory.initialize(context)
        initialized = true
    }
}