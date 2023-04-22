package com.themysterys.spu.mixin;

import com.themysterys.spu.ServerPackUnlocker;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourcePackProfile;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class ClientBuiltinResourcePackProviderMixin {

    @Shadow @Nullable private ResourcePackProfile serverContainer;

    @ModifyArg(
            method = "loadServerPack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackProfile;<init>(Ljava/lang/String;ZLjava/util/function/Supplier;Lnet/minecraft/text/Text;Lnet/minecraft/text/Text;Lnet/minecraft/resource/ResourcePackCompatibility;Lnet/minecraft/resource/ResourcePackProfile$InsertionPosition;ZLnet/minecraft/resource/ResourcePackSource;)V"),
            index = 7
    )
    private boolean loadServerPack(boolean isPinned) {
        return false;
    }

    @Inject(
            method = "loadServerPack",
            at = @At(value = "TAIL")
    )
    private void storeCurrentServerPack(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ServerPackUnlocker.setCurrentServerPack(serverContainer);
    }

}