package com.voxelmodpack.hdskins;

public class VanillaModels {
    public static final String SLIM = "slim";
    public static final String DEFAULT = "default";

    public static String of(String model) {
        return model != null && model.contains(SLIM) ? SLIM : DEFAULT;
    }

    public static String nonNull(String model) {
        return model == null ? DEFAULT : SLIM;
    }

    public static boolean isSlim(String model) {
        return SLIM.equals(model);
    }

    public static boolean isFat(String model) {
        return DEFAULT.equals(model);
    }
}
