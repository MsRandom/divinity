package net.msrandom.projecti.tags

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.msrandom.projecti.ProjectI

object ProjectIBlockTags {
    val ROCK: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProjectI.MOD_ID, "rock"))
    val QUARTZ: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProjectI.MOD_ID, "quartz"))
    val COAL: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProjectI.MOD_ID, "coal"))
    val CRYSTAL_GROWING_MATERIAL: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ProjectI.MOD_ID, "crystal_growing_material"))
}
