package com.mumfrey.liteloader.gl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.TexGen;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 * Convenience class for working with Mojang's GLStateManager:
 * 
 * <p>It would be pretty tolerable to work with GLStateManager as a static
 * import were it not for the fact that you still need to import the GL
 * namespaces themselves from LWJGL in order to get the constants, and also have
 * to deal with the fact that GLStateManager's methods don't have "gl-style"
 * names, making it annoying to work with. This class is designed to function as
 * an adapter to allow changeover to be more painless. Using this class means
 * that the following code:</p>
 * 
 * <pre><code>glEnable(GL_BLEND);
 * glAlphaFunc(GL_GREATER, 0.0F);</code></pre>
 *   
 * <p>becomes:</p>
 * 
 * <pre><code>glEnableBlend();
 * glAlphaFunc(GL_GREATER, 0.0F);</code></pre>
 *   
 * <p>Notice that the <tt>glAlphaFunc</tt> invocation remains unchanged, and the
 * <tt>glEnable</tt> call simply gets replaced with a logical equivalent which
 * invokes the GLStateManager method behind the scenes.</p>
 * 
 * <p>To use this class, simply replace existing static imports in your classes
 * with this single static import, then change <tt>glEnable</tt> and <tt>
 * glDisable</tt> calls accordingly.
 * 
 * @author Adam Mummery-Smith
 */
