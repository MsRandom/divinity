package net.msrandom.projecti.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.msrandom.projecti.world.level.block.entity.BlueCrystalBlockEntity

class BlueCrystalBlock(properties: Properties) : FaceAttachedHorizontalDirectionalBlock(properties), EntityBlock {
    init {
        registerDefaultState(defaultBlockState().setValue(AGE, 0))
    }

    override fun getRenderShape(state: BlockState) = RenderShape.INVISIBLE

    override fun triggerEvent(state: BlockState, level: Level, pos: BlockPos, id: Int, param: Int): Boolean {
        super.triggerEvent(state, level, pos, id, param)

        return level.getBlockEntity(pos)?.triggerEvent(id, param) == true
    }

    override fun codec(): MapCodec<BlueCrystalBlock> = simpleCodec(::BlueCrystalBlock)

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BlueCrystalBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    companion object {
        private val AGE: IntegerProperty = BlockStateProperties.AGE_5
    }
}
