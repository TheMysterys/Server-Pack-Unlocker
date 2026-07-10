package com.themysterys.spu.mixin;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonPacketListenerImplMixin {

    @Shadow
    @Final
    @Nullable
    protected ServerInfo serverInfo;

    @Shadow
    @Final
    protected ClientConnection connection;

    @Inject(method = "onResourcePackSend", at = @At("HEAD"), cancellable = true)
    public void rejectPackPrompt(ResourcePackSendS2CPacket packet, CallbackInfo ci) {
        ServerInfo.ResourcePackPolicy serverPackStatus = this.serverInfo != null ? this.serverInfo.getResourcePackPolicy() : null;
        if (serverPackStatus == ServerInfo.ResourcePackPolicy.DISABLED) {
            connection.send(new ResourcePackStatusC2SPacket(packet.id(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            ci.cancel();
        }
    }
}
