package com.themysterys.spu.mixin;

import com.themysterys.spu.PackPlacement;
import com.themysterys.spu.PackSpot;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(Pack.Position.class)
public class PackPositionMixin {

    @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
    public <T> void insert(List<T> items, T item, Function<T, PackSelectionConfig> profileGetter, boolean listInverted, CallbackInfoReturnable<Integer> cir) {
        if (!(item instanceof Pack pack) || pack.getPackSource() != PackSource.SERVER) {
            return;
        }
        int size = items.size();
        List<String> keys = new ArrayList<>(size);
        int fixedBottom = 0;
        boolean stillFixed = true;
        for (int row = 0; row < size; row++) {
            int at = listInverted ? row : size - 1 - row;
            keys.add(PackPlacement.keyOf((Pack) items.get(at)));
        }
        for (int row = size - 1; row >= 0 && stillFixed; row--) {
            int at = listInverted ? row : size - 1 - row;
            PackSelectionConfig config = profileGetter.apply(items.get(at));
            stillFixed = PackPlacement.isFixedBottom(config);
            if (stillFixed) {
                fixedBottom++;
            }
        }
        PackSpot spot = PackPlacement.spotOf(pack);
        int above = spot == null ? 0 : spot.indexIn(keys, fixedBottom);
        int index = listInverted ? above : size - above;
        items.add(index, item);
        cir.setReturnValue(index);
    }
}
