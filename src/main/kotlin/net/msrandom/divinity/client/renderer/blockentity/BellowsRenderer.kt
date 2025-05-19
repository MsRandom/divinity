package net.msrandom.divinity.client.renderer.blockentity

import com.mojang.blaze3d.vertex.PoseStack
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.msrandom.divinity.world.level.block.BellowsBlock.Companion.USAGE_TICK_MAX_INTERVAL
import net.msrandom.divinity.world.level.block.entity.BellowsBlockEntity
import net.msrandom.divinity.world.level.block.entity.BellowsBlockEntity.Companion.MAX_TICK_PROGRESS
import kotlin.math.max
import kotlin.math.min

class BellowsRenderer(context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<BellowsBlockEntity> {
    override fun render(
        blockEntity: BellowsBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
    ) {
        val part = ModelPart(listOf(CubeListBuilder.create().addBox(0f, 15f, 0f, 16f, 1f, 16f).cubes[0].bake(16, 16)), emptyMap())

        poseStack.pushPose()

        val elapsed = blockEntity.level!!.gameTime - blockEntity.lastActivationTick
        val totalTime = MAX_TICK_PROGRESS.toLong()
        val timeSpent = blockEntity.totalProgress
        val interpolatedTime = min(elapsed, totalTime)
        val assumedProgress = interpolatedTime

        val totalProgress = if (blockEntity.isBeingPushed && elapsed < USAGE_TICK_MAX_INTERVAL) {
            timeSpent + assumedProgress + partialTick
        } else {
            timeSpent - assumedProgress - partialTick
        }

        val animationTime = (totalProgress / totalTime.toFloat()).coerceIn(0f, 1f)

        poseStack.translate(0f, -animationTime, 0f)

        part.render(poseStack, bufferSource.getBuffer(RenderType.entitySolid(ResourceLocation.withDefaultNamespace("missingno"))), packedLight, packedOverlay)
        poseStack.popPose()
    }

    companion object {
        private val animationTimes = Object2FloatOpenHashMap<BlockPos>()
    }
}