public class GL
{
    // GL11
    public static final int GL_ACCUM = 0x100;
    public static final int GL_LOAD = 0x101;
    public static final int GL_RETURN = 0x102;
    public static final int GL_MULT = 0x103;
    public static final int GL_ADD = 0x104;
    public static final int GL_NEVER = 0x200;
    public static final int GL_LESS = 0x201;
    public static final int GL_EQUAL = 0x202;
    public static final int GL_LEQUAL = 0x203;
    public static final int GL_GREATER = 0x204;
    public static final int GL_NOTEQUAL = 0x205;
    public static final int GL_GEQUAL = 0x206;
    public static final int GL_ALWAYS = 0x207;
    public static final int GL_CURRENT_BIT = 0x1;
    public static final int GL_POINT_BIT = 0x2;
    public static final int GL_LINE_BIT = 0x4;
    public static final int GL_POLYGON_BIT = 0x8;
    public static final int GL_POLYGON_STIPPLE_BIT = 0x10;
    public static final int GL_PIXEL_MODE_BIT = 0x20;
    public static final int GL_LIGHTING_BIT = 0x40;
    public static final int GL_FOG_BIT = 0x80;
    public static final int GL_DEPTH_BUFFER_BIT = 0x100;
    public static final int GL_ACCUM_BUFFER_BIT = 0x200;
    public static final int GL_STENCIL_BUFFER_BIT = 0x400;
    public static final int GL_VIEWPORT_BIT = 0x800;
    public static final int GL_TRANSFORM_BIT = 0x1000;
    public static final int GL_ENABLE_BIT = 0x2000;
    public static final int GL_COLOR_BUFFER_BIT = 0x4000;
    public static final int GL_HINT_BIT = 0x8000;
    public static final int GL_EVAL_BIT = 0x10000;
    public static final int GL_LIST_BIT = 0x20000;
    public static final int GL_TEXTURE_BIT = 0x40000;
    public static final int GL_SCISSOR_BIT = 0x80000;
    public static final int GL_ALL_ATTRIB_BITS = 0xfffff;
    public static final int GL_POINTS = 0x0;
    public static final int GL_LINES = 0x1;
    public static final int GL_LINE_LOOP = 0x2;
    public static final int GL_LINE_STRIP = 0x3;
    public static final int GL_TRIANGLES = 0x4;
    public static final int GL_TRIANGLE_STRIP = 0x5;
    public static final int GL_TRIANGLE_FAN = 0x6;
    public static final int GL_QUADS = 0x7;
    public static final int GL_QUAD_STRIP = 0x8;
    public static final int GL_POLYGON = 0x9;
    public static final int GL_ZERO = 0x0;
    public static final int GL_ONE = 0x1;
    public static final int GL_SRC_COLOR = 0x300;
    public static final int GL_ONE_MINUS_SRC_COLOR = 0x301;
    public static final int GL_SRC_ALPHA = 0x302;
    public static final int GL_ONE_MINUS_SRC_ALPHA = 0x303;
    public static final int GL_DST_ALPHA = 0x304;
    public static final int GL_ONE_MINUS_DST_ALPHA = 0x305;
    public static final int GL_DST_COLOR = 0x306;
    public static final int GL_ONE_MINUS_DST_COLOR = 0x307;
    public static final int GL_SRC_ALPHA_SATURATE = 0x308;
    public static final int GL_CONSTANT_COLOR = 0x8001;
    public static final int GL_ONE_MINUS_CONSTANT_COLOR = 0x8002;
    public static final int GL_CONSTANT_ALPHA = 0x8003;
    public static final int GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004;
    public static final int GL_TRUE = 0x1;
    public static final int GL_FALSE = 0x0;
    public static final int GL_CLIP_PLANE0 = 0x3000;
    public static final int GL_CLIP_PLANE1 = 0x3001;
    public static final int GL_CLIP_PLANE2 = 0x3002;
    public static final int GL_CLIP_PLANE3 = 0x3003;
    public static final int GL_CLIP_PLANE4 = 0x3004;
    public static final int GL_CLIP_PLANE5 = 0x3005;
    public static final int GL_BYTE = 0x1400;
    public static final int GL_UNSIGNED_BYTE = 0x1401;
    public static final int GL_SHORT = 0x1402;
    public static final int GL_UNSIGNED_SHORT = 0x1403;
    public static final int GL_INT = 0x1404;
    public static final int GL_UNSIGNED_INT = 0x1405;
    public static final int GL_FLOAT = 0x1406;
    public static final int GL_2_BYTES = 0x1407;
    public static final int GL_3_BYTES = 0x1408;
    public static final int GL_4_BYTES = 0x1409;
    public static final int GL_DOUBLE = 0x140a;
    public static final int GL_NONE = 0x0;
    public static final int GL_FRONT_LEFT = 0x400;
    public static final int GL_FRONT_RIGHT = 0x401;
    public static final int GL_BACK_LEFT = 0x402;
    public static final int GL_BACK_RIGHT = 0x403;
    public static final int GL_FRONT = 0x404;
    public static final int GL_BACK = 0x405;
    public static final int GL_LEFT = 0x406;
    public static final int GL_RIGHT = 0x407;
    public static final int GL_FRONT_AND_BACK = 0x408;
    public static final int GL_AUX0 = 0x409;
    public static final int GL_AUX1 = 0x40a;
    public static final int GL_AUX2 = 0x40b;
    public static final int GL_AUX3 = 0x40c;
    public static final int GL_NO_ERROR = 0x0;
    public static final int GL_INVALID_ENUM = 0x500;
    public static final int GL_INVALID_VALUE = 0x501;
    public static final int GL_INVALID_OPERATION = 0x502;
    public static final int GL_STACK_OVERFLOW = 0x503;
    public static final int GL_STACK_UNDERFLOW = 0x504;
    public static final int GL_OUT_OF_MEMORY = 0x505;
    public static final int GL_2D = 0x600;
    public static final int GL_3D = 0x601;
    public static final int GL_3D_COLOR = 0x602;
    public static final int GL_3D_COLOR_TEXTURE = 0x603;
    public static final int GL_4D_COLOR_TEXTURE = 0x604;
    public static final int GL_PASS_THROUGH_TOKEN = 0x700;
    public static final int GL_POINT_TOKEN = 0x701;
    public static final int GL_LINE_TOKEN = 0x702;
    public static final int GL_POLYGON_TOKEN = 0x703;
    public static final int GL_BITMAP_TOKEN = 0x704;
    public static final int GL_DRAW_PIXEL_TOKEN = 0x705;
    public static final int GL_COPY_PIXEL_TOKEN = 0x706;
    public static final int GL_LINE_RESET_TOKEN = 0x707;
    public static final int GL_EXP = 0x800;
    public static final int GL_EXP2 = 0x801;
    public static final int GL_CW = 0x900;
    public static final int GL_CCW = 0x901;
    public static final int GL_COEFF = 0xa00;
    public static final int GL_ORDER = 0xa01;
    public static final int GL_DOMAIN = 0xa02;
    public static final int GL_CURRENT_COLOR = 0xb00;
    public static final int GL_CURRENT_INDEX = 0xb01;
    public static final int GL_CURRENT_NORMAL = 0xb02;
    public static final int GL_CURRENT_TEXTURE_COORDS = 0xb03;
    public static final int GL_CURRENT_RASTER_COLOR = 0xb04;
    public static final int GL_CURRENT_RASTER_INDEX = 0xb05;
    public static final int GL_CURRENT_RASTER_TEXTURE_COORDS = 0xb06;
    public static final int GL_CURRENT_RASTER_POSITION = 0xb07;
    public static final int GL_CURRENT_RASTER_POSITION_VALID = 0xb08;
    public static final int GL_CURRENT_RASTER_DISTANCE = 0xb09;
    public static final int GL_POINT_SMOOTH = 0xb10;
    public static final int GL_POINT_SIZE = 0xb11;
    public static final int GL_POINT_SIZE_RANGE = 0xb12;
    public static final int GL_POINT_SIZE_GRANULARITY = 0xb13;
    public static final int GL_LINE_SMOOTH = 0xb20;
    public static final int GL_LINE_WIDTH = 0xb21;
    public static final int GL_LINE_WIDTH_RANGE = 0xb22;
    public static final int GL_LINE_WIDTH_GRANULARITY = 0xb23;
    public static final int GL_LINE_STIPPLE = 0xb24;
    public static final int GL_LINE_STIPPLE_PATTERN = 0xb25;
    public static final int GL_LINE_STIPPLE_REPEAT = 0xb26;
    public static final int GL_LIST_MODE = 0xb30;
    public static final int GL_MAX_LIST_NESTING = 0xb31;
    public static final int GL_LIST_BASE = 0xb32;
    public static final int GL_LIST_INDEX = 0xb33;
    public static final int GL_POLYGON_MODE = 0xb40;
    public static final int GL_POLYGON_SMOOTH = 0xb41;
    public static final int GL_POLYGON_STIPPLE = 0xb42;
    public static final int GL_EDGE_FLAG = 0xb43;
    public static final int GL_CULL_FACE = 0xb44;
    public static final int GL_CULL_FACE_MODE = 0xb45;
    public static final int GL_FRONT_FACE = 0xb46;
    public static final int GL_LIGHTING = 0xb50;
    public static final int GL_LIGHT_MODEL_LOCAL_VIEWER = 0xb51;
    public static final int GL_LIGHT_MODEL_TWO_SIDE = 0xb52;
    public static final int GL_LIGHT_MODEL_AMBIENT = 0xb53;
    public static final int GL_SHADE_MODEL = 0xb54;
    public static final int GL_COLOR_MATERIAL_FACE = 0xb55;
    public static final int GL_COLOR_MATERIAL_PARAMETER = 0xb56;
    public static final int GL_COLOR_MATERIAL = 0xb57;
    public static final int GL_FOG = 0xb60;
    public static final int GL_FOG_INDEX = 0xb61;
    public static final int GL_FOG_DENSITY = 0xb62;
    public static final int GL_FOG_START = 0xb63;
    public static final int GL_FOG_END = 0xb64;
    public static final int GL_FOG_MODE = 0xb65;
    public static final int GL_FOG_COLOR = 0xb66;
    public static final int GL_DEPTH_RANGE = 0xb70;
    public static final int GL_DEPTH_TEST = 0xb71;
    public static final int GL_DEPTH_WRITEMASK = 0xb72;
    public static final int GL_DEPTH_CLEAR_VALUE = 0xb73;
    public static final int GL_DEPTH_FUNC = 0xb74;
    public static final int GL_ACCUM_CLEAR_VALUE = 0xb80;
    public static final int GL_STENCIL_TEST = 0xb90;
    public static final int GL_STENCIL_CLEAR_VALUE = 0xb91;
    public static final int GL_STENCIL_FUNC = 0xb92;
    public static final int GL_STENCIL_VALUE_MASK = 0xb93;
    public static final int GL_STENCIL_FAIL = 0xb94;
    public static final int GL_STENCIL_PASS_DEPTH_FAIL = 0xb95;
    public static final int GL_STENCIL_PASS_DEPTH_PASS = 0xb96;
    public static final int GL_STENCIL_REF = 0xb97;
    public static final int GL_STENCIL_WRITEMASK = 0xb98;
    public static final int GL_MATRIX_MODE = 0xba0;
    public static final int GL_NORMALIZE = 0xba1;
    public static final int GL_VIEWPORT = 0xba2;
    public static final int GL_MODELVIEW_STACK_DEPTH = 0xba3;
    public static final int GL_PROJECTION_STACK_DEPTH = 0xba4;
    public static final int GL_TEXTURE_STACK_DEPTH = 0xba5;
    public static final int GL_MODELVIEW_MATRIX = 0xba6;
    public static final int GL_PROJECTION_MATRIX = 0xba7;
    public static final int GL_TEXTURE_MATRIX = 0xba8;
    public static final int GL_ATTRIB_STACK_DEPTH = 0xbb0;
    public static final int GL_CLIENT_ATTRIB_STACK_DEPTH = 0xbb1;
    public static final int GL_ALPHA_TEST = 0xbc0;
    public static final int GL_ALPHA_TEST_FUNC = 0xbc1;
    public static final int GL_ALPHA_TEST_REF = 0xbc2;
    public static final int GL_DITHER = 0xbd0;
    public static final int GL_BLEND_DST = 0xbe0;
    public static final int GL_BLEND_SRC = 0xbe1;
    public static final int GL_BLEND = 0xbe2;
    public static final int GL_LOGIC_OP_MODE = 0xbf0;
    public static final int GL_INDEX_LOGIC_OP = 0xbf1;
    public static final int GL_COLOR_LOGIC_OP = 0xbf2;
    public static final int GL_AUX_BUFFERS = 0xc00;
    public static final int GL_DRAW_BUFFER = 0xc01;
    public static final int GL_READ_BUFFER = 0xc02;
    public static final int GL_SCISSOR_BOX = 0xc10;
    public static final int GL_SCISSOR_TEST = 0xc11;
    public static final int GL_INDEX_CLEAR_VALUE = 0xc20;
    public static final int GL_INDEX_WRITEMASK = 0xc21;
    public static final int GL_COLOR_CLEAR_VALUE = 0xc22;
    public static final int GL_COLOR_WRITEMASK = 0xc23;
    public static final int GL_INDEX_MODE = 0xc30;
    public static final int GL_RGBA_MODE = 0xc31;
    public static final int GL_DOUBLEBUFFER = 0xc32;
    public static final int GL_STEREO = 0xc33;
    public static final int GL_RENDER_MODE = 0xc40;
    public static final int GL_PERSPECTIVE_CORRECTION_HINT = 0xc50;
    public static final int GL_POINT_SMOOTH_HINT = 0xc51;
    public static final int GL_LINE_SMOOTH_HINT = 0xc52;
    public static final int GL_POLYGON_SMOOTH_HINT = 0xc53;
    public static final int GL_FOG_HINT = 0xc54;
    public static final int GL_TEXTURE_GEN_S = 0xc60;
    public static final int GL_TEXTURE_GEN_T = 0xc61;
    public static final int GL_TEXTURE_GEN_R = 0xc62;
    public static final int GL_TEXTURE_GEN_Q = 0xc63;
    public static final int GL_PIXEL_MAP_I_TO_I = 0xc70;
    public static final int GL_PIXEL_MAP_S_TO_S = 0xc71;
    public static final int GL_PIXEL_MAP_I_TO_R = 0xc72;
    public static final int GL_PIXEL_MAP_I_TO_G = 0xc73;
    public static final int GL_PIXEL_MAP_I_TO_B = 0xc74;
    public static final int GL_PIXEL_MAP_I_TO_A = 0xc75;
    public static final int GL_PIXEL_MAP_R_TO_R = 0xc76;
    public static final int GL_PIXEL_MAP_G_TO_G = 0xc77;
    public static final int GL_PIXEL_MAP_B_TO_B = 0xc78;
    public static final int GL_PIXEL_MAP_A_TO_A = 0xc79;
    public static final int GL_PIXEL_MAP_I_TO_I_SIZE = 0xcb0;
    public static final int GL_PIXEL_MAP_S_TO_S_SIZE = 0xcb1;
    public static final int GL_PIXEL_MAP_I_TO_R_SIZE = 0xcb2;
    public static final int GL_PIXEL_MAP_I_TO_G_SIZE = 0xcb3;
    public static final int GL_PIXEL_MAP_I_TO_B_SIZE = 0xcb4;
    public static final int GL_PIXEL_MAP_I_TO_A_SIZE = 0xcb5;
    public static final int GL_PIXEL_MAP_R_TO_R_SIZE = 0xcb6;
    public static final int GL_PIXEL_MAP_G_TO_G_SIZE = 0xcb7;
    public static final int GL_PIXEL_MAP_B_TO_B_SIZE = 0xcb8;
    public static final int GL_PIXEL_MAP_A_TO_A_SIZE = 0xcb9;
    public static final int GL_UNPACK_SWAP_BYTES = 0xcf0;
    public static final int GL_UNPACK_LSB_FIRST = 0xcf1;
    public static final int GL_UNPACK_ROW_LENGTH = 0xcf2;
    public static final int GL_UNPACK_SKIP_ROWS = 0xcf3;
    public static final int GL_UNPACK_SKIP_PIXELS = 0xcf4;
    public static final int GL_UNPACK_ALIGNMENT = 0xcf5;
    public static final int GL_PACK_SWAP_BYTES = 0xd00;
    public static final int GL_PACK_LSB_FIRST = 0xd01;
    public static final int GL_PACK_ROW_LENGTH = 0xd02;
    public static final int GL_PACK_SKIP_ROWS = 0xd03;
    public static final int GL_PACK_SKIP_PIXELS = 0xd04;
    public static final int GL_PACK_ALIGNMENT = 0xd05;
    public static final int GL_MAP_COLOR = 0xd10;
    public static final int GL_MAP_STENCIL = 0xd11;
    public static final int GL_INDEX_SHIFT = 0xd12;
    public static final int GL_INDEX_OFFSET = 0xd13;
    public static final int GL_RED_SCALE = 0xd14;
    public static final int GL_RED_BIAS = 0xd15;
    public static final int GL_ZOOM_X = 0xd16;
    public static final int GL_ZOOM_Y = 0xd17;
    public static final int GL_GREEN_SCALE = 0xd18;
    public static final int GL_GREEN_BIAS = 0xd19;
    public static final int GL_BLUE_SCALE = 0xd1a;
    public static final int GL_BLUE_BIAS = 0xd1b;
    public static final int GL_ALPHA_SCALE = 0xd1c;
    public static final int GL_ALPHA_BIAS = 0xd1d;
    public static final int GL_DEPTH_SCALE = 0xd1e;
    public static final int GL_DEPTH_BIAS = 0xd1f;
    public static final int GL_MAX_EVAL_ORDER = 0xd30;
    public static final int GL_MAX_LIGHTS = 0xd31;
    public static final int GL_MAX_CLIP_PLANES = 0xd32;
    public static final int GL_MAX_TEXTURE_SIZE = 0xd33;
    public static final int GL_MAX_PIXEL_MAP_TABLE = 0xd34;
    public static final int GL_MAX_ATTRIB_STACK_DEPTH = 0xd35;
    public static final int GL_MAX_MODELVIEW_STACK_DEPTH = 0xd36;
    public static final int GL_MAX_NAME_STACK_DEPTH = 0xd37;
    public static final int GL_MAX_PROJECTION_STACK_DEPTH = 0xd38;
    public static final int GL_MAX_TEXTURE_STACK_DEPTH = 0xd39;
    public static final int GL_MAX_VIEWPORT_DIMS = 0xd3a;
    public static final int GL_MAX_CLIENT_ATTRIB_STACK_DEPTH = 0xd3b;
    public static final int GL_SUBPIXEL_BITS = 0xd50;
    public static final int GL_INDEX_BITS = 0xd51;
    public static final int GL_RED_BITS = 0xd52;
    public static final int GL_GREEN_BITS = 0xd53;
    public static final int GL_BLUE_BITS = 0xd54;
    public static final int GL_ALPHA_BITS = 0xd55;
    public static final int GL_DEPTH_BITS = 0xd56;
    public static final int GL_STENCIL_BITS = 0xd57;
    public static final int GL_ACCUM_RED_BITS = 0xd58;
    public static final int GL_ACCUM_GREEN_BITS = 0xd59;
    public static final int GL_ACCUM_BLUE_BITS = 0xd5a;
    public static final int GL_ACCUM_ALPHA_BITS = 0xd5b;
    public static final int GL_NAME_STACK_DEPTH = 0xd70;
    public static final int GL_AUTO_NORMAL = 0xd80;
    public static final int GL_MAP1_COLOR_4 = 0xd90;
    public static final int GL_MAP1_INDEX = 0xd91;
    public static final int GL_MAP1_NORMAL = 0xd92;
    public static final int GL_MAP1_TEXTURE_COORD_1 = 0xd93;
    public static final int GL_MAP1_TEXTURE_COORD_2 = 0xd94;
    public static final int GL_MAP1_TEXTURE_COORD_3 = 0xd95;
    public static final int GL_MAP1_TEXTURE_COORD_4 = 0xd96;
    public static final int GL_MAP1_VERTEX_3 = 0xd97;
    public static final int GL_MAP1_VERTEX_4 = 0xd98;
    public static final int GL_MAP2_COLOR_4 = 0xdb0;
    public static final int GL_MAP2_INDEX = 0xdb1;
    public static final int GL_MAP2_NORMAL = 0xdb2;
    public static final int GL_MAP2_TEXTURE_COORD_1 = 0xdb3;
    public static final int GL_MAP2_TEXTURE_COORD_2 = 0xdb4;
    public static final int GL_MAP2_TEXTURE_COORD_3 = 0xdb5;
    public static final int GL_MAP2_TEXTURE_COORD_4 = 0xdb6;
    public static final int GL_MAP2_VERTEX_3 = 0xdb7;
    public static final int GL_MAP2_VERTEX_4 = 0xdb8;
    public static final int GL_MAP1_GRID_DOMAIN = 0xdd0;
    public static final int GL_MAP1_GRID_SEGMENTS = 0xdd1;
    public static final int GL_MAP2_GRID_DOMAIN = 0xdd2;
    public static final int GL_MAP2_GRID_SEGMENTS = 0xdd3;
    public static final int GL_TEXTURE_1D = 0xde0;
    public static final int GL_TEXTURE_2D = 0xde1;
    public static final int GL_FEEDBACK_BUFFER_POINTER = 0xdf0;
    public static final int GL_FEEDBACK_BUFFER_SIZE = 0xdf1;
    public static final int GL_FEEDBACK_BUFFER_TYPE = 0xdf2;
    public static final int GL_SELECTION_BUFFER_POINTER = 0xdf3;
    public static final int GL_SELECTION_BUFFER_SIZE = 0xdf4;
    public static final int GL_TEXTURE_WIDTH = 0x1000;
    public static final int GL_TEXTURE_HEIGHT = 0x1001;
    public static final int GL_TEXTURE_INTERNAL_FORMAT = 0x1003;
    public static final int GL_TEXTURE_BORDER_COLOR = 0x1004;
    public static final int GL_TEXTURE_BORDER = 0x1005;
    public static final int GL_DONT_CARE = 0x1100;
    public static final int GL_FASTEST = 0x1101;
    public static final int GL_NICEST = 0x1102;
    public static final int GL_LIGHT0 = 0x4000;
    public static final int GL_LIGHT1 = 0x4001;
    public static final int GL_LIGHT2 = 0x4002;
    public static final int GL_LIGHT3 = 0x4003;
    public static final int GL_LIGHT4 = 0x4004;
    public static final int GL_LIGHT5 = 0x4005;
    public static final int GL_LIGHT6 = 0x4006;
    public static final int GL_LIGHT7 = 0x4007;
    public static final int GL_AMBIENT = 0x1200;
    public static final int GL_DIFFUSE = 0x1201;
    public static final int GL_SPECULAR = 0x1202;
    public static final int GL_POSITION = 0x1203;
    public static final int GL_SPOT_DIRECTION = 0x1204;
    public static final int GL_SPOT_EXPONENT = 0x1205;
    public static final int GL_SPOT_CUTOFF = 0x1206;
    public static final int GL_CONSTANT_ATTENUATION = 0x1207;
    public static final int GL_LINEAR_ATTENUATION = 0x1208;
    public static final int GL_QUADRATIC_ATTENUATION = 0x1209;
    public static final int GL_COMPILE = 0x1300;
    public static final int GL_COMPILE_AND_EXECUTE = 0x1301;
    public static final int GL_CLEAR = 0x1500;
    public static final int GL_AND = 0x1501;
    public static final int GL_AND_REVERSE = 0x1502;
    public static final int GL_COPY = 0x1503;
    public static final int GL_AND_INVERTED = 0x1504;
    public static final int GL_NOOP = 0x1505;
    public static final int GL_XOR = 0x1506;
    public static final int GL_OR = 0x1507;
    public static final int GL_NOR = 0x1508;
    public static final int GL_EQUIV = 0x1509;
    public static final int GL_INVERT = 0x150a;
    public static final int GL_OR_REVERSE = 0x150b;
    public static final int GL_COPY_INVERTED = 0x150c;
    public static final int GL_OR_INVERTED = 0x150d;
    public static final int GL_NAND = 0x150e;
    public static final int GL_SET = 0x150f;
    public static final int GL_EMISSION = 0x1600;
    public static final int GL_SHININESS = 0x1601;
    public static final int GL_AMBIENT_AND_DIFFUSE = 0x1602;
    public static final int GL_COLOR_INDEXES = 0x1603;
    public static final int GL_MODELVIEW = 0x1700;
    public static final int GL_PROJECTION = 0x1701;
    public static final int GL_TEXTURE = 0x1702;
    public static final int GL_COLOR = 0x1800;
    public static final int GL_DEPTH = 0x1801;
    public static final int GL_STENCIL = 0x1802;
    public static final int GL_COLOR_INDEX = 0x1900;
    public static final int GL_STENCIL_INDEX = 0x1901;
    public static final int GL_DEPTH_COMPONENT = 0x1902;
    public static final int GL_RED = 0x1903;
    public static final int GL_GREEN = 0x1904;
    public static final int GL_BLUE = 0x1905;
    public static final int GL_ALPHA = 0x1906;
    public static final int GL_RGB = 0x1907;
    public static final int GL_RGBA = 0x1908;
    public static final int GL_LUMINANCE = 0x1909;
    public static final int GL_LUMINANCE_ALPHA = 0x190a;
    public static final int GL_BITMAP = 0x1a00;
    public static final int GL_POINT = 0x1b00;
    public static final int GL_LINE = 0x1b01;
    public static final int GL_FILL = 0x1b02;
    public static final int GL_RENDER = 0x1c00;
    public static final int GL_FEEDBACK = 0x1c01;
    public static final int GL_SELECT = 0x1c02;
    public static final int GL_FLAT = 0x1d00;
    public static final int GL_SMOOTH = 0x1d01;
    public static final int GL_KEEP = 0x1e00;
    public static final int GL_REPLACE = 0x1e01;
    public static final int GL_INCR = 0x1e02;
    public static final int GL_DECR = 0x1e03;
    public static final int GL_VENDOR = 0x1f00;
    public static final int GL_RENDERER = 0x1f01;
    public static final int GL_VERSION = 0x1f02;
    public static final int GL_EXTENSIONS = 0x1f03;
    public static final int GL_S = 0x2000;
    public static final int GL_T = 0x2001;
    public static final int GL_R = 0x2002;
    public static final int GL_Q = 0x2003;
    public static final int GL_MODULATE = 0x2100;
    public static final int GL_DECAL = 0x2101;
    public static final int GL_TEXTURE_ENV_MODE = 0x2200;
    public static final int GL_TEXTURE_ENV_COLOR = 0x2201;
    public static final int GL_TEXTURE_ENV = 0x2300;
    public static final int GL_EYE_LINEAR = 0x2400;
    public static final int GL_OBJECT_LINEAR = 0x2401;
    public static final int GL_SPHERE_MAP = 0x2402;
    public static final int GL_TEXTURE_GEN_MODE = 0x2500;
    public static final int GL_OBJECT_PLANE = 0x2501;
    public static final int GL_EYE_PLANE = 0x2502;
    public static final int GL_NEAREST = 0x2600;
    public static final int GL_LINEAR = 0x2601;
    public static final int GL_NEAREST_MIPMAP_NEAREST = 0x2700;
    public static final int GL_LINEAR_MIPMAP_NEAREST = 0x2701;
    public static final int GL_NEAREST_MIPMAP_LINEAR = 0x2702;
    public static final int GL_LINEAR_MIPMAP_LINEAR = 0x2703;
    public static final int GL_TEXTURE_MAG_FILTER = 0x2800;
    public static final int GL_TEXTURE_MIN_FILTER = 0x2801;
    public static final int GL_TEXTURE_WRAP_S = 0x2802;
    public static final int GL_TEXTURE_WRAP_T = 0x2803;
    public static final int GL_CLAMP = 0x2900;
    public static final int GL_REPEAT = 0x2901;
    public static final int GL_CLIENT_PIXEL_STORE_BIT = 0x1;
    public static final int GL_CLIENT_VERTEX_ARRAY_BIT = 0x2;
    public static final int GL_ALL_CLIENT_ATTRIB_BITS = 0xffffffff;
    public static final int GL_POLYGON_OFFSET_FACTOR = 0x8038;
    public static final int GL_POLYGON_OFFSET_UNITS = 0x2a00;
    public static final int GL_POLYGON_OFFSET_POINT = 0x2a01;
    public static final int GL_POLYGON_OFFSET_LINE = 0x2a02;
    public static final int GL_POLYGON_OFFSET_FILL = 0x8037;
    public static final int GL_ALPHA4 = 0x803b;
    public static final int GL_ALPHA8 = 0x803c;
    public static final int GL_ALPHA12 = 0x803d;
    public static final int GL_ALPHA16 = 0x803e;
    public static final int GL_LUMINANCE4 = 0x803f;
    public static final int GL_LUMINANCE8 = 0x8040;
    public static final int GL_LUMINANCE12 = 0x8041;
    public static final int GL_LUMINANCE16 = 0x8042;
    public static final int GL_LUMINANCE4_ALPHA4 = 0x8043;
    public static final int GL_LUMINANCE6_ALPHA2 = 0x8044;
    public static final int GL_LUMINANCE8_ALPHA8 = 0x8045;
    public static final int GL_LUMINANCE12_ALPHA4 = 0x8046;
    public static final int GL_LUMINANCE12_ALPHA12 = 0x8047;
    public static final int GL_LUMINANCE16_ALPHA16 = 0x8048;
    public static final int GL_INTENSITY = 0x8049;
    public static final int GL_INTENSITY4 = 0x804a;
    public static final int GL_INTENSITY8 = 0x804b;
    public static final int GL_INTENSITY12 = 0x804c;
    public static final int GL_INTENSITY16 = 0x804d;
    public static final int GL_R3_G3_B2 = 0x2a10;
    public static final int GL_RGB4 = 0x804f;
    public static final int GL_RGB5 = 0x8050;
    public static final int GL_RGB8 = 0x8051;
    public static final int GL_RGB10 = 0x8052;
    public static final int GL_RGB12 = 0x8053;
    public static final int GL_RGB16 = 0x8054;
    public static final int GL_RGBA2 = 0x8055;
    public static final int GL_RGBA4 = 0x8056;
    public static final int GL_RGB5_A1 = 0x8057;
    public static final int GL_RGBA8 = 0x8058;
    public static final int GL_RGB10_A2 = 0x8059;
    public static final int GL_RGBA12 = 0x805a;
    public static final int GL_RGBA16 = 0x805b;
    public static final int GL_TEXTURE_RED_SIZE = 0x805c;
    public static final int GL_TEXTURE_GREEN_SIZE = 0x805d;
    public static final int GL_TEXTURE_BLUE_SIZE = 0x805e;
    public static final int GL_TEXTURE_ALPHA_SIZE = 0x805f;
    public static final int GL_TEXTURE_LUMINANCE_SIZE = 0x8060;
    public static final int GL_TEXTURE_INTENSITY_SIZE = 0x8061;
    public static final int GL_PROXY_TEXTURE_1D = 0x8063;
    public static final int GL_PROXY_TEXTURE_2D = 0x8064;
    public static final int GL_TEXTURE_PRIORITY = 0x8066;
    public static final int GL_TEXTURE_RESIDENT = 0x8067;
    public static final int GL_TEXTURE_BINDING_1D = 0x8068;
    public static final int GL_TEXTURE_BINDING_2D = 0x8069;
    public static final int GL_VERTEX_ARRAY = 0x8074;
    public static final int GL_NORMAL_ARRAY = 0x8075;
    public static final int GL_COLOR_ARRAY = 0x8076;
    public static final int GL_INDEX_ARRAY = 0x8077;
    public static final int GL_TEXTURE_COORD_ARRAY = 0x8078;
    public static final int GL_EDGE_FLAG_ARRAY = 0x8079;
    public static final int GL_VERTEX_ARRAY_SIZE = 0x807a;
    public static final int GL_VERTEX_ARRAY_TYPE = 0x807b;
    public static final int GL_VERTEX_ARRAY_STRIDE = 0x807c;
    public static final int GL_NORMAL_ARRAY_TYPE = 0x807e;
    public static final int GL_NORMAL_ARRAY_STRIDE = 0x807f;
    public static final int GL_COLOR_ARRAY_SIZE = 0x8081;
    public static final int GL_COLOR_ARRAY_TYPE = 0x8082;
    public static final int GL_COLOR_ARRAY_STRIDE = 0x8083;
    public static final int GL_INDEX_ARRAY_TYPE = 0x8085;
    public static final int GL_INDEX_ARRAY_STRIDE = 0x8086;
    public static final int GL_TEXTURE_COORD_ARRAY_SIZE = 0x8088;
    public static final int GL_TEXTURE_COORD_ARRAY_TYPE = 0x8089;
    public static final int GL_TEXTURE_COORD_ARRAY_STRIDE = 0x808a;
    public static final int GL_EDGE_FLAG_ARRAY_STRIDE = 0x808c;
    public static final int GL_VERTEX_ARRAY_POINTER = 0x808e;
    public static final int GL_NORMAL_ARRAY_POINTER = 0x808f;
    public static final int GL_COLOR_ARRAY_POINTER = 0x8090;
    public static final int GL_INDEX_ARRAY_POINTER = 0x8091;
    public static final int GL_TEXTURE_COORD_ARRAY_POINTER = 0x8092;
    public static final int GL_EDGE_FLAG_ARRAY_POINTER = 0x8093;
    public static final int GL_V2F = 0x2a20;
    public static final int GL_V3F = 0x2a21;
    public static final int GL_C4UB_V2F = 0x2a22;
    public static final int GL_C4UB_V3F = 0x2a23;
    public static final int GL_C3F_V3F = 0x2a24;
    public static final int GL_N3F_V3F = 0x2a25;
    public static final int GL_C4F_N3F_V3F = 0x2a26;
    public static final int GL_T2F_V3F = 0x2a27;
    public static final int GL_T4F_V4F = 0x2a28;
    public static final int GL_T2F_C4UB_V3F = 0x2a29;
    public static final int GL_T2F_C3F_V3F = 0x2a2a;
    public static final int GL_T2F_N3F_V3F = 0x2a2b;
    public static final int GL_T2F_C4F_N3F_V3F = 0x2a2c;
    public static final int GL_T4F_C4F_N3F_V4F = 0x2a2d;
    public static final int GL_LOGIC_OP = 0xbf1;
    public static final int GL_TEXTURE_COMPONENTS = 0x1003;

