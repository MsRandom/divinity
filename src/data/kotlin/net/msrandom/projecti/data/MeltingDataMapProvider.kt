package net.msrandom.projecti.data

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluid
import net.msrandom.projecti.world.item.ProjectIItems
import net.msrandom.projecti.world.level.block.ProjectIBlocks
import net.msrandom.projecti.world.level.fluid.ProjectIFluids
import net.msrandom.projecti.world.level.melting.MeltingData
import net.neoforged.neoforge.common.data.DataMapProvider
import java.util.concurrent.CompletableFuture

class MeltingDataMapProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
) : DataMapProvider(packOutput, lookupProvider) {
    override fun getName() = "Melting Data Map"

    override fun gather(provider: HolderLookup.Provider) {
        val meltingData = builder(MeltingData.DATA_MAP)

        fun addMeltable(item: Item, data: Fluid) {
            meltingData.add(BuiltInRegistries.ITEM.getKey(item), data, false)
        }

        addMeltable(ProjectIBlocks.blueCrystal.asItem(), ProjectIFluids.moltenBlueCrystal)
        addMeltable(ProjectIItems.yellowCrystal, ProjectIFluids.moltenYellowCrystal)
        addMeltable(Items.GLASS, ProjectIFluids.moltenGlass)
    }
}
