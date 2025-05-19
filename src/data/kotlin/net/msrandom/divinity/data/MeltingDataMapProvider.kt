package net.msrandom.divinity.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.level.block.DivinityBlocks
import net.msrandom.divinity.world.level.fluid.DivinityFluids
import net.msrandom.divinity.world.level.melting.MeltingData
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.DataMapProvider
import java.util.concurrent.CompletableFuture

class MeltingDataMapProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
) : DataMapProvider(packOutput, lookupProvider) {
    override fun getName() = "Melting Data Map"

    override fun gather(provider: HolderLookup.Provider) {
        val meltingData = builder(MeltingData.DATA_MAP)

        meltingData.add(DivinityBlocks.holder(DivinityBlocks::blueCrystal).id, DivinityFluids.moltenBlueCrystal, false)
        meltingData.add(DivinityItems.holder(DivinityItems::yellowCrystal), DivinityFluids.moltenYellowCrystal, false)

        // Every glass block melts into the same fluid, the dye is burnt off
        meltingData
            .add(Tags.Items.GLASS_BLOCKS, DivinityFluids.moltenGlass, false)
            .add(Tags.Items.GLASS_PANES, DivinityFluids.moltenGlass, false)
    }
}
