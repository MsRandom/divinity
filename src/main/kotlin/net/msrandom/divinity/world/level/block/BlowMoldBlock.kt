package net.msrandom.divinity.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.msrandom.divinity.world.level.block.entity.BellowsBlockEntity
import net.msrandom.divinity.world.level.block.entity.BlowMoldBlockEntity

class BlowMoldBlock(properties: Properties) : BaseEntityBlock(properties) {
    override fun getRenderShape(state: BlockState) = RenderShape.ENTITYBLOCK_ANIMATED
    override fun codec(): MapCodec<BlowMoldBlock> = simpleCodec(::BlowMoldBlock)
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BlowMoldBlockEntity(pos, state)
}
