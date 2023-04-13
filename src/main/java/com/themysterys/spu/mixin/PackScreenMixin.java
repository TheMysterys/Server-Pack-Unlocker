package com.themysterys.spu.mixin;

import com.themysterys.spu.ServerPackUnlocker;
import net.minecraft.client.gui.screen.pack.PackScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackScreen.class)
public class PackScreenMixin {

    @Inject(method = "close", at = @At("HEAD"))
    private void closedPackScreen(CallbackInfo in) {
        if (ServerPackUnlocker.getCurrentServerAddress() != null)
            ServerPackUnlocker.setModifiedPacks(true);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void openedPackScreen(CallbackInfo in) {
        if (ServerPackUnlocker.getCurrentServerAddress() != null)
            ServerPackUnlocker.setModifiedPacks(true);
    }
}
