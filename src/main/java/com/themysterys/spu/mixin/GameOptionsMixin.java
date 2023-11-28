package com.themysterys.spu.mixin;

import com.themysterys.spu.ServerPackUnlocker;
import net.minecraft.client.option.GameOptions;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow public List<String> resourcePacks;

    @Inject(method = "refreshResourcePacks", at = @At("HEAD"))
    private void onRefreshResourcePacks(ResourcePackManager resourcePackManager, CallbackInfo ci) {
        if (ServerPackUnlocker.getCurrentServerPack() == null) return;
        if (this.resourcePacks.contains("server")) return;
        List<String> updatedPacks = resourcePackManager.getEnabledProfiles().stream()
                .filter(rp -> !rp.isPinned())
                .map(ResourcePackProfile::getName)
                .collect(Collectors.toList());
        int serverPackIndex = updatedPacks.indexOf("server");
        if (serverPackIndex == -1)
            serverPackIndex = this.resourcePacks.size();
        this.resourcePacks.add(Math.min(this.resourcePacks.size(), serverPackIndex), "server");
    }
}
