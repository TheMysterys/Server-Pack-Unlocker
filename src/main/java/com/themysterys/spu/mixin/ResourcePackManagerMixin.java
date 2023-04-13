package com.themysterys.spu.mixin;

import com.themysterys.spu.ServerPackUnlocker;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {

    @Inject(method = "buildEnabledProfiles", at = @At("RETURN"), cancellable = true)
    private void onBuildEnabledProfiles(Collection<String> enabledNames, CallbackInfoReturnable<List<ResourcePackProfile>> cir) {
        ResourcePackProfile currentServerPack = ServerPackUnlocker.getCurrentServerPack();
        if (currentServerPack != null) {
            ArrayList<ResourcePackProfile> enabledPacks = new ArrayList<>(cir.getReturnValue());
            if (ServerPackUnlocker.hasModifiedPacks()) {
                ServerPackUnlocker.setModifiedPacks(false);
                int packIndex = new ArrayList<>(enabledNames).indexOf(currentServerPack.getName());
                if (packIndex == (enabledNames.size() - 1)) packIndex = -1;
                ServerPackUnlocker.getPackManager().addPack(ServerPackUnlocker.getCurrentServerAddress(), packIndex);
                ServerPackUnlocker.getPackManager().writeJSON();

                return;
            }

            int packIndex = ServerPackUnlocker.getPackManager().getPackPosition(ServerPackUnlocker.getCurrentServerAddress());
            if (packIndex < 0 || packIndex > enabledNames.size()) packIndex = enabledPacks.size()-1;

            enabledPacks.remove(currentServerPack);
            enabledPacks.add(packIndex, currentServerPack);

            cir.setReturnValue(enabledPacks);
        }
    }
}
