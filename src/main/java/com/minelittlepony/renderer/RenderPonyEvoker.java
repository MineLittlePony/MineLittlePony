package com.minelittlepony.renderer;

import com.minelittlepony.PonyData;
import com.minelittlepony.PonyDataSerialzier;
import com.minelittlepony.model.pony.ModelEvokerPony;
import com.minelittlepony.model.pony.ModelIllagerPony;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class RenderPonyEvoker extends RenderLiving<EntityEvoker> {

    private static final ResourceLocation EVOKER = new ResourceLocation("minelittlepony", "textures/entity/illager/evoker_pony.png");

    public RenderPonyEvoker(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelEvokerPony(), 0.5F);
    }

    @Override
    public void doRender(EntityEvoker entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ModelIllagerPony model = (ModelIllagerPony) this.getMainModel();

        IResourceManager resources = Minecraft.getMinecraft().getResourceManager();
        try {
            model.glowColor = 0x4444aa;
            model.isUnicorn = false;
            IResource resource = resources.getResource(EVOKER);
            if (resource.hasMetadata()) {
                PonyData meta = resource.getMetadata(PonyDataSerialzier.NAME);
                model.isUnicorn = meta.hasMagic();
                model.glowColor = meta.getGlowColor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEvoker entity) {
        return EVOKER;
    }

    @Override
    protected void preRenderCallback(EntityEvoker entitylivingbaseIn, float partialTickTime) {

        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

}
