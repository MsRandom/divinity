package net.msrandom.divinity.world.level.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.msrandom.divinity.world.level.block.entity.BlueCrystalBlockEntity
import net.neoforged.neoforge.common.Tags

class BlueCrystalBlock(properties: Properties) : FaceAttachedHorizontalDirectionalBlock(properties), EntityBlock {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(AGE, 0)
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.FLOOR),
        )
    }

    override fun isRandomlyTicking(state: BlockState) = state.getValue(AGE) < MAX_AGE

    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (!level.isAreaLoaded(pos, 1)) {
            return
        }

        // It is assumed that this block is not maximum age as otherwise it would not have randomly ticked
        val age = state.getValue(AGE)
        val speed = getGrowthSpeed(state, level, pos)

        if (random.nextInt((25.0F / speed).toInt() + 1) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2)
        }
    }

    override fun getRenderShape(state: BlockState) = RenderShape.INVISIBLE

    override fun triggerEvent(state: BlockState, level: Level, pos: BlockPos, id: Int, param: Int): Boolean {
        super.triggerEvent(state, level, pos, id, param)

        return level.getBlockEntity(pos)?.triggerEvent(id, param) == true
    }

    override fun codec(): MapCodec<BlueCrystalBlock> = simpleCodec(::BlueCrystalBlock)
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BlueCrystalBlockEntity(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(AGE, FACE, FACING)
    }

    companion object {
        private val AGE: IntegerProperty = BlockStateProperties.AGE_5
        private const val MAX_AGE = 5

        // Mostly vanilla crop logic modified to use the right face directions & be less lenient
        private fun getGrowthSpeed(state: BlockState, level: LevelReader, pos: BlockPos): Float {
            val block = state.block
            var speed = 1.0F
            val surfaceDirection = getConnectedDirection(state).opposite
            val surfacePosition = pos.relative(surfaceDirection)

            for (x in -1..1) {
                for (z in -1..1) {
                    var extraSpeed = 0.0F

                    val siblingPos = surfacePosition.offset(state, x, z)
                    val siblingState = level.getBlockState(siblingPos)

                    if (canAttach(level, siblingPos, surfaceDirection)) {
                        extraSpeed = 1.0F

                        if (siblingState.`is`(Tags.Blocks.ORES_IN_GROUND_STONE)) {
                            extraSpeed = 3.0F
                        }
                    }

                    if (x != 0 || z != 0) {
                        extraSpeed /= 4.0F
                    }

                    speed += extraSpeed
                }
            }

            val north = pos.offset(state, Direction.NORTH)
            val south = pos.offset(state, Direction.SOUTH)
            val west = pos.offset(state, Direction.WEST)
            val east = pos.offset(state, Direction.EAST)

            val horizontalRow = level.getBlockState(west).`is`(block) || level.getBlockState(east).`is`(block)
            val verticalRow = level.getBlockState(north).`is`(block) || level.getBlockState(south).`is`(block)

            if (horizontalRow && verticalRow) {
                speed /= 2.0F
            }

            return speed
        }

        private fun BlockPos.offset(state: BlockState, direction: Direction) =
            offset(state, direction.stepX, direction.stepZ)

        private fun BlockPos.offset(state: BlockState, x: Int, z: Int) = when (state.getValue(FACE)) {
            AttachFace.FLOOR -> offset(x, 0, z)
            AttachFace.WALL -> {
                when (state.getValue(FACING)) {
                    Direction.NORTH -> offset(x, z, 0)
                    Direction.SOUTH -> offset(-x, z, 0)
                    Direction.WEST -> offset(0, z, -x)
                    Direction.EAST -> offset(0, z, x)

                    else -> throw IllegalStateException("Invalid axis ${Direction.Axis.Y} in horizontal directional value")
                }
            }

            AttachFace.CEILING -> offset(-x, 0, -z)
        }
    }
}
