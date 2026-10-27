package com.themysterys.spu.mixin;

import com.themysterys.spu.PackPlacement;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.resources.server.PackReloadConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DownloadedPackSource.class)
public abstract class DownloadedPackSourceMixin {

    @Inject(method = "loadRequestedPacks", at = @At("HEAD"))
    private void hashServerPacks(List<PackReloadConfig.IdAndPath> packs, CallbackInfoReturnable<List<?>> cir) {
        for (PackReloadConfig.IdAndPath pack : packs) {
            PackPlacement.hashFile(pack.id(), pack.path());
        }
    }
}
