package com.themysterys.spu.mixin;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(ResourcePackProfile.InsertionPosition.class)
public class PackPositionMixin {

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    public <T> void insert(List<T> items, T item, Function<T, ResourcePackProfile> profileGetter, boolean listInverted, CallbackInfoReturnable<Integer> cir) {
        int i;
        ResourcePackProfile pack;
        if (((ResourcePackProfile) item).getSource() == ResourcePackSource.SERVER) {
            System.out.println("[SPU] Pack List Size:" + items.size());
            for (i = 0; i < items.size(); i++) {
                System.out.println("[SPU] Pack Item:" + ((ResourcePackProfile) items.get(i)).getName());
                pack = (ResourcePackProfile) items.get(i);
                if (!pack.isPinned() || pack.getInitialPosition() != ResourcePackProfile.InsertionPosition.TOP ) {
                    break;
                }
            }
            i += 2;
            items.add(i, item);
            cir.setReturnValue(i);
        }
    }
}
