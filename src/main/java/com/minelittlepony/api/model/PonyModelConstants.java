package com.minelittlepony.api.model;

public interface PonyModelConstants {

    float
        PI = (float)Math.PI,

        BASE_MODEL_SCALE = 15/16F,

        BODY_CENTRE_X = 0,
        BODY_CENTRE_Y = 8,
        BODY_CENTRE_Z = 6,

        NECK_CENTRE_X = BODY_CENTRE_X - 2,
        NECK_CENTRE_Y = BODY_CENTRE_Y - 6.8F,
        NECK_CENTRE_Z = BODY_CENTRE_Z - 8.8F,

        BODY_ROT_X_NOTSNEAK = 0,
        BODY_ROT_X_SNEAK = 0.4F,
        BODY_ROT_X_RIDING = PI * 3.8F,

        BODY_RP_Y_NOTSNEAK = 0,
        BODY_RP_Y_SNEAK = 7,
        BODY_RP_Y_RIDING = 1,

        BODY_RP_Z_NOTSNEAK = 0,
        BODY_RP_Z_SNEAK = -4,
        BODY_RP_Z_RIDING = 4,

        FRONT_LEG_RP_Y_NOTSNEAK = 8,
        FRONT_LEG_RP_Y_SNEAK = 8,

        HEAD_CENTRE_X = 0,
        HEAD_CENTRE_Y = -1,
        HEAD_CENTRE_Z = -2,

        HEAD_RP_X = 0,
        HEAD_RP_Y = 0,
        HEAD_RP_Z = 0,

        HORN_X = HEAD_CENTRE_X - 0.5F,
        HORN_Y = HEAD_CENTRE_Y - 10,
        HORN_Z = HEAD_CENTRE_Z - 1.5F,

        EXT_WING_RP_X = 4.5F,
        EXT_WING_RP_Y = 5.3F,
        EXT_WING_RP_Z = 6,

        WING_ROT_Z_SNEAK = 4,

        ROTATE_270 = 4.712F,
        ROTATE_90 = 1.571F,

        LEG_ROT_X_SNEAK_ADJ = 0.4F,

        TAIL_RP_X = 0,
        TAIL_RP_Y = 0,

        TAIL_RP_Z_NOTSNEAK = 14,
        TAIL_RP_Z_SNEAK = 15,

        TAIL_RP_Y_RIDING = 3,
        TAIL_RP_Z_RIDING = 13,

        THIRDP_ARM_CENTRE_X = 0,
        THIRDP_ARM_CENTRE_Y = 4,

        WING_FOLDED_RP_Y = 13,
        WING_FOLDED_RP_Z = -2,

        NECK_ROT_X = 0.166F,

        FIN_ROT_Y = PI / 6;
}
