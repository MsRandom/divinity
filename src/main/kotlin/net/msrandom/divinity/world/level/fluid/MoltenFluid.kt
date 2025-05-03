package net.msrandom.divinity.world.level.fluid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.util.RandomSource
import net.minecraft.world.level.*
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.LavaFluid
import net.neoforged.neoforge.event.EventHooks
import net.neoforged.neoforge.fluids.BaseFlowingFluid

abstract class MoltenFluid protected constructor(properties: Properties) : BaseFlowingFluid(properties) {
    // Copied from vanilla to do lava-like particles
    public override fun animateTick(level: Level, pos: BlockPos, state: FluidState, random: RandomSource) {
        val above = pos.above()

        if (!level.getBlockState(above).isAir || level.getBlockState(above).isSolidRender(level, above)) {
            return
        }

        if (random.nextInt(100) == 0) {
            val d0 = pos.x.toDouble() + random.nextDouble()
            val d1 = pos.y.toDouble() + 1.0
            val d2 = pos.z.toDouble() + random.nextDouble()

            // TODO New particles
            level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0, 0.0, 0.0)

            // TODO New sound events
            level.playLocalSound(
                d0,
                d1,
                d2,
                SoundEvents.LAVA_POP,
                SoundSource.BLOCKS,
                0.2f + random.nextFloat() * 0.2f,
                0.9f + random.nextFloat() * 0.15f,
                false,
            )
        }

        if (random.nextInt(200) != 0) {
            return
        }

        level.playLocalSound(
            pos.x.toDouble(),
            pos.y.toDouble(),
            pos.z.toDouble(),
            SoundEvents.LAVA_AMBIENT,
            SoundSource.BLOCKS,
            0.2f + random.nextFloat() * 0.2f,
            0.9f + random.nextFloat() * 0.15f,
            false,
        )
    }

    // Copied from vanilla for lava fire spread
    public override fun randomTick(level: Level, pos: BlockPos, state: FluidState, random: RandomSource) {
        if (!level.gameRules.getBoolean(GameRules.RULE_DOFIRETICK)) {
            return
        }

        val fireSpread = random.nextInt(3)

        if (fireSpread > 0) {
            var currentPos = pos

            repeat(fireSpread) {
                currentPos = currentPos.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1)

                if (!level.isLoaded(currentPos)) {
                    return
                }

                val neighborState = level.getBlockState(currentPos)

                if (!neighborState.isAir) {
                    if (neighborState.blocksMotion()) {
                        return
                    }

                    return@repeat
                }

                if (hasFlammableNeighbours(level, currentPos)) {
                    level.setBlockAndUpdate(
                        currentPos,
                        EventHooks.fireFluidPlaceBlockEvent(
                            level,
                            currentPos,
                            pos,
                            BaseFireBlock.getState(level, currentPos),
                        )
                    )

                    return
                }
            }
        } else {
            repeat(3) {
                val randomNeighbor = pos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1)

                if (!level.isLoaded(randomNeighbor)) {
                    return
                }

                if (!level.isEmptyBlock(randomNeighbor.above()) || !this.isFlammable(level, randomNeighbor, Direction.UP)) {
                    return@repeat
                }

                level.setBlockAndUpdate(
                    randomNeighbor.above(),
                    EventHooks.fireFluidPlaceBlockEvent(
                        level,
                        randomNeighbor.above(),
                        pos,
                        BaseFireBlock.getState(level, randomNeighbor),
                    )
                )
            }
        }
    }

    private fun hasFlammableNeighbours(level: LevelReader, pos: BlockPos) = Direction.entries.any {
        isFlammable(level, pos.relative(it), it.opposite)
    }

    private fun isFlammable(level: LevelReader, pos: BlockPos, face: Direction): Boolean {
        if (pos.y >= level.minBuildHeight && pos.y < level.maxBuildHeight && !level.hasChunkAt(pos)) {
            return false
        }

        val state = level.getBlockState(pos)
        return state.ignitedByLava() && state.isFlammable(level, pos, face)
    }

    override fun beforeDestroyingBlock(level: LevelAccessor, pos: BlockPos, state: BlockState) = fizz(level, pos)

    public override fun getSlopeFindDistance(level: LevelReader): Int {
        val slopeFindDistance = super.getSlopeFindDistance(level)

        return if (level.dimensionType().ultraWarm()) {
            slopeFindDistance * 3
        } else {
            slopeFindDistance
        }
    }

    public override fun getDropOff(level: LevelReader): Int {
        val dropOff = super.getDropOff(level)

        return if (level.dimensionType().ultraWarm()) {
            dropOff / 2
        } else {
            dropOff
        }
    }

    public override fun canBeReplacedWith(
        fluidState: FluidState,
        blockReader: BlockGetter,
        pos: BlockPos,
        fluid: Fluid,
        direction: Direction,
    ) = fluidState.getHeight(blockReader, pos) >= LavaFluid.MIN_LEVEL_CUTOFF && fluid.`is`(FluidTags.WATER)

    override fun getTickDelay(level: LevelReader): Int {
        val tickDelay = super.getTickDelay(level)

        return if (level.dimensionType().ultraWarm()) {
            tickDelay / 2
        } else {
            tickDelay
        }
    }

    public override fun getSpreadDelay(
        level: Level,
        pos: BlockPos,
        currentState: FluidState,
        newState: FluidState
    ): Int {
        var tickDelay = this.getTickDelay(level)

        if (
            !currentState.isEmpty &&
            !newState.isEmpty &&
            !currentState.getValue(FALLING) &&
            !newState.getValue(FALLING) &&
            newState.getHeight(level, pos) > currentState.getHeight(level, pos) &&
            level.getRandom().nextInt(4) != 0
        ) {
            tickDelay *= 4
        }

        return tickDelay
    }

    private fun fizz(level: LevelAccessor, pos: BlockPos) = level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0)
    override fun canConvertToSource(level: Level) = level.gameRules.getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION)
    override fun isRandomlyTicking() = true

    class Flowing(properties: Properties) : MoltenFluid(properties) {
        init {
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7))
        }

        override fun createFluidStateDefinition(builder: StateDefinition.Builder<Fluid, FluidState>) {
            super.createFluidStateDefinition(builder)

            builder.add(LEVEL)
        }

        override fun getAmount(state: FluidState): Int = state.getValue(LEVEL)

        override fun isSource(state: FluidState) = false
    }

    class Source(properties: Properties) : MoltenFluid(properties) {
        override fun getAmount(state: FluidState) = 8
        override fun isSource(state: FluidState) = true
    }
}
