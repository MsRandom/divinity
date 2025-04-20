package net.msrandom.projecti.data

import net.minecraft.SharedConstants.TICKS_PER_SECOND
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.msrandom.projecti.world.item.ProjectIItems
import net.msrandom.projecti.world.level.block.ProjectIBlocks
import net.msrandom.projecti.world.level.fluid.ProjectIFluids
import net.msrandom.projecti.world.level.melting.MeltingData
import net.neoforged.neoforge.common.data.DataMapProvider
import java.util.concurrent.CompletableFuture

class MeltableDataMapProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
) : DataMapProvider(packOutput, lookupProvider) {
    override fun gather(provider: HolderLookup.Provider) {
        val meltingData = builder(MeltingData.DATA_MAP)

        fun addMeltable(item: Item, data: MeltingData) {
            meltingData.add(BuiltInRegistries.ITEM.getKey(item), data, false)
        }

        addMeltable(ProjectIBlocks.blueCrystal.asItem(), MeltingData(ProjectIFluids.moltenBlueCrystal))
        addMeltable(ProjectIItems.yellowCrystal, MeltingData(ProjectIFluids.moltenYellowCrystal))
        addMeltable(Items.GLASS, MeltingData(ProjectIFluids.moltenGlass, 5 * 60 * TICKS_PER_SECOND))
    }
}
