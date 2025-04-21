package net.msrandom.projecti.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.msrandom.projecti.ProjectI
import net.msrandom.projecti.tags.ProjectIBlockTags
import net.msrandom.projecti.world.level.block.ProjectIBlocks
import net.neoforged.neoforge.common.Tags
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

        tag(ProjectIBlockTags.QUARTZ)
            .add(Blocks.QUARTZ_BLOCK)
            .add(Blocks.QUARTZ_SLAB)
            .add(Blocks.QUARTZ_BRICKS)
            .add(Blocks.QUARTZ_STAIRS)
            .add(Blocks.SMOOTH_QUARTZ)
            .add(Blocks.CHISELED_QUARTZ_BLOCK)
            .add(Blocks.NETHER_QUARTZ_ORE)
            .add(Blocks.SMOOTH_QUARTZ_SLAB)
            .add(Blocks.SMOOTH_QUARTZ_STAIRS)

        tag(ProjectIBlockTags.COAL)
            .add(Blocks.COAL_BLOCK)
            .add(Blocks.COAL_ORE)
            .add(Blocks.DEEPSLATE_COAL_ORE)

        tag(ProjectIBlockTags.ROCK)
            .addTag(BlockTags.BASE_STONE_NETHER)
            .addTag(BlockTags.BASE_STONE_OVERWORLD)
            .addTag(Tags.Blocks.STONES)
            .addTag(Tags.Blocks.COBBLESTONES)
            .addTag(Tags.Blocks.OBSIDIANS)
            .addTag(ProjectIBlockTags.QUARTZ)
            .addTag(ProjectIBlockTags.COAL)
            .add(Blocks.BEDROCK)

        tag(ProjectIBlockTags.CRYSTAL_GROWING_MATERIAL).addTag(ProjectIBlockTags.ROCK)
    }
}
