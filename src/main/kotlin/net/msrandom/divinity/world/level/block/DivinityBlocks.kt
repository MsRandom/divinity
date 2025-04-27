package net.msrandom.divinity.world.level.block

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.msrandom.divinity.Divinity
import net.msrandom.divinity.getValue
import net.msrandom.divinity.world.Registrar
import net.msrandom.divinity.world.item.BlueCrystalItem
import net.msrandom.divinity.world.item.DivinityItems
import net.msrandom.divinity.world.level.fluid.DivinityFluids
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object DivinityBlocks : Registrar<Block> {
    override val register: DeferredRegister.Blocks = DeferredRegister.createBlocks(Divinity.MOD_ID)

    val blueCrystal: BlueCrystalBlock by blockWithItem("blue_crystal", ::BlueCrystalBlock, itemFactory = ::BlueCrystalItem)

    val circuitStamper: CircuitStamperBlock by blockWithItem("circuit_stamper", ::CircuitStamperBlock)
    val bellows: BellowsBlock by blockWithItem("bellows", ::BellowsBlock)

    val moltenBlueCrystal: LiquidBlock by moltenFluidBlock("molten_blue_crystal", DivinityFluids::moltenBlueCrystal)
    val moltenYellowCrystal: LiquidBlock by moltenFluidBlock("molten_yellow_crystal", DivinityFluids::moltenYellowCrystal)
    val moltenGlass: LiquidBlock by moltenFluidBlock("molten_glass", DivinityFluids::moltenGlass)

    private fun moltenFluidBlock(name: String, fluid: () -> FlowingFluid): DeferredBlock<LiquidBlock> {
        return register.registerBlock(name) {
            LiquidBlock(
                fluid(),
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.FIRE)
                    .replaceable()
                    .noCollission()
                    .randomTicks()
                    .strength(100.0F)
                    .lightLevel { 15 }
                    .pushReaction(PushReaction.DESTROY)
                    .noLootTable()
                    .liquid()
                    .sound(SoundType.EMPTY)
            )
        }
    }

    private fun <T : Block> blockWithItem(
        name: String,
        factory: (properties: BlockBehaviour.Properties) -> T,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        itemFactory: (block: Block, properties: Item.Properties) -> BlockItem = ::BlockItem,
        itemProperties: Item.Properties = Item.Properties(),
    ): DeferredBlock<T> {
        val block = register.registerBlock(name, factory, properties)

        DivinityItems.register.register(name) { ->
            itemFactory(block.get(), itemProperties)
        }

        return block
    }
}
