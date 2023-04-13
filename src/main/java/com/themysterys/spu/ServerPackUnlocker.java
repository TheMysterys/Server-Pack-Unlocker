package com.themysterys.spu;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.resource.ResourcePackProfile;

public class ServerPackUnlocker implements ModInitializer {

    private static ServerPackUnlocker instance;
    private PackManager packManager;
    private ResourcePackProfile currentServerPack;

    private boolean modifiedPacks = false;

    private PlayerConnectionEventHandler playerConnectionEventHandler;

    @Override
    public void onInitialize() {
        instance = this;
        packManager = new PackManager();

        playerConnectionEventHandler = new PlayerConnectionEventHandler();
        ClientPlayConnectionEvents.JOIN.register(playerConnectionEventHandler);
        ClientPlayConnectionEvents.DISCONNECT.register(playerConnectionEventHandler);
    }

    public static PackManager getPackManager() {
        return instance.packManager;
    }

    public static ResourcePackProfile getCurrentServerPack() {
        return instance.currentServerPack;
    }

    public static void setCurrentServerPack(ResourcePackProfile currentServerPack) {
        instance.currentServerPack = currentServerPack;
    }

    public static String getCurrentServerAddress() {
        return instance.playerConnectionEventHandler.currentServerAddress;
    }

    public static boolean hasModifiedPacks() {
        return instance.modifiedPacks;
    }

    public static void setModifiedPacks(boolean modifiedPacks) {
        instance.modifiedPacks = modifiedPacks;
    }
}
