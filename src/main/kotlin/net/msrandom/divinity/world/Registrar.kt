package net.msrandom.divinity.world

import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.msrandom.divinity.Divinity
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import kotlin.reflect.KProperty0

sealed interface BaseRegistrar<T, R : DeferredRegister<T>> {
    val register: R

    fun <U : T> holder(property: KProperty0<U>): DeferredHolder<T, U> {
        @Suppress("UNCHECKED_CAST")
        return property.getDelegate() as DeferredHolder<T, U>
    }
}

open class ItemRegistrar : BaseRegistrar<Item, DeferredRegister.Items> {
    final override val register: DeferredRegister.Items = DeferredRegister.createItems(Divinity.MOD_ID)
}

open class BlockRegistrar : BaseRegistrar<Block, DeferredRegister.Blocks> {
    final override val register: DeferredRegister.Blocks = DeferredRegister.createBlocks(Divinity.MOD_ID)
}

open class DataComponentsRegistrar(
    registry: ResourceKey<Registry<DataComponentType<*>>> = Registries.DATA_COMPONENT_TYPE,
) : BaseRegistrar<DataComponentType<*>, DeferredRegister.DataComponents> {
    final override val register: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(registry, Divinity.MOD_ID)
}

open class Registrar<T>(registry: ResourceKey<out Registry<T>>) : BaseRegistrar<T, DeferredRegister<T>> {
    final override val register: DeferredRegister<T> = DeferredRegister.create(registry, Divinity.MOD_ID)
}
