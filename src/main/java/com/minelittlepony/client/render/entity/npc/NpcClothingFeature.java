package com.minelittlepony.client.render.entity.npc;

import com.kenza.KenzaInjector;
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

import java.util.ArrayList;
import java.util.List;


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

    public static Identifier getClothingTexture(VillagerDataContainer entity, String entityType) {
        VillagerProfession profession = entity.getVillagerData().getProfession();

        return createTexture("minelittlepony", entityType, "profession", Registry.VILLAGER_PROFESSION.getId(profession));
    }

    public static Identifier createTexture(String namespace, String entityType, String category, Identifier identifier) {
        return KenzaInjector.INSTANCE.findTexture(category, identifier, entityType);

//                new Identifier(namespace, String.format("textures/entity/%s/%s/%s.png", entityType, category, identifier.getPath()));
    }

    public Identifier createTexture(String category, Identifier identifier) {

        return KenzaInjector.INSTANCE.findTexture(category, identifier, entityType);

//        return createTexture("minelittlepony", entityType, category, identifier);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider provider, int i, T entity, float f, float g, float h, float j, float k, float l) {
        if (entity.isInvisible()) {
            return;
        }

        VillagerData data = entity.getVillagerData();
        M entityModel = getContextModel();

        if (entity.isBaby() || data.getProfession() == VillagerProfession.NONE) {
//            Identifier typeSkin = createTexture("type", Registry.VILLAGER_TYPE.getId(data.getType()));
//            renderModel(entityModel, typeSkin, matrixStack, provider, i, entity, 1, 1, 1);
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

//        skins.add(createTexture("type", typeId));
        skins.add(createTexture("profession", Registry.VILLAGER_PROFESSION.getId(profession)));
        if (profession != VillagerProfession.NITWIT) {
            skins.add(createTexture("profession_level", LEVEL_TO_ID.get(level)));
        }

        return skins;
    }
}

//class NpcClothingFeature<
//        T extends LivingEntity & VillagerDataContainer,
//        M extends EntityModel<T> & IPonyModel<T> & ModelWithHat,
//        C extends FeatureRendererContext<T, M> & IPonyRenderContext<T, M>> extends AbstractPonyFeature<T, M> {
//
//    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap<>(), a -> {
//        a.put(1, new Identifier("stone"));
//        a.put(2, new Identifier("iron"));
//        a.put(3, new Identifier("gold"));
//        a.put(4, new Identifier("emerald"));
//        a.put(5, new Identifier("diamond"));
//    });
//
//    private final Object2ObjectMap<VillagerType, HatType> typeHatCache = new Object2ObjectOpenHashMap<>();
//    private final Object2ObjectMap<VillagerProfession, HatType> profHatCache = new Object2ObjectOpenHashMap<>();
//
//    private final String entityType;
//
//    private final ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
//
//    public NpcClothingFeature(C context, String type) {
//        super(context);
//        entityType = type;
//    }
//
//    public static Identifier getClothingTexture(VillagerDataContainer entity, String entityType) {
//        VillagerProfession profession = entity.getVillagerData().getProfession();
//
//        return createTexture("minelittlepony", "profession", entityType, Registry.VILLAGER_PROFESSION.getId(profession));
//    }
//
//    public static Identifier createTexture(String namespace, String type, String entityType, Identifier profession) {
//        return new Identifier(namespace, String.format("textures/entity/%s/%s/%s.png", entityType, type, profession.getPath()));
//    }
//
//
//    protected static <T extends LivingEntity> void renderModel(EntityModel<T> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float red, float green, float blue) {
//        VertexConsumer vertexConsumer = vertexConsumers.getBuffer( RenderLayer.getArmorCutoutNoCull(texture));
//        model.render(matrices, vertexConsumer, light,  LivingEntityRenderer.getOverlay(entity, 0.0F), red, green, blue, 1.0F);
//    }
//
//    @Override
//    public void render(MatrixStack matrixStack, VertexConsumerProvider provider, int i, T entity, float f, float g, float h, float j, float k, float l) {
//        if (!entity.isInvisible()) {
//
//            VillagerData data = entity.getVillagerData();
//            VillagerType type = data.getType();
//            VillagerProfession profession = data.getProfession();
//
//            HatType typeHatLayer = getHatType(typeHatCache, "type", Registry.VILLAGER_TYPE, type);
//            HatType profHatLayer = getHatType(profHatCache, "profession", Registry.VILLAGER_PROFESSION, profession);
//            M entityModel = getContextModel();
//
//            entityModel.setHatVisible(
//                    profHatLayer == VillagerResourceMetadata.HatType.NONE
//                            || (profHatLayer == VillagerResourceMetadata.HatType.PARTIAL && typeHatLayer != VillagerResourceMetadata.HatType.FULL)
//            );
//
//
//            Identifier typeSkin = findTexture(entity, type);
//
////            renderModel(entityModel, typeSkin, matrixStack, provider, i, entity, 1, 1, 1);
//
//            entityModel.setHatVisible(true);
//
//            if (profession != VillagerProfession.NONE && !entity.isBaby()) {
//                Identifier professionSkin = findTexture("profession", Registry.VILLAGER_PROFESSION.getId(profession));
//
//                renderModel(entityModel, professionSkin, matrixStack, provider, i, entity, 1, 1, 1);
//
//                if (profession != VillagerProfession.NITWIT) {
//                    Identifier levelSkin = findTexture("profession_level", LEVEL_TO_ID.get(MathHelper.clamp(data.getLevel(), 1, LEVEL_TO_ID.size())));
//
//                    renderModel(entityModel, levelSkin, matrixStack, provider, i, entity, 1, 1, 1);
//                }
//            }
//        }
//    }
//
//    public <K> VillagerResourceMetadata.HatType getHatType(Object2ObjectMap<K, HatType> cache, String type, DefaultedRegistry<K> registry, K key) {
//        if (cache.containsKey(key)) {
//            return cache.get(key); // People often complain that villagers cause lag,
//            // so let's do better than Mojang and rather NOT go
//            // through all the lambda generations if we can avoid it.
//        }
//        return loadHatType(cache, type, registry, key);
//    }
//
//
//
//
//
//    private <K> VillagerResourceMetadata.HatType loadHatType(Map<K, HatType> cache, String type, DefaultedRegistry<K> registry, K key) {
//        return cache.computeIfAbsent(key, k -> {
//            try {
//
//                Resource res = resourceManager.getResource(findTexture(type, registry.getId(k))).get();
//                VillagerResourceMetadata meta = res.getMetadata(VillagerResourceMetadata.READER);
//                if (meta != null) {
//                    return meta.getHatType();
//                }
//            } catch (IOException e) {
//            }
//            return HatType.NONE;
//        });
//    }
//
//    public Identifier findTexture(String category, Identifier identifier) {
//        return KenzaInjector.INSTANCE.findTexture(category, identifier, entityType);
//    }
//
//    public Identifier findTexture(Entity entity, VillagerType type) {
////        if (canLoadDynamicPonySkin(entity)) {
////            return KenzaInjector.INSTANCE.findTexture(entity);
////        } else {
//            return findTexture("type", Registry.VILLAGER_TYPE.getId(type));
////        }
//    }
//}
