package net.msrandom.divinity

import net.neoforged.neoforge.registries.DeferredHolder
import kotlin.reflect.KProperty

operator fun <R : Any, T : R> DeferredHolder<R, T>.getValue(
    thisRef: Any?,
    property: KProperty<*>
): T = this.get()
