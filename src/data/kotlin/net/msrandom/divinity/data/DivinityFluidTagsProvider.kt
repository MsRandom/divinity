package net.msrandom.divinity.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.FluidTagsProvider
import net.minecraft.tags.FluidTags
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.world.level.fluid.DivinityFluids
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class DivinityFluidTagsProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper,
) : FluidTagsProvider(packOutput, lookupProvider, Divinity.MOD_ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {
        val lavaTag = tag(FluidTags.LAVA)

        for (holder in DivinityFluids.register.entries) {
            lavaTag.add(holder.get())
        }
    }
}
