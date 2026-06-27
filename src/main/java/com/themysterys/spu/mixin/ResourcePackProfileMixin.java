package com.themysterys.spu.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ResourcePackProfile.class)
public abstract class ResourcePackProfileMixin {

    @ModifyReturnValue(method = "isPinned", at = @At("RETURN"))
    private boolean isFixedPosition(boolean original) {
        return false;
    }
}
