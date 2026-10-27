package com.themysterys.spu.mixin;

import com.themysterys.spu.PackPlacement;
import com.themysterys.spu.PackSpot;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PackSelectionModel.class)
public abstract class PackSelectionModelMixin {

    @Shadow
    @Final
    private List<Pack> selected;

    @Inject(method = "commit", at = @At("HEAD"))
    private void rememberServerPackPlaces(CallbackInfo ci) {
        List<String> keys = new ArrayList<>(selected.size());
        for (Pack pack : selected) {
            keys.add(PackPlacement.keyOf(pack));
        }
        int fixedBottom = 0;
        for (int row = selected.size() - 1; row >= 0 && PackPlacement.isFixedBottom(selected.get(row).selectionConfig()); row--) {
            fixedBottom++;
        }
        for (int row = 0; row < selected.size(); row++) {
            Pack pack = selected.get(row);
            if (pack.getPackSource() == PackSource.SERVER) {
                PackPlacement.remember(pack, PackSpot.around(keys, row, fixedBottom), keys);
            }
        }
    }
}
