package net.msrandom.divinity.world

import net.neoforged.neoforge.registries.DeferredRegister

interface Registrar<T : DeferredRegister<*>> {
    val register: T
}
