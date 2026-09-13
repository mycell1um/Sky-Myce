package me.mycellium.skymyce.hud.widget

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.mycellium.skymyce.SkyMyce
import me.mycellium.skymyce.SkyMyceModule
import java.io.File
import java.lang.ref.WeakReference
import java.nio.file.Path
import kotlin.collections.plusAssign

object WidgetManager : SkyMyceModule() {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val type = object : TypeToken<Map<String, WidgetConfig>>() {}.type
    val saveFile: File = SkyMyce.configPath.resolve("widgets.json").toFile()

    val loadedConfig: Map<String, WidgetConfig> by lazy {
        if (!saveFile.exists()) return@lazy mapOf()

        saveFile.reader().use {
            gson.fromJson(it, type) ?: mapOf()
        }
    }

    private val instances = mutableListOf<WeakReference<Widget>>()

    fun Widget.register() {
        instances += WeakReference(this)
        loadedConfig[id]?.applyConfig(this)
    }

    fun save() {
        val save = widgets.associate { it.id to it.toConfig() }

        saveFile.parentFile.mkdirs()
        saveFile.writer().use { writer ->
            gson.toJson(save, writer)
        }
    }

    val widgets: List<Widget>
        get() {
            instances.removeIf { it.get() == null }
            return instances.mapNotNull { it.get() }
        }

    val renderableWidgets: List<Widget>
        get() = widgets.filter { it.isRenderable }

    fun getActiveWidgets(): List<Widget> {
        val now = System.currentTimeMillis()
        return widgets.filter { (now - it.sinceActive) < 10000 }
    }
}

class WidgetConfig(
    val x: Int = 0,
    val y: Int = 0,
    val scale: Float = 1f,
    val anchor: Anchor = Anchor.TOP_LEFT,
) {
    fun applyConfig(widget: Widget) {
        widget.x = x
        widget.y = y
        widget.scale = scale
        widget.anchor = anchor
    }
}