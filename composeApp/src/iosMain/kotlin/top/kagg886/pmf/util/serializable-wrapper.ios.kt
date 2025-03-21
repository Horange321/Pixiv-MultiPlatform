package top.kagg886.pmf.util

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

actual class SerializableWrapper<T : Any> {
    private var value: T? = null
    actual operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value!!
    }
    actual companion object {
        actual fun <T : Any> makeItSerializable(value: T, clazz: KClass<T>): SerializableWrapper<T> {
            return SerializableWrapper<T>().apply {
                this.value = value
            }
        }
    }
}