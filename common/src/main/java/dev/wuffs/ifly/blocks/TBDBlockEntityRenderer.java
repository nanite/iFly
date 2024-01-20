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

public class TBDBlockEntityRenderer implements BlockEntityRenderer<TbdBlockEntity> {

    public TBDBlockEntityRenderer(BlockEntityRendererProvider.Context context){

    }
    @Override
    public void render(TbdBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        LevelRenderer.renderLineBox(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES), new AABB(BlockPos.ZERO).inflate(16D), 1,0.5f,1,1);
    }
}
