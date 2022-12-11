package com.themysterys.spu.mixin;

import net.minecraft.client.resource.ServerResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerResourcePackProvider.class)
public class ServerResourcePackProviderMixin {

    @ModifyArg(
            method = "loadServerPack(Ljava/io/File;Lnet/minecraft/resource/ResourcePackSource;)Ljava/util/concurrent/CompletableFuture;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackProfile;of(Ljava/lang/String;Lnet/minecraft/text/Text;ZLnet/minecraft/resource/ResourcePackProfile$PackFactory;Lnet/minecraft/resource/ResourcePackProfile$Metadata;Lnet/minecraft/resource/ResourceType;Lnet/minecraft/resource/ResourcePackProfile$InsertionPosition;ZLnet/minecraft/resource/ResourcePackSource;)Lnet/minecraft/resource/ResourcePackProfile;"),
            index = 7
    )
    private boolean loadServerPack(boolean isPinned) {
        return false;
    }

}