    // GL12
    public static final int GL_TEXTURE_BINDING_3D = 0x806a;
    public static final int GL_PACK_SKIP_IMAGES = 0x806b;
    public static final int GL_PACK_IMAGE_HEIGHT = 0x806c;
    public static final int GL_UNPACK_SKIP_IMAGES = 0x806d;
    public static final int GL_UNPACK_IMAGE_HEIGHT = 0x806e;
    public static final int GL_TEXTURE_3D = 0x806f;
    public static final int GL_PROXY_TEXTURE_3D = 0x8070;
    public static final int GL_TEXTURE_DEPTH = 0x8071;
    public static final int GL_TEXTURE_WRAP_R = 0x8072;
    public static final int GL_MAX_3D_TEXTURE_SIZE = 0x8073;
    public static final int GL_BGR = 0x80e0;
    public static final int GL_BGRA = 0x80e1;
    public static final int GL_UNSIGNED_BYTE_3_3_2 = 0x8032;
    public static final int GL_UNSIGNED_BYTE_2_3_3_REV = 0x8362;
    public static final int GL_UNSIGNED_SHORT_5_6_5 = 0x8363;
    public static final int GL_UNSIGNED_SHORT_5_6_5_REV = 0x8364;
    public static final int GL_UNSIGNED_SHORT_4_4_4_4 = 0x8033;
    public static final int GL_UNSIGNED_SHORT_4_4_4_4_REV = 0x8365;
    public static final int GL_UNSIGNED_SHORT_5_5_5_1 = 0x8034;
    public static final int GL_UNSIGNED_SHORT_1_5_5_5_REV = 0x8366;
    public static final int GL_UNSIGNED_INT_8_8_8_8 = 0x8035;
    public static final int GL_UNSIGNED_INT_8_8_8_8_REV = 0x8367;
    public static final int GL_UNSIGNED_INT_10_10_10_2 = 0x8036;
    public static final int GL_UNSIGNED_INT_2_10_10_10_REV = 0x8368;
    public static final int GL_RESCALE_NORMAL = 0x803a;
    public static final int GL_LIGHT_MODEL_COLOR_CONTROL = 0x81f8;
    public static final int GL_SINGLE_COLOR = 0x81f9;
    public static final int GL_SEPARATE_SPECULAR_COLOR = 0x81fa;
    public static final int GL_CLAMP_TO_EDGE = 0x812f;
    public static final int GL_TEXTURE_MIN_LOD = 0x813a;
    public static final int GL_TEXTURE_MAX_LOD = 0x813b;
    public static final int GL_TEXTURE_BASE_LEVEL = 0x813c;
    public static final int GL_TEXTURE_MAX_LEVEL = 0x813d;
    public static final int GL_MAX_ELEMENTS_VERTICES = 0x80e8;
    public static final int GL_MAX_ELEMENTS_INDICES = 0x80e9;
    public static final int GL_ALIASED_POINT_SIZE_RANGE = 0x846d;
    public static final int GL_ALIASED_LINE_WIDTH_RANGE = 0x846e;
    public static final int GL_SMOOTH_POINT_SIZE_RANGE = 0xb12;
    public static final int GL_SMOOTH_POINT_SIZE_GRANULARITY = 0xb13;
    public static final int GL_SMOOTH_LINE_WIDTH_RANGE = 0xb22;
    public static final int GL_SMOOTH_LINE_WIDTH_GRANULARITY = 0xb23;

