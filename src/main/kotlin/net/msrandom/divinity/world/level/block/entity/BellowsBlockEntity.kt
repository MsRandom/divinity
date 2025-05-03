package net.msrandom.divinity.world.level.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.max

class BellowsBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(DivinityBlockEntities.bellows, pos, state) {
    var lastUsageTick = 0L
    var totalProgress = 0

    fun addProgress(progress: Int) {
        totalProgress += progress

        if (totalProgress >= MAX_TICK_PROGRESS) {
            val pos = blockPos.relative(blockState.getValue(HorizontalDirectionalBlock.FACING))

            activate(pos)
        }
    }

    fun removeProgress(progress: Int) {
        totalProgress = max(totalProgress - progress, 0)
    }

    private fun activate(pos: BlockPos) {
        DivinityBlockEntities.blowMold.getBlockEntity(level, pos)?.craft()
    }

    private companion object {
        private const val MAX_TICK_PROGRESS = 25
    }
}
