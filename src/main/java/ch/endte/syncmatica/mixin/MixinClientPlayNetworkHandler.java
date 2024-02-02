package ch.endte.syncmatica.mixin;

import ch.endte.syncmatica.SyncmaticaReference;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.network.ClientNetworkPlayHandler;
import ch.endte.syncmatica.network.ClientNetworkPlayInitHandler;
import ch.endte.syncmatica.network.packet.ActorClientPlayNetworkHandler;
import ch.endte.syncmatica.network.payload.PacketType;
import ch.endte.syncmatica.network.payload.channels.RegisterVersion;
import ch.endte.syncmatica.network.payload.channels.SyncmaticaNbtData;
import ch.endte.syncmatica.util.SyncLog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 998)
public abstract class MixinClientPlayNetworkHandler
{
    @Unique
    public ExchangeTarget exTarget = null;

    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    private void syncmatica$handlePacket(CustomPayload packet, CallbackInfo ci) {
        // ChannelManager.onChannelRegisterHandle(getExchangeTarget(), packet.getChannel(), packet.getData());
        if (!MinecraftClient.getInstance().isOnThread())
        {
            return; //only execute packet on main thread
        }

        PacketType type =  PacketType.getType(packet.getId().id());
        if (type != null && !SyncmaticaReference.isSinglePlayer()) {
            // For NbtData hello packets
            if (type.equals(PacketType.NBT_DATA))
            {
                SyncmaticaNbtData payload = (SyncmaticaNbtData) packet;
                ActorClientPlayNetworkHandler.getInstance().packetNbtEvent(type, payload.data(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            else if (type.equals(PacketType.REGISTER_VERSION))
            {
                RegisterVersion payload = (RegisterVersion) packet;
                ActorClientPlayNetworkHandler.getInstance().packetEvent(type, payload.byteBuf(), (ClientPlayNetworkHandler) (Object) this, ci);
            }
            // Not for us
        }
    }
    @Unique
    private ExchangeTarget getExchangeTarget()
    {
        if (exTarget == null)
        {
            exTarget = new ExchangeTarget((ClientPlayNetworkHandler) (Object) this);
        }
        return exTarget;
    }
    @Inject(method = "onGameJoin", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;joinWorld(" +
                    "Lnet/minecraft/client/world/ClientWorld;)V"))
    private void syncmatica_onPreGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        SyncLog.debug("MixinClientPlayNetworkHandler#syncmatica_onPreGameJoin(): invoked");
        if (SyncmaticaReference.isClient())
            ClientNetworkPlayInitHandler.registerPlayChannels();
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void syncmatica_onPostGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        SyncLog.debug("MixinClientPlayNetworkHandler#syncmatica_onPostGameJoin(): invoked");
        if (SyncmaticaReference.isClient())
        {
            ClientNetworkPlayInitHandler.registerReceivers();

            //ActorClientPlayNetworkHandler.getInstance().startClient();

            if (!SyncmaticaReference.isSinglePlayer()) {
                SyncLog.debug("MixinClientPlayNetworkHandler#syncmatica_onPostGameJoin(): yeet test hello packet.");
                // Test hello packet (NBT)
                NbtCompound nbt = new NbtCompound();
                nbt.putString(SyncmaticaNbtData.KEY, "hello");
                SyncmaticaNbtData payload = new SyncmaticaNbtData(nbt);
                ClientNetworkPlayHandler.sendSyncPacket(payload);
            }
            else
                SyncLog.debug("MixinClientPlayNetworkHandler#syncmatica_onPostGameJoin(): Single player detected; skipping.");
        }
    }
}