    // GL13
    public static final int GL_TEXTURE0 = 0x84c0;
    public static final int GL_TEXTURE1 = 0x84c1;
    public static final int GL_TEXTURE2 = 0x84c2;
    public static final int GL_TEXTURE3 = 0x84c3;
    public static final int GL_TEXTURE4 = 0x84c4;
    public static final int GL_TEXTURE5 = 0x84c5;
    public static final int GL_TEXTURE6 = 0x84c6;
    public static final int GL_TEXTURE7 = 0x84c7;
    public static final int GL_TEXTURE8 = 0x84c8;
    public static final int GL_TEXTURE9 = 0x84c9;
    public static final int GL_TEXTURE10 = 0x84ca;
    public static final int GL_TEXTURE11 = 0x84cb;
    public static final int GL_TEXTURE12 = 0x84cc;
    public static final int GL_TEXTURE13 = 0x84cd;
    public static final int GL_TEXTURE14 = 0x84ce;
    public static final int GL_TEXTURE15 = 0x84cf;
    public static final int GL_TEXTURE16 = 0x84d0;
    public static final int GL_TEXTURE17 = 0x84d1;
    public static final int GL_TEXTURE18 = 0x84d2;
    public static final int GL_TEXTURE19 = 0x84d3;
    public static final int GL_TEXTURE20 = 0x84d4;
    public static final int GL_TEXTURE21 = 0x84d5;
    public static final int GL_TEXTURE22 = 0x84d6;
    public static final int GL_TEXTURE23 = 0x84d7;
    public static final int GL_TEXTURE24 = 0x84d8;
    public static final int GL_TEXTURE25 = 0x84d9;
    public static final int GL_TEXTURE26 = 0x84da;
    public static final int GL_TEXTURE27 = 0x84db;
    public static final int GL_TEXTURE28 = 0x84dc;
    public static final int GL_TEXTURE29 = 0x84dd;
    public static final int GL_TEXTURE30 = 0x84de;
    public static final int GL_TEXTURE31 = 0x84df;
    public static final int GL_ACTIVE_TEXTURE = 0x84e0;
    public static final int GL_CLIENT_ACTIVE_TEXTURE = 0x84e1;
    public static final int GL_MAX_TEXTURE_UNITS = 0x84e2;
    public static final int GL_NORMAL_MAP = 0x8511;
    public static final int GL_REFLECTION_MAP = 0x8512;
    public static final int GL_TEXTURE_CUBE_MAP = 0x8513;
    public static final int GL_TEXTURE_BINDING_CUBE_MAP = 0x8514;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 0x8515;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 0x8516;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 0x8517;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 0x8518;
    public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 0x8519;
    public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 0x851a;
    public static final int GL_PROXY_TEXTURE_CUBE_MAP = 0x851b;
    public static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 0x851c;
    public static final int GL_COMPRESSED_ALPHA = 0x84e9;
    public static final int GL_COMPRESSED_LUMINANCE = 0x84ea;
    public static final int GL_COMPRESSED_LUMINANCE_ALPHA = 0x84eb;
    public static final int GL_COMPRESSED_INTENSITY = 0x84ec;
    public static final int GL_COMPRESSED_RGB = 0x84ed;
    public static final int GL_COMPRESSED_RGBA = 0x84ee;
    public static final int GL_TEXTURE_COMPRESSION_HINT = 0x84ef;
    public static final int GL_TEXTURE_COMPRESSED_IMAGE_SIZE = 0x86a0;
    public static final int GL_TEXTURE_COMPRESSED = 0x86a1;
    public static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86a2;
    public static final int GL_COMPRESSED_TEXTURE_FORMATS = 0x86a3;
    public static final int GL_MULTISAMPLE = 0x809d;
    public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 0x809e;
    public static final int GL_SAMPLE_ALPHA_TO_ONE = 0x809f;
    public static final int GL_SAMPLE_COVERAGE = 0x80a0;
    public static final int GL_SAMPLE_BUFFERS = 0x80a8;
    public static final int GL_SAMPLES = 0x80a9;
    public static final int GL_SAMPLE_COVERAGE_VALUE = 0x80aa;
    public static final int GL_SAMPLE_COVERAGE_INVERT = 0x80ab;
    public static final int GL_MULTISAMPLE_BIT = 0x20000000;
    public static final int GL_TRANSPOSE_MODELVIEW_MATRIX = 0x84e3;
    public static final int GL_TRANSPOSE_PROJECTION_MATRIX = 0x84e4;
    public static final int GL_TRANSPOSE_TEXTURE_MATRIX = 0x84e5;
    public static final int GL_TRANSPOSE_COLOR_MATRIX = 0x84e6;
    public static final int GL_COMBINE = 0x8570;
    public static final int GL_COMBINE_RGB = 0x8571;
    public static final int GL_COMBINE_ALPHA = 0x8572;
    public static final int GL_SOURCE0_RGB = 0x8580;
    public static final int GL_SOURCE1_RGB = 0x8581;
    public static final int GL_SOURCE2_RGB = 0x8582;
    public static final int GL_SOURCE0_ALPHA = 0x8588;
    public static final int GL_SOURCE1_ALPHA = 0x8589;
    public static final int GL_SOURCE2_ALPHA = 0x858a;
    public static final int GL_OPERAND0_RGB = 0x8590;
    public static final int GL_OPERAND1_RGB = 0x8591;
    public static final int GL_OPERAND2_RGB = 0x8592;
    public static final int GL_OPERAND0_ALPHA = 0x8598;
    public static final int GL_OPERAND1_ALPHA = 0x8599;
    public static final int GL_OPERAND2_ALPHA = 0x859a;
    public static final int GL_RGB_SCALE = 0x8573;
    public static final int GL_ADD_SIGNED = 0x8574;
    public static final int GL_INTERPOLATE = 0x8575;
    public static final int GL_SUBTRACT = 0x84e7;
    public static final int GL_CONSTANT = 0x8576;
    public static final int GL_PRIMARY_COLOR = 0x8577;
    public static final int GL_PREVIOUS = 0x8578;
    public static final int GL_DOT3_RGB = 0x86ae;
    public static final int GL_DOT3_RGBA = 0x86af;
    public static final int GL_CLAMP_TO_BORDER = 0x812d;

