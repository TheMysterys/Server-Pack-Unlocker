package com.themysterys.spu.mixin;

import net.minecraft.client.resource.ServerResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerResourcePackProvider.class)
public class ServerResourcePackProviderMixin {

    /*@ModifyArg(method = "loadServerPack", at = @At(value = "INVOKE", target = ""), index = 7)
    private boolean loadServerPack(boolean isPinned) {
        return false;
    }*/

}