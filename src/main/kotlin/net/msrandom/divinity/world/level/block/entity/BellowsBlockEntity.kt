package net.msrandom.divinity.world.level.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import java.util.Optional
import kotlin.math.max

class BellowsBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(DivinityBlockEntities.bellows, pos, state) {
    var lastActivationTick = 0L
    var totalProgress = 0
    private var triggered = false

    /**
     * Indicates that the bellows is active and the animation should interpolate from a closed state to an open one
     */
    internal var isBeingPushed = false

    fun addProgress(progress: Int) {
        totalProgress += progress

        if (totalProgress >= MAX_TICK_PROGRESS) {
            val pos = blockPos.relative(blockState.getValue(HorizontalDirectionalBlock.FACING))

            markFinished()
            activate(pos)
        }
    }

    fun removeProgress(progress: Int) {
        totalProgress = max(totalProgress - progress, 0)
    }

    private fun activate(pos: BlockPos) {
        if (!level!!.isClientSide) {
            level!!.server!!.logChatMessage(
                Component.literal("Pushed"), ChatType.Bound(
                    level!!.registryAccess().registryOrThrow(Registries.CHAT_TYPE).getHolder(ChatType.CHAT).get(),
                    Component.literal("Bellow"), Optional.empty()
                ), null
            )
        }
        DivinityBlockEntities.blowMold.getBlockEntity(level!!, pos)?.craft()
    }

    private fun markFinished() {
        // TODO Send client packet
        triggered = true

        setChanged()
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket =
        ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply {
            putLong("LastActivationTick", lastActivationTick)
            putByte("TotalProgress", totalProgress.toByte())
            putBoolean("IsBeingPushed", isBeingPushed)
        }
    }

    override fun onDataPacket(
        net: Connection,
        pkt: ClientboundBlockEntityDataPacket,
        lookupProvider: HolderLookup.Provider
    ) = handleUpdateTag(pkt.tag, lookupProvider)

    override fun handleUpdateTag(tag: CompoundTag, lookupProvider: HolderLookup.Provider) {
        lastActivationTick = tag.getLong("LastActivationTick")
        totalProgress = tag.getByte("TotalProgress").toInt()
        isBeingPushed = tag.getBoolean("IsBeingPushed")
    }

    override fun setChanged() {
        super.setChanged()

        level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS)
    }

    companion object {
        internal const val MAX_TICK_PROGRESS = 25
        private const val COMPLETION_ANALOG_SIGNAL_TICKS = 2
    }
}
