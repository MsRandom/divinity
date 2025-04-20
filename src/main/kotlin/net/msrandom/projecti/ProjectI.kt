package net.msrandom.projecti

import net.msrandom.projecti.world.Registrar
import net.msrandom.projecti.world.item.ProjectIItems
import net.msrandom.projecti.world.item.crafting.ProjectIRecipeSerializers
import net.msrandom.projecti.world.item.crafting.ProjectIRecipeTypes
import net.msrandom.projecti.world.level.block.ProjectIBlocks
import net.msrandom.projecti.world.level.block.entity.ProjectIBlockEntities
import net.msrandom.projecti.world.level.fluid.ProjectIFluids
import net.msrandom.projecti.world.level.melting.MeltEventHandler
import net.msrandom.projecti.world.level.melting.MeltingData
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent

@Mod(ProjectI.MOD_ID)
class ProjectI(modBus: IEventBus) {
    init {
        val extraRegisters = listOf(
            ProjectIFluids.fluidTypeRegister,
            ProjectIItems.tabRegister,
        )

        val registers = arrayOf(
            ProjectIBlocks,
            ProjectIItems,
            ProjectIBlockEntities,
            ProjectIFluids,
            ProjectIRecipeTypes,
            ProjectIRecipeSerializers,
        ).map(Registrar<*>::register) + extraRegisters

        for (register in registers) {
            register.register(modBus)
        }

        modBus.addListener(::registerDataMapTypes)
        NeoForge.EVENT_BUS.register(MeltEventHandler)
    }

    private fun registerDataMapTypes(event: RegisterDataMapTypesEvent) {
        event.register(MeltingData.DATA_MAP)
    }

    companion object {
        const val MOD_ID = "project_i"
    }
}
