@file:EventBusSubscriber(value = [Dist.CLIENT], modid = Divinity.MOD_ID, bus = EventBusSubscriber.Bus.MOD)

package net.msrandom.divinity.client

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.client.renderer.blockentity.BellowsRenderer
import net.msrandom.divinity.world.level.block.entity.DivinityBlockEntities
import net.msrandom.divinity.world.level.fluid.DivinityFluids
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent

@SubscribeEvent
fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
    fun registerFluidTextures(fluid: Fluid) {
        val id = BuiltInRegistries.FLUID.getKey(fluid)

        event.registerFluidType(
            object : IClientFluidTypeExtensions {
                override fun getStillTexture() =
                    ResourceLocation.fromNamespaceAndPath(id.namespace, "block/${id.path}_still")

                override fun getFlowingTexture() =
                    ResourceLocation.fromNamespaceAndPath(id.namespace, "block/${id.path}_flow")
            },
            fluid.fluidType,
        )
    }

    registerFluidTextures(DivinityFluids.moltenBlueCrystal)
    registerFluidTextures(DivinityFluids.moltenYellowCrystal)
    registerFluidTextures(DivinityFluids.moltenGlass)
    registerFluidTextures(DivinityFluids.moltenCrystallizedSoulbone)
}

@SubscribeEvent
fun clientSetup(event: FMLClientSetupEvent) {
    BlockEntityRenderers.register(DivinityBlockEntities.bellows, ::BellowsRenderer)
}
