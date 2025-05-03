package net.msrandom.divinity.world.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.msrandom.divinity.world.level.block.BlueCrystalBlock

class BlueCrystalItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val existingState = context.level.getBlockState(context.clickedPos)

        if (existingState.block == block) {
            if (context.player?.abilities?.instabuild != true || !BlueCrystalBlock.canGrow(existingState)) {
                val result = super.useOn(context)

                if (result == InteractionResult.FAIL) {
                    // Let block handle it
                    return InteractionResult.PASS
                }
            }

            // TODO Send sound event
            BlueCrystalBlock.grow(existingState, context.level, context.clickedPos)

            return InteractionResult.sidedSuccess(context.level.isClientSide)
        }

        return super.useOn(context)
    }
}
