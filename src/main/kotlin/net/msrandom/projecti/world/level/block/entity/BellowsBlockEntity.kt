package net.msrandom.projecti.world.level.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class BellowsBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ProjectIBlockEntities.bellows, pos, state)