    // GL14
    public static final int GL_GENERATE_MIPMAP = 0x8191;
    public static final int GL_GENERATE_MIPMAP_HINT = 0x8192;
    public static final int GL_DEPTH_COMPONENT16 = 0x81a5;
    public static final int GL_DEPTH_COMPONENT24 = 0x81a6;
    public static final int GL_DEPTH_COMPONENT32 = 0x81a7;
    public static final int GL_TEXTURE_DEPTH_SIZE = 0x884a;
    public static final int GL_DEPTH_TEXTURE_MODE = 0x884b;
    public static final int GL_TEXTURE_COMPARE_MODE = 0x884c;
    public static final int GL_TEXTURE_COMPARE_FUNC = 0x884d;
    public static final int GL_COMPARE_R_TO_TEXTURE = 0x884e;
    public static final int GL_FOG_COORDINATE_SOURCE = 0x8450;
    public static final int GL_FOG_COORDINATE = 0x8451;
    public static final int GL_FRAGMENT_DEPTH = 0x8452;
    public static final int GL_CURRENT_FOG_COORDINATE = 0x8453;
    public static final int GL_FOG_COORDINATE_ARRAY_TYPE = 0x8454;
    public static final int GL_FOG_COORDINATE_ARRAY_STRIDE = 0x8455;
    public static final int GL_FOG_COORDINATE_ARRAY_POINTER = 0x8456;
    public static final int GL_FOG_COORDINATE_ARRAY = 0x8457;
    public static final int GL_POINT_SIZE_MIN = 0x8126;
    public static final int GL_POINT_SIZE_MAX = 0x8127;
    public static final int GL_POINT_FADE_THRESHOLD_SIZE = 0x8128;
    public static final int GL_POINT_DISTANCE_ATTENUATION = 0x8129;
    public static final int GL_COLOR_SUM = 0x8458;
    public static final int GL_CURRENT_SECONDARY_COLOR = 0x8459;
    public static final int GL_SECONDARY_COLOR_ARRAY_SIZE = 0x845a;
    public static final int GL_SECONDARY_COLOR_ARRAY_TYPE = 0x845b;
    public static final int GL_SECONDARY_COLOR_ARRAY_STRIDE = 0x845c;
    public static final int GL_SECONDARY_COLOR_ARRAY_POINTER = 0x845d;
    public static final int GL_SECONDARY_COLOR_ARRAY = 0x845e;
    public static final int GL_BLEND_DST_RGB = 0x80c8;
    public static final int GL_BLEND_SRC_RGB = 0x80c9;
    public static final int GL_BLEND_DST_ALPHA = 0x80ca;
    public static final int GL_BLEND_SRC_ALPHA = 0x80cb;
    public static final int GL_INCR_WRAP = 0x8507;
    public static final int GL_DECR_WRAP = 0x8508;
    public static final int GL_TEXTURE_FILTER_CONTROL = 0x8500;
    public static final int GL_TEXTURE_LOD_BIAS = 0x8501;
    public static final int GL_MAX_TEXTURE_LOD_BIAS = 0x84fd;
    public static final int GL_MIRRORED_REPEAT = 0x8370;
    public static final int GL_BLEND_COLOR = 0x8005;
    public static final int GL_BLEND_EQUATION = 0x8009;
    public static final int GL_FUNC_ADD = 0x8006;
    public static final int GL_FUNC_SUBTRACT = 0x800a;
    public static final int GL_FUNC_REVERSE_SUBTRACT = 0x800b;
    public static final int GL_MIN = 0x8007;
    public static final int GL_MAX = 0x8008;

