package net.msrandom.divinity.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.msrandom.divinity.world.item.crafting.DivinityRecipeTypes
import net.msrandom.divinity.world.level.block.entity.BlowMoldBlockEntity
import net.msrandom.divinity.world.level.block.entity.DivinityBlockEntities
import net.msrandom.divinity.world.level.block.entity.LiquidInletBlockEntity
import net.msrandom.divinity.world.level.melting.MeltingData
import net.msrandom.divinity.world.level.melting.calculateMeltTime
import net.msrandom.divinity.world.level.melting.getMoltenForm
import net.neoforged.neoforge.common.NeoForgeMod
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil
import kotlin.streams.asSequence

class BlowMoldBlock(properties: Properties) : BaseEntityBlock(properties) {
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
        SHAPE

    override fun getInteractionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape =
        INSIDE

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

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)

        tryAttachIntake(level, pos, null, null)
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

        val blockEntity = DivinityBlockEntities.blowMold.getBlockEntity(level, pos) ?: return
        val fluid = blockEntity.tank.fluid.fluidType

        if (fluid === NeoForgeMod.LAVA_TYPE.value()) {
            entity.lavaHurt()
        }

        if (entity !is ItemEntity) {
            return
        }

        val moltenForm = getMoltenForm(entity) ?: return

        val meltTime = if (moltenForm.fluidType.temperature >= fluid.temperature) {
            // Immediate liquid is not hot enough to melt item, check hottest intake
            val intakeFluid = blockEntity.hottestIntakeFluid ?: return

            if (moltenForm.fluidType.temperature >= intakeFluid.temperature) {
                return
            }

            calculateMeltTime(moltenForm, intakeFluid, tickFactor = MeltingData.INDIRECT_MELTING_TICK_FACTOR)
        } else {
            calculateMeltTime(moltenForm, fluid)
        }


        if (entity.age <= meltTime) {
            return
        }

        entity.discard()
        blockEntity.tank.fluid = FluidStack(moltenForm, FluidType.BUCKET_VOLUME)
        level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0)
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

        val neighborEntity = DivinityBlockEntities.liquidInlet.getBlockEntity(level, neighborPos)

        tryAttachIntake(level, pos, neighborPos, neighborEntity)
    }

    private fun tryAttachIntake(level: Level, pos: BlockPos, neighborPos: BlockPos?, neighborEntity: LiquidInletBlockEntity?) {
        val blockEntity = DivinityBlockEntities.blowMold.getBlockEntity(level, pos) ?: return

        val neighbors = Direction.Plane.HORIZONTAL
            .asSequence()
            .mapNotNull { direction ->
                val pos = pos.relative(direction)

                val entity = if (pos == neighborPos) {
                    neighborEntity
                } else {
                    DivinityBlockEntities.liquidInlet.getBlockEntity(level, pos)
                }

                entity?.let { direction to it }
            }

        val bestIntakeDirection = neighbors.maxByOrNull { (_, entity) -> entity.tank.fluid.fluidType.temperature }?.let { (direction, _) -> direction }

        blockEntity.attachIntake(bestIntakeDirection)
    }

    override fun getRenderShape(state: BlockState) = RenderShape.ENTITYBLOCK_ANIMATED
    override fun codec(): MapCodec<BlowMoldBlock> = simpleCodec(::BlowMoldBlock)
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BlowMoldBlockEntity(pos, state)

    companion object {
        private val INSIDE = box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0)

        private val SHAPE = Shapes.join(
            Shapes.block(),
            Shapes.or(
                box(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                box(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                INSIDE,
            ),
            BooleanOp.ONLY_FIRST,
        );
    }
}
