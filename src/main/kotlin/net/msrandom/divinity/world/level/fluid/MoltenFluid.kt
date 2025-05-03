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
    public override fun animateTick(level: Level, pos: BlockPos, state: FluidState?, random: RandomSource?) {
        val blockpos = pos.above()
        if (level.getBlockState(blockpos).isAir() && !level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
            if (random!!.nextInt(100) == 0) {
                val d0 = pos.getX().toDouble() + random.nextDouble()
                val d1 = pos.getY().toDouble() + 1.0
                val d2 = pos.getZ().toDouble() + random.nextDouble()
                level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0, 0.0, 0.0)
                level.playLocalSound(
                    d0,
                    d1,
                    d2,
                    SoundEvents.LAVA_POP,
                    SoundSource.BLOCKS,
                    0.2f + random.nextFloat() * 0.2f,
                    0.9f + random.nextFloat() * 0.15f,
                    false
                )
            }

            if (random.nextInt(200) == 0) {
                level.playLocalSound(
                    pos.getX().toDouble(),
                    pos.getY().toDouble(),
                    pos.getZ().toDouble(),
                    SoundEvents.LAVA_AMBIENT,
                    SoundSource.BLOCKS,
                    0.2f + random.nextFloat() * 0.2f,
                    0.9f + random.nextFloat() * 0.15f,
                    false
                )
            }
        }
    }

    public override fun randomTick(level: Level, pos: BlockPos, state: FluidState?, random: RandomSource?) {
        if (level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            val i = random!!.nextInt(3)
            if (i > 0) {
                var blockpos = pos

                for (j in 0..<i) {
                    blockpos = blockpos.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1)
                    if (!level.isLoaded(blockpos)) {
                        return
                    }

                    val blockstate = level.getBlockState(blockpos)
                    if (blockstate.isAir()) {
                        if (this.hasFlammableNeighbours(level, blockpos)) {
                            level.setBlockAndUpdate(
                                blockpos,
                                EventHooks.fireFluidPlaceBlockEvent(
                                    level,
                                    blockpos,
                                    pos,
                                    BaseFireBlock.getState(level, blockpos)
                                )
                            )
                            return
                        }
                    } else if (blockstate.blocksMotion()) {
                        return
                    }
                }
            } else {
                for (k in 0..2) {
                    val blockpos1 = pos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1)
                    if (!level.isLoaded(blockpos1)) {
                        return
                    }

                    if (level.isEmptyBlock(blockpos1.above()) && this.isFlammable(level, blockpos1, Direction.UP)) {
                        level.setBlockAndUpdate(
                            blockpos1.above(),
                            EventHooks.fireFluidPlaceBlockEvent(
                                level,
                                blockpos1.above(),
                                pos,
                                BaseFireBlock.getState(level, blockpos1)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun hasFlammableNeighbours(level: LevelReader, pos: BlockPos): Boolean {
        for (direction in Direction.entries) {
            if (this.isFlammable(level, pos.relative(direction), direction.getOpposite())) {
                return true
            }
        }

        return false
    }

    private fun isFlammable(level: LevelReader, pos: BlockPos, face: Direction): Boolean {
        if (pos.getY() >= level.getMinBuildHeight() && pos.getY() < level.getMaxBuildHeight() && !level.hasChunkAt(pos)) {
            return false
        } else {
            val state = level.getBlockState(pos)
            return state.ignitedByLava() && state.isFlammable(level, pos, face)
        }
    }

    override fun beforeDestroyingBlock(level: LevelAccessor?, pos: BlockPos?, state: BlockState?) {
        this.fizz(level!!, pos!!)
    }

    public override fun getSlopeFindDistance(level: LevelReader?): Int {
        val slopeFindDistance = super.getSlopeFindDistance(level)

        return if (level!!.dimensionType().ultraWarm()) slopeFindDistance * 3 else slopeFindDistance
    }

    public override fun getDropOff(level: LevelReader?): Int {
        val dropOff = super.getDropOff(level)

        return if (level!!.dimensionType().ultraWarm()) dropOff / 2 else dropOff
    }

    public override fun canBeReplacedWith(
        fluidState: FluidState,
        blockReader: BlockGetter?,
        pos: BlockPos?,
        fluid: Fluid?,
        direction: Direction?
    ): Boolean {
        return fluidState.getHeight(blockReader, pos) >= LavaFluid.MIN_LEVEL_CUTOFF && fluid!!.`is`(FluidTags.WATER)
    }

    override fun getTickDelay(level: LevelReader?): Int {
        val tickDelay = super.getTickDelay(level)

        return if (level!!.dimensionType().ultraWarm()) tickDelay / 2 else tickDelay
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