    // GL15
    public static final int GL_ARRAY_BUFFER = 0x8892;
    public static final int GL_ELEMENT_ARRAY_BUFFER = 0x8893;
    public static final int GL_ARRAY_BUFFER_BINDING = 0x8894;
    public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 0x8895;
    public static final int GL_VERTEX_ARRAY_BUFFER_BINDING = 0x8896;
    public static final int GL_NORMAL_ARRAY_BUFFER_BINDING = 0x8897;
    public static final int GL_COLOR_ARRAY_BUFFER_BINDING = 0x8898;
    public static final int GL_INDEX_ARRAY_BUFFER_BINDING = 0x8899;
    public static final int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = 0x889a;
    public static final int GL_EDGE_FLAG_ARRAY_BUFFER_BINDING = 0x889b;
    public static final int GL_SECONDARY_COLOR_ARRAY_BUFFER_BINDING = 0x889c;
    public static final int GL_FOG_COORDINATE_ARRAY_BUFFER_BINDING = 0x889d;
    public static final int GL_WEIGHT_ARRAY_BUFFER_BINDING = 0x889e;
    public static final int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 0x889f;
    public static final int GL_STREAM_DRAW = 0x88e0;
    public static final int GL_STREAM_READ = 0x88e1;
    public static final int GL_STREAM_COPY = 0x88e2;
    public static final int GL_STATIC_DRAW = 0x88e4;
    public static final int GL_STATIC_READ = 0x88e5;
    public static final int GL_STATIC_COPY = 0x88e6;
    public static final int GL_DYNAMIC_DRAW = 0x88e8;
    public static final int GL_DYNAMIC_READ = 0x88e9;
    public static final int GL_DYNAMIC_COPY = 0x88ea;
    public static final int GL_READ_ONLY = 0x88b8;
    public static final int GL_WRITE_ONLY = 0x88b9;
    public static final int GL_READ_WRITE = 0x88ba;
    public static final int GL_BUFFER_SIZE = 0x8764;
    public static final int GL_BUFFER_USAGE = 0x8765;
    public static final int GL_BUFFER_ACCESS = 0x88bb;
    public static final int GL_BUFFER_MAPPED = 0x88bc;
    public static final int GL_BUFFER_MAP_POINTER = 0x88bd;
    public static final int FOG_COORD_SRC = 0x8450;
    public static final int GL_FOG_COORD = 0x8451;
    public static final int GL_CURRENT_FOG_COORD = 0x8453;
    public static final int GL_FOG_COORD_ARRAY_TYPE = 0x8454;
    public static final int GL_FOG_COORD_ARRAY_STRIDE = 0x8455;
    public static final int GL_FOG_COORD_ARRAY_POINTER = 0x8456;
    public static final int GL_FOG_COORD_ARRAY = 0x8457;
    public static final int GL_FOG_COORD_ARRAY_BUFFER_BINDING = 0x889d;
    public static final int GL_SRC0_RGB = 0x8580;
    public static final int GL_SRC1_RGB = 0x8581;
    public static final int GL_SRC2_RGB = 0x8582;
    public static final int GL_SRC0_ALPHA = 0x8588;
    public static final int GL_SRC1_ALPHA = 0x8589;
    public static final int GL_SRC2_ALPHA = 0x858a;

