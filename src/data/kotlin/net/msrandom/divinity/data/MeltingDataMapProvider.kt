package net.msrandom.divinity.data

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluid
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.level.block.DivinityBlocks
import net.msrandom.divinity.world.level.fluid.DivinityFluids
import net.msrandom.divinity.world.level.melting.MeltingData
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

        addMeltable(DivinityBlocks.blueCrystal.asItem(), DivinityFluids.moltenBlueCrystal)
        addMeltable(DivinityItems.yellowCrystal, DivinityFluids.moltenYellowCrystal)
        addMeltable(Items.GLASS, DivinityFluids.moltenGlass)
    }
}
