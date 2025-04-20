package net.msrandom.projecti.world

import net.neoforged.neoforge.registries.DeferredRegister

interface Registrar<T> {
    val register: DeferredRegister<T>
}