    public static void glPushAttrib()
    {
        // GL_ENABLE_BIT | GL_LIGHTING_BIT
        GlStateManager.pushAttrib();
    }

    public static void glPushAttrib(int mask)
    {
        GL11.glPushAttrib(mask);
    }

    public static void glPopAttrib()
    {
        GlStateManager.popAttrib();
    }

    public static void glDisableAlphaTest()
    {
        GlStateManager.disableAlpha();
    }

    public static void glEnableAlphaTest()
    {
        GlStateManager.enableAlpha();
    }

    public static void glAlphaFunc(int func, float ref)
    {
        GlStateManager.alphaFunc(func, ref);
    }

    public static void glEnableLighting()
    {
        GlStateManager.enableLighting();
    }

    public static void glDisableLighting()
    {
        GlStateManager.disableLighting();
    }

    public static void glEnableLight(int light)
    {
        GlStateManager.enableLight(light); // TODO OBF MCPTEST enableBooleanStateAt - enableLight
    }

    public static void glDisableLight(int light)
    {
        GlStateManager.disableLight(light); // TODO OBF MCPTEST disableBooleanStateAt - disableLight
    }

    public static void glLight(int light, int pname, FloatBuffer params)
    {
        GL11.glLight(light, pname, params);
    }

    public static void glLightModel(int pname, FloatBuffer params)
    {
        GL11.glLightModel(pname, params);
    }

    public static void glLightModeli(int pname, int param)
    {
        GL11.glLightModeli(pname, param);
    }

    public static void glEnableColorMaterial()
    {
        GlStateManager.enableColorMaterial();
    }

    public static void glDisableColorMaterial()
    {
        GlStateManager.disableColorMaterial();
    }

    public static void glColorMaterial(int face, int mode)
    {
        GlStateManager.colorMaterial(face, mode);
    }

    public static void glDisableDepthTest()
    {
        GlStateManager.disableDepth();
    }

    public static void glEnableDepthTest()
    {
        GlStateManager.enableDepth();
    }

    public static void glDepthFunc(int func)
    {
        GlStateManager.depthFunc(func);
    }

    public static void glDepthMask(boolean flag)
    {
        GlStateManager.depthMask(flag);
    }

    public static void glDisableBlend()
    {
        GlStateManager.disableBlend();
    }

    public static void glEnableBlend()
    {
        GlStateManager.enableBlend();
    }

    public static void glBlendFunc(int sfactor, int dfactor)
    {
        GlStateManager.blendFunc(sfactor, dfactor);
    }

