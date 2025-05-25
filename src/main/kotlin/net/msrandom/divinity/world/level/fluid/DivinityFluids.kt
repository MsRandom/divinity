package net.msrandom.divinity.world.level.fluid

import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.pathfinder.PathType
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.level.block.DivinityBlocks
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.SoundActions
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidInteractionRegistry
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object DivinityFluids : Registrar<Fluid>(Registries.FLUID) {
    val fluidTypeRegister: DeferredRegister<FluidType> =
        DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Divinity.MOD_ID)

    private val interactionHandlers =
        mutableListOf<Pair<DeferredHolder<FluidType, FluidType>, FluidInteractionRegistry.InteractionInformation>>()

    val moltenBlueCrystal: FlowingFluid by register(
        "molten_blue_crystal",
        DivinityBlocks::moltenBlueCrystal,
        DivinityBlocks::blueCrystal
    )

    val moltenYellowCrystal: FlowingFluid by register(
        "molten_yellow_crystal",
        DivinityBlocks::moltenYellowCrystal,
        DivinityBlocks::blueCrystal
    )

    val moltenCrystallizedSoulbone: FlowingFluid by register(
        "molten_crystallized_soulbone",
        DivinityBlocks::moltenCrystallizedSoulbone,
        null,

        lightLevel = 12,
        temperature = 1200,
        tickRate = 35,
        slopeFindDistance = 2,
    )

    val moltenGlass: FlowingFluid by register("molten_glass", DivinityBlocks::moltenGlass, Blocks::GLASS)

    internal fun registerFluidInteractions(@Suppress("unused") event: FMLCommonSetupEvent) {
        for ((fluidType, interaction) in interactionHandlers) {
            FluidInteractionRegistry.addInteraction(fluidType.get(), interaction)
        }
    }

    private fun register(
        name: String,
        liquidBlock: () -> LiquidBlock,
        solidForm: (() -> Block)?,
        lightLevel: Int = 10,
        temperature: Int = 1000,
        tickRate: Int = 40,
        slopeFindDistance: Int = 1,
    ): DeferredHolder<Fluid, MoltenFluid.Source> {
        val fluidType = fluidTypeRegister.register(name) { ->
            FluidType(
                FluidType.Properties
                    .create()
                    .descriptionId("block.${Divinity.MOD_ID}.$name")
                    .canSwim(false)
                    .canDrown(false)
                    .pathType(PathType.LAVA)
                    .adjacentPathType(null)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA) // TODO Should be custom sound events
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .lightLevel(lightLevel)
                    .density(3000)
                    .viscosity(6000)
                    .temperature(temperature)
            )
        }

        if (solidForm != null) {
            interactionHandlers.add(fluidType to getMoltenFluidInteraction(solidForm))
        }

        val fluid = object {
            val source: DeferredHolder<Fluid, MoltenFluid.Source> = register.register(name) { ->
                MoltenFluid.Source(fluidProperties)
            }

            val flowing: DeferredHolder<Fluid, MoltenFluid.Flowing> = register.register("flowing_$name") { ->
                MoltenFluid.Flowing(fluidProperties)
            }

            val bucket: DeferredHolder<Item, BucketItem> = DivinityItems.register.registerItem("${name}_bucket", {
                BucketItem(source.get(), it)
            }, Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))

            val fluidProperties = BaseFlowingFluid.Properties(fluidType, source, flowing)
                .block(liquidBlock)
                .bucket(bucket)
                .explosionResistance(100f)
                .tickRate(tickRate)
                .slopeFindDistance(slopeFindDistance)
                .levelDecreasePerBlock(2)
        }.source

        return fluid
    }
}
