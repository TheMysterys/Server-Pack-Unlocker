package com.themysterys.spu;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;

public class PlayerConnectionEventHandler implements ClientPlayConnectionEvents.Join, ClientPlayConnectionEvents.Disconnect {

    public String currentServerAddress;

    @Override
    public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (!client.isInSingleplayer()) {
            ServerInfo serverInfo = client.getCurrentServerEntry();
            if (serverInfo == null) return;
            this.currentServerAddress = serverInfo.address;
            handler.sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
        }
    }

    @Override
    public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        if (!client.isInSingleplayer() && ServerPackUnlocker.getCurrentServerPack() != null) {
            this.currentServerAddress = null;
            ServerPackUnlocker.setCurrentServerPack(null);
        }
    }
}
