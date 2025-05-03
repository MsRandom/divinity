package net.msrandom.divinity

import net.minecraft.core.BlockPos

operator fun BlockPos.minus(other: BlockPos): BlockPos = subtract(other)
