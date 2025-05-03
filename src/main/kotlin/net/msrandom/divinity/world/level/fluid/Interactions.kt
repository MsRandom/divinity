package net.msrandom.divinity.world.level.fluid

import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.common.NeoForgeMod
import net.neoforged.neoforge.event.EventHooks
import net.neoforged.neoforge.fluids.FluidInteractionRegistry

fun getMoltenFluidInteraction(solidForm: () -> Block) = FluidInteractionRegistry.InteractionInformation(
    { level, currentPos, relativePos, state ->
        state.isSource &&
            level.getFluidState(relativePos).fluidType == NeoForgeMod.WATER_TYPE.value() &&
            solidForm().defaultBlockState().canSurvive(level, currentPos)
    },
    { level, currentPos, relativePos, state ->
        val block = solidForm()

        val newState = if (BlockStateProperties.HORIZONTAL_FACING in block.stateDefinition.properties) {
            block.defaultBlockState().setValue(
                BlockStateProperties.HORIZONTAL_FACING,
                Direction.from2DDataValue(currentPos.asLong().toInt()),
            )
        } else {
            block.defaultBlockState()
        }

        level.setBlockAndUpdate(currentPos, EventHooks.fireFluidPlaceBlockEvent(level, currentPos, currentPos, newState));
        level.levelEvent(LevelEvent.LAVA_FIZZ, currentPos, 0);
    },
)
