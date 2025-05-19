package net.msrandom.divinity

import net.msrandom.divinity.world.BaseRegistrar
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.item.DivinityMoldItems
import net.msrandom.divinity.world.item.crafting.DivinityRecipeSerializers
import net.msrandom.divinity.world.item.crafting.DivinityRecipeTypes
import net.msrandom.divinity.world.level.block.DivinityBlocks
import net.msrandom.divinity.world.level.block.DivinityGlasswareBlocks
import net.msrandom.divinity.world.level.block.entity.DivinityBlockEntities
import net.msrandom.divinity.world.level.fluid.DivinityFluids
import net.msrandom.divinity.world.level.melting.MeltEventHandler
import net.msrandom.divinity.world.level.melting.MeltingData
import net.msrandom.divinity.world.level.soul.SoulEventHandler
import net.msrandom.divinity.world.level.soul.SoulType
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent

@Mod(Divinity.MOD_ID)
class Divinity(modBus: IEventBus) {
    init {
        val extraRegisters = listOf(
            DivinityFluids.fluidTypeRegister,
            DivinityItems.tabRegister,
        )

        val registers = arrayOf(
            DivinityBlocks,
            DivinityItems,
            DivinityMoldItems,
            DivinityGlasswareBlocks,
            DivinityBlockEntities,
            DivinityFluids,
            DivinityRecipeTypes,
            DivinityRecipeSerializers,
        ).map(BaseRegistrar<*, *>::register) + extraRegisters

        for (register in registers) {
            register.register(modBus)
        }

        modBus.addListener(::registerDataMapTypes)
        modBus.addListener(DivinityFluids::registerFluidInteractions)

        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, SoulEventHandler::dropSoulBone)
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, MeltEventHandler::checkEntityInvulnerability)
    }

    private fun registerDataMapTypes(event: RegisterDataMapTypesEvent) {
        event.register(SoulType.DATA_MAP)
        event.register(MeltingData.DATA_MAP)
    }

    companion object {
        const val MOD_ID = "divinity"
    }
}
