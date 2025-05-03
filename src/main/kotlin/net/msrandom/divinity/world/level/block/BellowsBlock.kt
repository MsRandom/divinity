package net.msrandom.divinity.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.msrandom.divinity.world.level.block.entity.BellowsBlockEntity
import net.msrandom.divinity.world.level.block.entity.DivinityBlockEntities

class BellowsBlock(properties: Properties) : HorizontalDirectionalBlock(properties), EntityBlock {
    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? =
        defaultBlockState().setValue(FACING, context.horizontalDirection)

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult,
    ): InteractionResult {
        val entity = DivinityBlockEntities.bellows.getBlockEntity(level, pos) ?: return super.useWithoutItem(
            state,
            level,
            pos,
            player,
            hitResult,
        )

        val lastUsageTick = entity.lastUsageTick
        val gameTime = level.gameTime
        val delay = gameTime - lastUsageTick

        entity.lastUsageTick = gameTime

        if (delay > USAGE_TICK_MAX_INTERVAL) {
            entity.removeProgress(delay.toInt() - USAGE_TICK_MAX_INTERVAL)

            return InteractionResult.SUCCESS_NO_ITEM_USED
        } else {
            // Continuous tick, add progress
            entity.addProgress(USAGE_TICK_MAX_INTERVAL - delay.toInt())

            return InteractionResult.FAIL
        }
    }

    override fun getRenderShape(state: BlockState) = RenderShape.ENTITYBLOCK_ANIMATED
    override fun codec(): MapCodec<BellowsBlock> = simpleCodec(::BellowsBlock)
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BellowsBlockEntity(pos, state)

    override fun triggerEvent(state: BlockState, level: Level, pos: BlockPos, id: Int, param: Int): Boolean {
        super.triggerEvent(state, level, pos, id, param)

        return level.getBlockEntity(pos)?.triggerEvent(id, param) == true
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    private companion object {
        private const val USAGE_TICK_MAX_INTERVAL = 5
    }
}
