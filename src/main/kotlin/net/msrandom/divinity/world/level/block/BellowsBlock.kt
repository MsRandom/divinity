package net.msrandom.divinity.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.msrandom.divinity.world.level.block.entity.BellowsBlockEntity
import net.msrandom.divinity.world.level.block.entity.DivinityBlockEntities
import kotlin.math.max

class BellowsBlock(properties: Properties) : HorizontalDirectionalBlock(properties), EntityBlock {
    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? =
        defaultBlockState().setValue(FACING, context.horizontalDirection)

    override fun hasAnalogOutputSignal(state: BlockState) = true

    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos): Int {
        return super.getAnalogOutputSignal(state, level, pos)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean,
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)

        val hasPower = level.hasNeighborSignal(pos)
        val wasPowered = state.getValue(POWERED)

        if (hasPower != wasPowered) {
            // State changed
            level.setBlock(pos, state.setValue(POWERED, hasPower), UPDATE_CLIENTS)

            val entity = DivinityBlockEntities.bellows.getBlockEntity(level, pos) ?: return

            entity.lastActivationTick = level.gameTime
            level.scheduleTick(pos, this, USAGE_TICK_MAX_INTERVAL)
        }
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult,
    ): InteractionResult {
        fun passthrough() = super.useWithoutItem(
            state,
            level,
            pos,
            player,
            hitResult,
        )

        if (state.getValue(POWERED)) {
            return passthrough()
        }

        val entity = DivinityBlockEntities.bellows.getBlockEntity(level, pos) ?: return passthrough()

        val lastUsageTick = entity.lastActivationTick
        val gameTime = level.gameTime
        val delay = gameTime - lastUsageTick

        entity.lastActivationTick = gameTime
        entity.isBeingPushed = true
        entity.setChanged()

        level.scheduleTick(pos, this, USAGE_TICK_MAX_INTERVAL + 1)

        return if (delay > USAGE_TICK_MAX_INTERVAL) {
            entity.addProgress(1)
            InteractionResult.SUCCESS_NO_ITEM_USED
        } else {
            entity.addProgress(delay.toInt())
            InteractionResult.FAIL
        }
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        super.tick(state, level, pos, random)

        val entity = DivinityBlockEntities.bellows.getBlockEntity(level, pos) ?: return

        if (state.getValue(POWERED)) {
            entity.isBeingPushed = true
            entity.lastActivationTick = level.gameTime
            entity.setChanged()

            entity.addProgress(USAGE_TICK_MAX_INTERVAL)

            entity.setChanged()

            level.scheduleTick(pos, this, USAGE_TICK_MAX_INTERVAL)
        } else {
            val lastUsageTick = entity.lastActivationTick
            val gameTime = level.gameTime
            val delay = gameTime - lastUsageTick

            if (entity.isBeingPushed) {
                if (delay > USAGE_TICK_MAX_INTERVAL) {
                    entity.isBeingPushed = false
                    entity.lastActivationTick = gameTime

                    entity.removeProgress(delay.toInt())

                    entity.setChanged()

                    level.scheduleTick(pos, this, USAGE_TICK_MAX_INTERVAL)
                }
            } else {
                entity.lastActivationTick = gameTime

                entity.removeProgress(delay.toInt())

                entity.setChanged()

                if (entity.totalProgress != 0) {
                    level.scheduleTick(pos, this, USAGE_TICK_MAX_INTERVAL)
                }
            }
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
        builder.add(FACING, POWERED)
    }

    companion object {
        internal const val USAGE_TICK_MAX_INTERVAL = 5

        private val POWERED = BlockStateProperties.POWERED
    }
}
