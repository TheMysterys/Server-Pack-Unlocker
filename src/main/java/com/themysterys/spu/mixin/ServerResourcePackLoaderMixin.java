package com.themysterys.spu.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.client.resource.server.ReloadScheduler;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.List;

@Mixin(ServerResourcePackLoader.class)
public class ServerResourcePackLoaderMixin {

    @Inject(method = "toProfiles", at = @At(value = "RETURN"), cancellable = true)
    private void toProfiles(List<ReloadScheduler.PackInfo> packs, CallbackInfoReturnable<List<ResourcePackProfile>> cir) {
        List<ResourcePackProfile> returnList = cir.getReturnValue();
        int size = packs.size();

        // 从后往前获取size个returnList
        List<ResourcePackProfile> profiles = returnList.subList(returnList.size() - size, returnList.size());

        for (int i = 0; i < size; i++) {
            // 获取packInfo
            ReloadScheduler.PackInfo packInfo = packs.get(i);
            // 获取profile
            ResourcePackProfile profile = profiles.get(i);

            // 获取参数
            String name = profile.getName();
            Text displayName = profile.getDisplayName();
            ResourcePackSource source = profile.getSource();

            // 获取path
            Path path = packInfo.path();

            // 创建packFactory
            ResourcePackProfile.PackFactory packFactory = new ZipResourcePack.ZipBackedFactory(path, false);
            int count = SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES);
            ResourcePackProfile.Metadata metadata = ResourcePackProfile.loadMetadata(name, packFactory, count);
            if (metadata == null) {
                return;
            }

            // 移除旧的profile
            returnList.remove(profile);

            // 创建新的profile
            returnList.add(ResourcePackProfile.of(name, displayName, true, packFactory, metadata, ResourcePackProfile.InsertionPosition.TOP, false, source));
        }

        // 设置返回值
        cir.setReturnValue(returnList);
    }
}
