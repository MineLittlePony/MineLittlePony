package com.minelittlepony.client.render.entity.npc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.*;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.AbstractPonyFeature;
import com.minelittlepony.client.render.entity.npc.textures.SillyPonyTextureSupplier;
import com.minelittlepony.client.util.render.TextureFlattener;
import com.minelittlepony.util.ResourceUtil;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.*;

class NpcClothingFeature<
        T extends LivingEntity & VillagerDataHolder,
        S extends SillyPonyTextureSupplier.State,
        M extends ClientPonyModel<S>,
        C extends RenderLayerParent<S, M> & PonyRenderContext<T, S, M>> extends AbstractPonyFeature<S, M> {

    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap<>(), a -> {
        a.put(1, Identifier.withDefaultNamespace("stone"));
        a.put(2, Identifier.withDefaultNamespace("iron"));
        a.put(3, Identifier.withDefaultNamespace("gold"));
        a.put(4, Identifier.withDefaultNamespace("emerald"));
        a.put(5, Identifier.withDefaultNamespace("diamond"));
    });
    private final Set<Identifier> loadedTextures = new HashSet<>();

    private final String entityType;

    public NpcClothingFeature(C context, String type) {
        super(context);
        entityType = type;
    }

    @Override
    public void submit(PoseStack matrixStack, SubmitNodeCollector queue, int light, S state, float yRot, float xRot) {
        if (state.isInvisible) {
            return;
        }

        M entityModel = getParentModel();

        if (state.isBaby || state.profession.equals(VillagerProfession.NONE)) {
            Identifier typeSkin = createTexture("type", state.type.identifier());
            if (!ResourceUtil.textureExists(typeSkin)) {
                typeSkin = createTexture("type", VillagerType.PLAINS.identifier());
            }
            renderColoredCutoutModel(entityModel, typeSkin, matrixStack, queue, light, state, CommonColors.WHITE, 1);
        } else {
            renderColoredCutoutModel(entityModel, getMergedTexture(state), matrixStack, queue, light, state, CommonColors.WHITE, 1);
        }
    }

    public Identifier getMergedTexture(S state) {
        ResourceKey<VillagerType> type = state.type;
        ResourceKey<VillagerProfession> profession = state.profession;
        int level = Mth.clamp(state.level, 1, LEVEL_TO_ID.size());

        Identifier typeId = type.identifier();
        Identifier profId = profession.identifier();

        Identifier key = MineLittlePony.id((typeId + "/" + profId + "/" + level).replace(':', '_'));

        if (loadedTextures.add(key) && !ResourceUtil.textureExists(key)) {
            TextureFlattener.flatten(computeTextures(typeId, profId, profession.equals(VillagerProfession.NITWIT) ? -1 : level), key);
        }

        return key;
    }

    private List<Identifier> computeTextures(Identifier typeId, Identifier profId, int level) {
        List<Identifier> skins = new ArrayList<>();

        Identifier typeTexture = createTexture("type", typeId);
        if (ResourceUtil.textureExists(typeTexture)) {
            skins.add(typeTexture);
        }

        Identifier profTexture = createTexture("profession", profId);
        skins.add(ResourceUtil.textureExists(profTexture) ? profTexture : createTexture("profession", VillagerProfession.NITWIT.identifier()));

        if (level != -1) {
            skins.add(createTexture("profession_level", LEVEL_TO_ID.get(level)));
        }

        return skins;
    }

    public Identifier createTexture(S state, String category) {
        return createTexture(category, state.profession.identifier());
    }

    private Identifier createTexture(String category, Identifier identifier) {
        return MineLittlePony.id(String.format("textures/entity/%s/%s/%s.png", entityType, category, identifier.getPath()));
    }
}