    public static void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha)
    {
        GlStateManager.tryBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
    }

    public static void glEnableFog()
    {
        GlStateManager.enableFog();
    }

    public static void glDisableFog()
    {
        GlStateManager.disableFog();
    }

    public static void glSetFogMode(int mode)
    {
        GlStateManager.setFog(mode);
    }

    public static void glSetFogDensity(float density)
    {
        GlStateManager.setFogDensity(density);
    }

    public static void glSetFogStart(float start)
    {
        GlStateManager.setFogStart(start);
    }

    public static void glSetFogEnd(float end)
    {
        GlStateManager.setFogEnd(end);
    }

    public static void glSetFogColour(FloatBuffer colour)
    {
        GL11.glFog(GL_FOG_COLOR, colour);
    }

    public static void glFogi(int pname, int param)
    {
        GL11.glFogi(pname, param);
    }

    public static void glFogf(int pname, float param)
    {
        GL11.glFogf(pname, param);
    }

    public static void glEnableCulling()
    {
        GlStateManager.enableCull();
    }

    public static void glDisableCulling()
    {
        GlStateManager.disableCull();
    }

    public static void glCullFace(int mode)
    {
        GlStateManager.cullFace(mode);
    }

    public static void glEnablePolygonOffset()
    {
        GlStateManager.enablePolygonOffset();
    }

    public static void glDisablePolygonOffset()
    {
        GlStateManager.disablePolygonOffset();
    }

    public static void glPolygonOffset(float factor, float units)
    {
        GlStateManager.doPolygonOffset(factor, units);
    }

    public static void glEnableColorLogic()
    {
        GlStateManager.enableColorLogic();
    }

    public static void glDisableColorLogic()
    {
        GlStateManager.disableColorLogic();
    }

    public static void glLogicOp(int opcode)
    {
        GlStateManager.colorLogicOp(opcode);
    }

    public static void glEnableTexGenCoord(TexGen tex)
    {
        GlStateManager.enableTexGenCoord(tex);
    }

    public static void glDisableTexGenCoord(TexGen tex)
    {
        GlStateManager.disableTexGenCoord(tex);
    }

    public static void glTexGeni(TexGen tex, int mode)
    {
        GlStateManager.texGen(tex, mode);
    }

    public static void glTexGen(TexGen tex, int pname, FloatBuffer params)
    {
        GlStateManager.func_179105_a(tex, pname, params);
    }

    public static void glSetActiveTextureUnit(int texture)
    {
        GlStateManager.setActiveTexture(texture);
    }

    public static void glEnableTexture2D()
    {
        GlStateManager.enableTexture2D(); // TODO OBF MCPTEST func_179098_w - enableTexture2D
    }

    public static void glDisableTexture2D()
    {
        GlStateManager.disableTexture2D(); // TODO OBF MCPTEST func_179090_x - disableTexture2D
    }

    public static int glGenTextures()
    {
        return GlStateManager.generateTexture(); // TODO OBF MCPTEST func_179146_y - generateTexture
    }

    public static void glDeleteTextures(int textureName)
    {
        GlStateManager.deleteTexture(textureName); // TODO OBF MCPTEST func_179150_h - deleteTexture
    }

    public static void glBindTexture2D(int textureName)
    {
        GlStateManager.bindTexture(textureName); // TODO OBF MCPTEST func_179144_i - bindTexture
    }

    public static void glEnableNormalize()
    {
        GlStateManager.enableNormalize();
    }

    public static void glDisableNormalize()
    {
        GlStateManager.disableNormalize();
    }

    public static void glShadeModel(int mode)
    {
        GlStateManager.shadeModel(mode);
    }

    public static void glEnableRescaleNormal()
    {
        GlStateManager.enableRescaleNormal();
    }

    public static void glDisableRescaleNormal()
    {
        GlStateManager.disableRescaleNormal();
    }

    public static void glViewport(int x, int y, int width, int height)
    {
        GlStateManager.viewport(x, y, width, height);
    }

    public static void glColorMask(boolean red, boolean green, boolean blue, boolean alpha)
    {
        GlStateManager.colorMask(red, green, blue, alpha);
    }

    public static void glClearDepth(double depth)
    {
        GlStateManager.clearDepth(depth);
    }

    public static void glClearColor(float red, float green, float blue, float alpha)
    {
        GlStateManager.clearColor(red, green, blue, alpha);
    }

    public static void glClear(int mask)
    {
        GlStateManager.clear(mask);
    }

    public static void glMatrixMode(int mode)
    {
        GlStateManager.matrixMode(mode);
    }

    public static void glLoadIdentity()
    {
        GlStateManager.loadIdentity();
    }

    public static void glPushMatrix()
    {
        GlStateManager.pushMatrix();
    }

    public static void glPopMatrix()
    {
        GlStateManager.popMatrix();
    }

    public static void glGetFloat(int pname, FloatBuffer params)
    {
        GlStateManager.getFloat(pname, params);
    }

    public static float glGetFloat(int pname)
    {
        return GL11.glGetFloat(pname);
    }

    public static void glGetDouble(int pname, DoubleBuffer params)
    {
        GL11.glGetDouble(pname, params);
    }

    public static double glGetDouble(int pname)
    {
        return GL11.glGetDouble(pname);
    }

    public static void glGetInteger(int pname, IntBuffer params)
    {
        GL11.glGetInteger(pname, params);
    }

    public static int glGetInteger(int pname)
    {
        return GL11.glGetInteger(pname);
    }

    public static void glGetBoolean(int pname, ByteBuffer params)
    {
        GL11.glGetBoolean(pname, params);
    }

    public static boolean glGetBoolean(int pname)
    {
        return GL11.glGetBoolean(pname);
    }

    public static void gluProject(float objx, float objy, float objz, FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport,
            FloatBuffer winPos)
    {
        GLU.gluProject(objx, objy, objz, modelMatrix, projMatrix, viewport, winPos);
    }

    public static void gluPerspective(float fovy, float aspect, float zNear, float zFar)
    {
        GLU.gluPerspective(fovy, aspect, zNear, zFar);
    }

    public static void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar)
    {
        GlStateManager.ortho(left, right, bottom, top, zNear, zFar);
    }

    public static void glRotatef(float angle, float x, float y, float z)
    {
        GlStateManager.rotate(angle, x, y, z);
    }

    public static void glRotated(double angle, double x, double y, double z)
    {
        GL11.glRotated(angle, x, y, z);
    }

    public static void glScalef(float x, float y, float z)
    {
        GlStateManager.scale(x, y, z);
    }

    public static void glScaled(double x, double y, double z)
    {
        GlStateManager.scale(x, y, z);
    }

    public static void glTranslatef(float x, float y, float z)
    {
        GlStateManager.translate(x, y, z);
    }

    public static void glTranslated(double x, double y, double z)
    {
        GlStateManager.translate(x, y, z);
    }

    public static void glMultMatrix(FloatBuffer m)
    {
        GlStateManager.multMatrix(m);
    }

    public static void glColor4f(float red, float green, float blue, float alpha)
    {
        GlStateManager.color(red, green, blue, alpha);
    }

    public static void glColor3f(float red, float green, float blue)
    {
        GlStateManager.color(red, green, blue, 1.0F);
    }

    public static void glResetColor()
    {
        GlStateManager.resetColor();
    }

    public static void glCallList(int list)
    {
        GlStateManager.callList(list);
    }

    public static void glCallLists(IntBuffer lists)
    {
        GL11.glCallLists(lists);
    }

    public static void glNewList(int list, int mode)
    {
        GL11.glNewList(list, mode);
    }

    public static void glEndList()
    {
        GL11.glEndList();
    }

    public static void glLineWidth(float width)
    {
        GL11.glLineWidth(width);
    }

    public static void glPolygonMode(int face, int mode)
    {
        GL11.glPolygonMode(face, mode);
    }

    public static void glPixelStorei(int pname, int param)
    {
        GL11.glPixelStorei(pname, param);
    }

    public static void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels)
    {
        GL11.glReadPixels(x, y, width, height, format, type, pixels);
    }

    public static void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels)
    {
        GL11.glGetTexImage(target, level, format, type, pixels);
    }
}
