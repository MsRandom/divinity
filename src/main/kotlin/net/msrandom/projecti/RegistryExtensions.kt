package net.msrandom.projecti

import net.neoforged.neoforge.registries.DeferredHolder
import kotlin.reflect.KProperty

inline operator fun <R : Any, T : R> DeferredHolder<R, T>.getValue(
    thisRef: Any?,
    property: KProperty<*>
): T = this.get()
