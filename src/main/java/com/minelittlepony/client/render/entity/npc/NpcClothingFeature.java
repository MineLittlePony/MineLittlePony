package com.minelittlepony.client.render.entity.npc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.entity.feature.AbstractPonyFeature;
import com.minelittlepony.client.util.render.TextureFlattener;

import java.util.*;

class NpcClothingFeature<
        T extends LivingEntity & VillagerDataContainer,
        M extends EntityModel<T> & IPonyModel<T> & ModelWithHat,
        C extends FeatureRendererContext<T, M> & IPonyRenderContext<T, M>> extends AbstractPonyFeature<T, M> {

    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap<>(), a -> {
        a.put(1, new Identifier("stone"));
        a.put(2, new Identifier("iron"));
        a.put(3, new Identifier("gold"));
        a.put(4, new Identifier("emerald"));
        a.put(5, new Identifier("diamond"));
    });

    private final String entityType;

    public NpcClothingFeature(C context, String type) {
        super(context);
        entityType = type;
    }

    @Deprecated(forRemoval = true)
    public static Identifier getClothingTexture(VillagerDataContainer entity, String entityType) {
        VillagerProfession profession = entity.getVillagerData().getProfession();

        return createTexture("minelittlepony", entityType, "profession", Registry.VILLAGER_PROFESSION.getId(profession));
    }

    public static Identifier createTexture(String namespace, String entityType, String category, Identifier profession) {
        return new Identifier(namespace, String.format("textures/entity/%s/%s/%s.png", entityType, category, profession.getPath()));
    }

    public Identifier createTexture(VillagerDataContainer entity, String category) {
        VillagerProfession profession = entity.getVillagerData().getProfession();

        return createTexture(category, Registry.VILLAGER_PROFESSION.getId(profession));
    }

    public Identifier createTexture(String category, Identifier identifier) {
        return createTexture("minelittlepony", entityType, category, identifier);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider provider, int i, T entity, float f, float g, float h, float j, float k, float l) {
        if (entity.isInvisible()) {
            return;
        }

        VillagerData data = entity.getVillagerData();
        M entityModel = getContextModel();

        if (entity.isBaby() || data.getProfession() == VillagerProfession.NONE) {
            Identifier typeSkin = createTexture("type", Registry.VILLAGER_TYPE.getId(data.getType()));

            renderModel(entityModel, typeSkin, matrixStack, provider, i, entity, 1, 1, 1);
        } else {
            renderModel(entityModel, getMergedTexture(data), matrixStack, provider, i, entity, 1, 1, 1);
        }
    }

    public Identifier getMergedTexture(VillagerData data) {
        VillagerType type = data.getType();
        VillagerProfession profession = data.getProfession();
        int level = MathHelper.clamp(data.getLevel(), 1, LEVEL_TO_ID.size());

        Identifier typeId = Registry.VILLAGER_TYPE.getId(type);
        Identifier profId = Registry.VILLAGER_PROFESSION.getId(profession);

        Identifier key = new Identifier("minelittlepony", (typeId + "/" + profId + "/" + level).replace(':', '_'));

        if (MinecraftClient.getInstance().getTextureManager().getOrDefault(key, null) == null) {
            TextureFlattener.flatten(computeTextures(type, profession, typeId, profId, level), key);
        }

        return key;
    }

    private List<Identifier> computeTextures(VillagerType type, VillagerProfession profession, Identifier typeId, Identifier profId, int level) {
        List<Identifier> skins = new ArrayList<>();

        skins.add(createTexture("type", typeId));
        skins.add(createTexture("profession", Registry.VILLAGER_PROFESSION.getId(profession)));
        if (profession != VillagerProfession.NITWIT) {
            skins.add(createTexture("profession_level", LEVEL_TO_ID.get(level)));
        }

        return skins;
    }
}
