package com.minelittlepony.api.model;

public interface PonyModelConstants {
    float
        PI = (float)Math.PI,

        ROTATE_270 = 4.712F,
        ROTATE_90 = 1.571F,

        BASE_MODEL_SCALE = 15/16F,

        BODY_ROT_X = 0,
        BODY_ROT_X_SNEAK = 0.4F,
        BODY_ROT_X_RIDING = PI * 3.8F,

        BODY_RP_Y = 0,
        BODY_RP_Y_SNEAK = 7,
        BODY_RP_Y_RIDING = 1,

        BODY_RP_Z = 0,
        BODY_RP_Z_SNEAK = -4,
        BODY_RP_Z_RIDING = 4,

        FRONT_LEG_RP_Y = 8,
        FRONT_LEG_RP_Y_SNEAK = 8,

        WING_ROT_Z_FLYING = ROTATE_270 + 0.4F,
        WING_ROT_Z_SNEAK = 4,

        LEG_ROT_X_SNEAK_ADJ = 0.4F,

        LEG_SLEEP_OFFSET_Y = 2,
        FRONT_LEG_SLEEP_OFFSET_Z = 6,
        BACK_LEG_SLEEP_OFFSET_Z = -8,

        TAIL_RP_Z = 14,
        TAIL_RP_Z_SNEAK = 15,

        TAIL_RP_Y_RIDING = 3,
        TAIL_RP_Z_RIDING = 13,

        HEAD_RP_X_SNEAK = 0,
        HEAD_RP_Y_SNEAK = 6,
        HEAD_RP_Z_SNEAK = -2,

        HEAD_RP_X_SLEEP = 1,
        HEAD_RP_Y_SLEEP = 2,

        HEAD_RP_Y_SWIM = -2,
        HEAD_RP_Z_SWIM = -4,

        NECK_ROT_X = 0.166F,

        FIN_ROT_Y = PI / 6;
}
