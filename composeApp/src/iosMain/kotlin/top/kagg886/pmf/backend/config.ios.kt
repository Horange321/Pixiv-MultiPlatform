package top.kagg886.pmf.backend

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import io.ktor.utils.io.core.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import platform.Foundation.*
import top.kagg886.pmf.util.*

actual fun createConfigPlatform(file: Path): Settings {
    if (file.exists().not()) {
        file.absolutePath().parent?.mkdirs()
        file.createNewFile()
    }
    Logger.d("load config from ${file.absolutePath()}")
    val settings = JsonDefaultSettings(
        delegate0 = Json.decodeFromString<JsonObject>(
            file.source().buffer().readUtf8().ifEmpty { "{}" }
        ),
        onModify = { json ->
            file.delete()
            file.sink().buffer().use {
                it.write(Json.encodeToString(json).toByteArray())
                it.flush()
            }
        }
    )

    return settings
}

private class JsonDefaultSettings(
    delegate0: JsonObject,
    val onModify: (Map<String, JsonElement>) -> Unit = {}
) : Settings {
    private val delegate = delegate0.toMutableMap()

    override val keys: Set<String> = delegate.keys
    override val size: Int = delegate.size

    override fun clear() {
        delegate.clear()
        onModify(delegate)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return delegate.getOrElse(key) { JsonPrimitive(defaultValue) }.jsonPrimitive.boolean
    }

    override fun getBooleanOrNull(key: String): Boolean? {
        return delegate[key]?.jsonPrimitive?.booleanOrNull
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        return delegate.getOrElse(key) { JsonPrimitive(defaultValue) }.jsonPrimitive.double
    }

    override fun getDoubleOrNull(key: String): Double? {
        return delegate[key]?.jsonPrimitive?.doubleOrNull
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return delegate.getOrElse(key) { JsonPrimitive(defaultValue) }.jsonPrimitive.float
    }

    override fun getFloatOrNull(key: String): Float? {
        return delegate[key]?.jsonPrimitive?.floatOrNull
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return delegate.getOrElse(key) { JsonPrimitive(defaultValue) }.jsonPrimitive.int
    }

    override fun getIntOrNull(key: String): Int? {
        return delegate[key]?.jsonPrimitive?.intOrNull
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return delegate.getOrElse(key) { JsonPrimitive(defaultValue) }.jsonPrimitive.long
    }

    override fun getLongOrNull(key: String): Long? {
        return delegate[key]?.jsonPrimitive?.longOrNull
    }

    override fun getString(key: String, defaultValue: String): String {
        return delegate.getOrElse(key) { JsonPrimitive(defaultValue) }.jsonPrimitive.content
    }

    override fun getStringOrNull(key: String): String? {
        return delegate[key]?.jsonPrimitive?.contentOrNull
    }

    override fun hasKey(key: String): Boolean {
        return delegate.containsKey(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        delegate[key] = JsonPrimitive(value)
        onModify(delegate)
    }

    override fun putDouble(key: String, value: Double) {
        delegate[key] = JsonPrimitive(value)
        onModify(delegate)
    }

    override fun putFloat(key: String, value: Float) {
        delegate[key] = JsonPrimitive(value)
        onModify(delegate)
    }

    override fun putInt(key: String, value: Int) {
        delegate[key] = JsonPrimitive(value)
        onModify(delegate)
    }

    override fun putLong(key: String, value: Long) {
        delegate[key] = JsonPrimitive(value)
        onModify(delegate)
    }

    override fun putString(key: String, value: String) {
        delegate[key] = JsonPrimitive(value)
        onModify(delegate)
    }

    override fun remove(key: String) {
        delegate.remove(key)
        onModify(delegate)
    }

}


actual val dataPath: Path by lazy {
    val path = NSSearchPathForDirectoriesInDomains(
        directory = NSDocumentDirectory,
        domainMask = NSUserDomainMask,
        true
    )
    with(Path) {
        path[0]!!.toString().toPath()
    }
}
actual val cachePath: Path by lazy {
    NSLog("cache path: ${NSTemporaryDirectory().toPath()}")
    NSTemporaryDirectory().toPath()
}
