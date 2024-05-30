package com.themysterys.spu.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ResourcePackSendS2CPacket.class)
public abstract class ResourcePackSendS2CPacketMixin {

    @Shadow
    public abstract UUID id();

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientCommonPacketListener;)V", at = @At("HEAD"), cancellable = true)
    private void apply(ClientCommonPacketListener clientCommonPacketListener, CallbackInfo ci) {
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        if (serverInfo != null && serverInfo.getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.DISABLED) {
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(this.id(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            }
            ci.cancel();
        }
    }

}
