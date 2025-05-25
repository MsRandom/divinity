package net.msrandom.divinity.data.client

import net.minecraft.data.PackOutput
import net.minecraft.data.models.model.TextureMapping
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.world.level.block.DivinityBlocks
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class DivinityBlockStateProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper,
) : BlockStateProvider(output, Divinity.MOD_ID, existingFileHelper) {
    override fun registerStatesAndModels() {
        paneBlock(
            DivinityBlocks.crystallizedSoulbonePane,
            blockTexture(DivinityBlocks.crystallizedSoulbonePane),
            TextureMapping.getBlockTexture(DivinityBlocks.crystallizedSoulbonePane, "_top"),
        )
    }
}
