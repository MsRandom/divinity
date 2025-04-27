package net.msrandom.divinity.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.msrandom.divinity.world.level.block.entity.CircuitStamperBlockEntity
import net.msrandom.divinity.world.level.block.entity.DivinityBlockEntities

class CircuitStamperBlock(properties: Properties) : HorizontalDirectionalBlock(properties), EntityBlock {
    init {
        registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(FACING, Direction.NORTH))
    }

    override fun getRenderShape(state: BlockState) = RenderShape.ENTITYBLOCK_ANIMATED

    override fun hasAnalogOutputSignal(state: BlockState) = true

    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos): Int {
        val entity = DivinityBlockEntities.circuitStamper.getBlockEntity(level, pos) ?: return 0

        if (!entity.getItem(CircuitStamperBlockEntity.OUTPUT_SLOT).isEmpty) {
            return 15
        }

        return entity.items.count { !it.isEmpty } * 3
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val pos = context.clickedPos
        val level = context.level

        return if (pos.y < level.maxBuildHeight - 1) {
            defaultBlockState()
                .setValue(FACING, context.horizontalDirection)
                .setValue(POWERED, level.hasNeighborSignal(pos))
        } else {
            null
        }
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (state.getValue(POWERED)) {
            DivinityBlockEntities.circuitStamper.getBlockEntity(level, pos)?.stamp()
        }
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean,
    ) {
        val powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos)

        if (!defaultBlockState().`is`(neighborBlock) && powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), UPDATE_CLIENTS)

            if (powered) {
                DivinityBlockEntities.circuitStamper.getBlockEntity(level, pos)?.stamp()
            }
        }
    }

    override fun triggerEvent(state: BlockState, level: Level, pos: BlockPos, id: Int, param: Int): Boolean {
        super.triggerEvent(state, level, pos, id, param)

        return level.getBlockEntity(pos)?.triggerEvent(id, param) == true
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState =
        state.setValue(FACING, rot.rotate(state.getValue(FACING)))

    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        state.setValue(FACING, mirror.mirror(state.getValue(FACING)))

    override fun codec(): MapCodec<CircuitStamperBlock> = simpleCodec(::CircuitStamperBlock)
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = CircuitStamperBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(POWERED, FACING)
    }

    companion object {
        @JvmField
        val POWERED: BooleanProperty = BlockStateProperties.POWERED
    }
}
