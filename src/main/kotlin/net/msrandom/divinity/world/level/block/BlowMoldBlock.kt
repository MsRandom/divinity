package net.msrandom.divinity.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.msrandom.divinity.world.item.crafting.DivinityRecipeTypes
import net.msrandom.divinity.world.level.block.entity.BlowMoldBlockEntity
import net.msrandom.divinity.world.level.block.entity.DivinityBlockEntities
import net.neoforged.neoforge.fluids.FluidUtil

class BlowMoldBlock(properties: Properties) : BaseEntityBlock(properties) {
    override fun hasAnalogOutputSignal(state: BlockState) = true

    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos): Int {
        val entity = DivinityBlockEntities.blowMold.getBlockEntity(level, pos) ?: return 0

        if (!entity.getItem(BlowMoldBlockEntity.OUTPUT_SLOT).isEmpty) {
            return 2
        }

        if (!entity.tank.isEmpty) {
            return 1
        }

        return 0
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult,
    ): ItemInteractionResult {
        val entity = DivinityBlockEntities.blowMold.getBlockEntity(level, pos) ?: return super.useItemOn(
            stack,
            state,
            level,
            pos,
            player,
            hand,
            hitResult
        )

        // Try to transfer fluids first and foremost
        if (FluidUtil.interactWithFluidHandler(player, hand, entity.tank)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        }

        if (level.isClientSide) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        }

        if (entity.getItem(BlowMoldBlockEntity.MOLD_SLOT).isEmpty) {
            // Check if the item is a mold
            val isValidMold = level.recipeManager.getAllRecipesFor(DivinityRecipeTypes.blowMold).any {
                it.value.ingredients[0].test(stack)
            }

            if (isValidMold) {
                entity.setItem(BlowMoldBlockEntity.MOLD_SLOT, stack.copyWithCount(1))
                stack.shrink(1)

                return ItemInteractionResult.sidedSuccess(level.isClientSide)
            }
        }

        val output = entity.getItem(BlowMoldBlockEntity.OUTPUT_SLOT)

        if (!output.isEmpty) {
            entity.setItem(BlowMoldBlockEntity.OUTPUT_SLOT, ItemStack.EMPTY)

            if (stack.isEmpty) {
                player.setItemInHand(hand, output)
            } else if (!player.inventory.add(output)) {
                player.addItem(output)
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult,
    ): InteractionResult {
        val entity = DivinityBlockEntities.blowMold.getBlockEntity(level, pos) ?: return super.useWithoutItem(
            state,
            level,
            pos,
            player,
            hitResult
        )

        if (!entity.getItem(BlowMoldBlockEntity.MOLD_SLOT).isEmpty) {
            val mold = entity.removeItem(BlowMoldBlockEntity.MOLD_SLOT, 1)

            player.setItemInHand(InteractionHand.MAIN_HAND, mold)
        }

        return super.useWithoutItem(state, level, pos, player, hitResult)
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        super.entityInside(state, level, pos, entity)


    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
    }

    override fun getRenderShape(state: BlockState) = RenderShape.ENTITYBLOCK_ANIMATED
    override fun codec(): MapCodec<BlowMoldBlock> = simpleCodec(::BlowMoldBlock)
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BlowMoldBlockEntity(pos, state)
}
