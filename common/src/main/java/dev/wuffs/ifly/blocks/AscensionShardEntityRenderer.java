package dev.wuffs.ifly.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class AscensionShardEntityRenderer implements BlockEntityRenderer<AscensionShardBlockEntity> {

    public AscensionShardEntityRenderer(BlockEntityRendererProvider.Context context){

    }

    @Override
    public void render(AscensionShardBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        AABB aabb = new AABB(BlockPos.ZERO).inflate(AscensionShardBlockEntity.RADIUS).setMinY(blockEntity.getLevel().getMinBuildHeight()).setMaxY(blockEntity.getLevel().getMaxBuildHeight());
        LevelRenderer.renderLineBox(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES), aabb, 1,0.5f,1,1);
    }
}
