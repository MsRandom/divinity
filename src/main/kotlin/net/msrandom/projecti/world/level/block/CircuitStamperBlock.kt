package net.msrandom.projecti.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.msrandom.projecti.world.level.block.entity.CircuitStamperBlockEntity

class CircuitStamperBlock(properties: Properties) : BaseEntityBlock(properties) {
    override fun getRenderShape(state: BlockState) = RenderShape.ENTITYBLOCK_ANIMATED

    override fun codec(): MapCodec<CircuitStamperBlock> = simpleCodec(::CircuitStamperBlock)
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = CircuitStamperBlockEntity(pos, state)
}
