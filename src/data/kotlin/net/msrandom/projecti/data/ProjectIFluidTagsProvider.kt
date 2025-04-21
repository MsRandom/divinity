package net.msrandom.projecti.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.FluidTagsProvider
import net.minecraft.tags.FluidTags
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.world.level.fluid.ProjectIFluids
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ProjectIFluidTagsProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper,
) : FluidTagsProvider(packOutput, lookupProvider, ProjectI.MOD_ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {
        val lavaTag = tag(FluidTags.LAVA)

        for (holder in ProjectIFluids.register.entries) {
            lavaTag.add(holder.get())
        }
    }
}
