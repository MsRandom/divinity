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
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
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
) : BaseEntityBlock(properties) {
    // TODO Analog signal?
    // TODO Implement updateShape?

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
        )
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

    private fun connections(level: LevelReader, pos: BlockPos) =
        Direction.Plane.HORIZONTAL.asSequence().filter {
            level.getBlockState(pos.relative(it)).block === DivinityBlocks.blowMold
        }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos) = connections(level, pos).any()

    private fun getUpdatedState(level: LevelReader, pos: BlockPos): BlockState? {
        var state: BlockState? = null

        fun toggleDirection(property: BooleanProperty) {
            val base = state ?: defaultBlockState()

            state = base.setValue(property, true)
        }

        for (direction in connections(level, pos)) {
            when (direction) {
                Direction.NORTH -> toggleDirection(NORTH)
                Direction.SOUTH -> toggleDirection(SOUTH)
                Direction.WEST -> toggleDirection(WEST)
                Direction.EAST -> toggleDirection(EAST)
                else -> {}
            }
        }

        return state
    }

    override fun getStateForPlacement(context: BlockPlaceContext) =
        getUpdatedState(context.level, context.clickedPos)

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult,
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

            level.updateNeighbourForOutputSignal(pos, this)

            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        }

        return passthrough()
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

        fun handleDirection(direction: Direction, property: BooleanProperty): Boolean {
            if (neighborPos == pos.relative(direction)) {
                val isBlowMold = level.getBlockState(neighborPos).block === DivinityBlocks.blowMold
                val isToggled = state.getValue(property)

                if (isBlowMold != isToggled) {
                    val keepPlaced = if (isBlowMold) {
                        true
                    } else {
                        listOf(NORTH, SOUTH, WEST, EAST)
                            .filterNot(property::equals)
                            .map(state::getValue)
                            .any { it }
                    }

                    if (keepPlaced) {
                        level.setBlockAndUpdate(pos, state.setValue(property, isBlowMold))
                    } else {
                        level.removeBlock(pos, false)

                        dropResources(state, level, pos)
                    }
                }

                return true
            }

            return false
        }

        if (handleDirection(Direction.NORTH, NORTH)) {
            return
        }

        if (handleDirection(Direction.SOUTH, SOUTH)) {
            return
        }

        if (handleDirection(Direction.WEST, WEST)) {
            return
        }

        handleDirection(Direction.EAST, EAST)
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

        level.setBlockAndUpdate(pos, fluidBlockState)

        level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0)
    }

    override fun codec() = CODEC

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState,
    ) = LiquidInletBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(NORTH, SOUTH, WEST, EAST)
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

        private val NORTH = BlockStateProperties.NORTH
        private val SOUTH = BlockStateProperties.SOUTH
        private val WEST = BlockStateProperties.WEST
        private val EAST = BlockStateProperties.EAST
    }
}
