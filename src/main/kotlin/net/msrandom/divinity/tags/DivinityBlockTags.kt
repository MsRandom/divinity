package net.msrandom.divinity.tags

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.msrandom.divinity.Divinity

object DivinityBlockTags {
    val ROCK: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Divinity.MOD_ID, "rock"))
    val QUARTZ: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Divinity.MOD_ID, "quartz"))
    val COAL: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Divinity.MOD_ID, "coal"))
    val CRYSTAL_GROWING_MATERIAL: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Divinity.MOD_ID, "crystal_growing_material"))
}
