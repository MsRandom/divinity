package net.msrandom.projecti.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.world.level.block.ProjectIBlocks
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ProjectIBlockTagsProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper,
) : BlockTagsProvider(packOutput, lookupProvider, ProjectI.MOD_ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {
        tag(BlockTags.DRAGON_IMMUNE).add(ProjectIBlocks.circuitStamper)
        tag(BlockTags.WITHER_IMMUNE).add(ProjectIBlocks.circuitStamper)
    }
}
