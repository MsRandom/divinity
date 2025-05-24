package net.msrandom.divinity.world.level.block

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.msrandom.divinity.world.level.block.entity.DivinityBlockEntities
import net.msrandom.divinity.world.level.block.entity.LiquidInletBlockEntity
import net.msrandom.divinity.world.level.melting.calculateMeltTime
import net.msrandom.divinity.world.level.melting.getMoltenForm
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil

class LiquidInletBlock(
    val material: Holder<Item>,
    val cooled: Boolean,
    properties: Properties,
) : HorizontalDirectionalBlock(properties), EntityBlock {
    // TODO Analog signal?

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    private fun maybeScheduleMelt(level: LevelAccessor, pos: BlockPos, entity: LiquidInletBlockEntity) {
        val moltenForm = getMoltenForm(material) ?: return
        val meltTime = calculateMeltTime(moltenForm, entity.tank.fluid.fluidType)

        if (entity.tank.fluid.fluidType.temperature <= moltenForm.fluidType.temperature) {
            return
        }

        // Item can melt, schedule melt tick
        level.scheduleTick(pos, this, meltTime)
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        fun passthrough() = super.useItemOn(
            stack,
            state,
            level,
            pos,
            player,
            hand,
            hitResult
        )

        val entity = DivinityBlockEntities.liquidInlet.getBlockEntity(level, pos) ?: return passthrough()

        if (FluidUtil.interactWithFluidHandler(player, hand, entity.tank)) {
            if (!cooled && !entity.tank.isEmpty) {
                maybeScheduleMelt(level, pos, entity)
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        }

        return passthrough()
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        super.tick(state, level, pos, random)

        val entity = DivinityBlockEntities.liquidInlet.getBlockEntity(level, pos) ?: return

        val moltenForm = getMoltenForm(material) ?: return

        val fluidType = moltenForm.fluidType

        if (entity.tank.fluid.fluidType.temperature <= fluidType.temperature) {
            return
        }

        val stack = FluidStack(moltenForm, FluidType.BUCKET_VOLUME)

        val fluidState = fluidType.getStateForPlacement(level, pos, stack)
        val fluidBlockState = fluidType.getBlockForFluidState(level, pos, fluidState)

        level.setBlock(pos, fluidBlockState, UPDATE_ALL)

        level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0)
    }

    override fun codec() = CODEC

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState,
    ) = LiquidInletBlockEntity(pos, state)

    override fun triggerEvent(state: BlockState, level: Level, pos: BlockPos, id: Int, param: Int): Boolean {
        super.triggerEvent(state, level, pos, id, param)

        return level.getBlockEntity(pos)?.triggerEvent(id, param) == true
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    companion object {
        @JvmField
        val CODEC: MapCodec<LiquidInletBlock> = RecordCodecBuilder.mapCodec {
            it.group(
                BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("material").forGetter(LiquidInletBlock::material),
                Codec.BOOL.optionalFieldOf("cooled", false).forGetter(LiquidInletBlock::cooled),
                propertiesCodec(),
            ).apply(it, ::LiquidInletBlock)
        }
    }
}
