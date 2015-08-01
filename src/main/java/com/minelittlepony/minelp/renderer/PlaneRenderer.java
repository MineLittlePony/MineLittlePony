package com.minelittlepony.minelp.renderer;

import static net.minecraft.client.renderer.GlStateManager.*;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public class PlaneRenderer {
    public float textureWidth;
    public float textureHeight;
    private PositionTextureVertex[] corners;
    private TexturedQuad[] faces;
    private int textureOffsetX;
    private int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public float field_35977_i;
    public float field_35975_j;
    public float field_35976_k;
    public float field_35973_l;
    public float field_35974_m;
    public float field_35972_n;
    private boolean compiled = false;
    private int displayList = 0;
    public boolean mirror = false;
    public boolean mirrory = false;
    public boolean mirrorxy = false;
    public boolean showModel = true;
    public boolean isHidden = false;

    public PlaneRenderer(ModelBase modelbase, int i, int j) {
        this.textureOffsetX = i;
        this.textureOffsetY = j;
        this.textureWidth = modelbase.textureWidth;
        this.textureHeight = modelbase.textureHeight;
    }

    public void addBackPlane(float f, float f1, float f2, int i, int j, int k) {
        this.addBackPlane(f, f1, f2, i, j, k, 0.0F);
    }

    public void addSidePlane(float f, float f1, float f2, int i, int j, int k) {
        this.addSidePlane(f, f1, f2, i, j, k, 0.0F);
    }

    public void addTopPlane(float f, float f1, float f2, int i, int j, int k) {
        this.addTopPlane(f, f1, f2, i, j, k, 0.0F);
    }

    public void addBottomPlane(float f, float f1, float f2, int i, int j, int k) {
        this.addBottomPlane(f, f1, f2, i, j, k, 0.0F);
    }

    public void addBackPlane(float f, float f1, float f2, int i, int j, int k, float f3) {
        this.field_35977_i = f;
        this.field_35975_j = f1;
        this.field_35976_k = f2;
        this.field_35973_l = f + i;
        this.field_35974_m = f1 + j;
        this.field_35972_n = f2 + k;
        this.corners = new PositionTextureVertex[8];
        this.faces = new TexturedQuad[1];
        float f4 = f + i;
        float f5 = f1 + j;
        float f6 = f2 + k;
        f -= f3;
        f1 -= f3;
        f2 -= f3;
        f4 += f3;
        f5 += f3;
        f6 += f3;
        if (this.mirror) {
            float positiontexturevertex = f4;
            f4 = f;
            f = positiontexturevertex;
        }

        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, f1, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, f1, f2, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(f4, f5, f2, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(f, f5, f2, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, f1, f6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, f1, f6, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(f4, f5, f6, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(f, f5, f6, 8.0F, 0.0F);
        this.corners[0] = positiontexturevertex;
        this.corners[1] = positiontexturevertex1;
        this.corners[2] = positiontexturevertex2;
        this.corners[3] = positiontexturevertex3;
        this.corners[4] = positiontexturevertex4;
        this.corners[5] = positiontexturevertex5;
        this.corners[6] = positiontexturevertex6;
        this.corners[7] = positiontexturevertex7;
        this.faces[0] = new TexturedQuad(
                new PositionTextureVertex[] {
                        positiontexturevertex1,
                        positiontexturevertex,
                        positiontexturevertex3,
                        positiontexturevertex2 },
                this.textureOffsetX, this.textureOffsetY,
                this.textureOffsetX + i, this.textureOffsetY + j,
                this.textureWidth, this.textureHeight);
        if (this.mirror) {
            this.faces[0].flipFace();
        }

    }

    public void addSidePlane(float f, float f1, float f2, int i, int j, int k, float f3) {
        this.field_35977_i = f;
        this.field_35975_j = f1;
        this.field_35976_k = f2;
        this.field_35973_l = f + i;
        this.field_35974_m = f1 + j;
        this.field_35972_n = f2 + k;
        this.corners = new PositionTextureVertex[8];
        this.faces = new TexturedQuad[1];
        float f4 = f + i;
        float f5 = f1 + j;
        float f6 = f2 + k;
        f -= f3;
        f1 -= f3;
        f2 -= f3;
        f4 += f3;
        f5 += f3;
        f6 += f3;
        if (this.mirror) {
            float positiontexturevertex = f4;
            f4 = f;
            f = positiontexturevertex;
        }

        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, f1, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, f1, f2, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(f4, f5, f2, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(f, f5, f2, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, f1, f6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, f1, f6, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(f4, f5, f6, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(f, f5, f6, 8.0F, 0.0F);
        this.corners[0] = positiontexturevertex;
        this.corners[1] = positiontexturevertex1;
        this.corners[2] = positiontexturevertex2;
        this.corners[3] = positiontexturevertex3;
        this.corners[4] = positiontexturevertex4;
        this.corners[5] = positiontexturevertex5;
        this.corners[6] = positiontexturevertex6;
        this.corners[7] = positiontexturevertex7;
        this.faces[0] = new TexturedQuad(
                new PositionTextureVertex[] {
                        positiontexturevertex5,
                        positiontexturevertex1,
                        positiontexturevertex2,
                        positiontexturevertex6 },
                this.textureOffsetX, this.textureOffsetY,
                this.textureOffsetX + k, this.textureOffsetY + j,
                this.textureWidth, this.textureHeight);
        if (this.mirror) {
            this.faces[0].flipFace();
        }

    }

    public void addTopPlane(float f, float f1, float f2, int i, int j, int k, float f3) {
        this.field_35977_i = f;
        this.field_35975_j = f1;
        this.field_35976_k = f2;
        this.field_35973_l = f + i;
        this.field_35974_m = f1 + j;
        this.field_35972_n = f2 + k;
        this.corners = new PositionTextureVertex[8];
        this.faces = new TexturedQuad[1];
        float f4 = f + i;
        float f5 = f1 + j;
        float f6 = f2 + k;
        f -= f3;
        f1 -= f3;
        f2 -= f3;
        f4 += f3;
        f5 += f3;
        f6 += f3;
        float vertex;
        if (this.mirror) {
            vertex = f4;
            f4 = f;
            f = vertex;
        }

        if (this.mirrory) {
            vertex = f6;
            f6 = f2;
            f2 = vertex;
        }

        if (this.mirrorxy) {
            vertex = f6;
            f6 = f2;
            f2 = vertex;
            vertex = f4;
            f4 = f;
            f = vertex;
        }

        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, f1, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, f1, f2, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(f4, f5, f2, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(f, f5, f2, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, f1, f6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, f1, f6, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(f4, f5, f6, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(f, f5, f6, 8.0F, 0.0F);
        this.corners[0] = positiontexturevertex;
        this.corners[1] = positiontexturevertex1;
        this.corners[2] = positiontexturevertex2;
        this.corners[3] = positiontexturevertex3;
        this.corners[4] = positiontexturevertex4;
        this.corners[5] = positiontexturevertex5;
        this.corners[6] = positiontexturevertex6;
        this.corners[7] = positiontexturevertex7;
        this.faces[0] = new TexturedQuad(
                new PositionTextureVertex[] {
                        positiontexturevertex5,
                        positiontexturevertex4,
                        positiontexturevertex,
                        positiontexturevertex1 },
                this.textureOffsetX, this.textureOffsetY,
                this.textureOffsetX + i, this.textureOffsetY + k,
                this.textureWidth, this.textureHeight);
        if (this.mirror || this.mirrory) {
            this.faces[0].flipFace();
        }

    }

    public void addBottomPlane(float f, float f1, float f2, int i, int j, int k, float f3) {
        this.field_35977_i = f;
        this.field_35975_j = f1;
        this.field_35976_k = f2;
        this.field_35973_l = f + i;
        this.field_35974_m = f1 + j;
        this.field_35972_n = f2 + k;
        this.corners = new PositionTextureVertex[8];
        this.faces = new TexturedQuad[1];
        float f4 = f + i;
        float f5 = f1 + j;
        float f6 = f2 + k;
        f -= f3;
        f1 -= f3;
        f2 -= f3;
        f4 += f3;
        f5 += f3;
        f6 += f3;
        float vertex;
        if (this.mirror) {
            vertex = f4;
            f4 = f;
            f = vertex;
        }

        if (this.mirrory) {
            vertex = f6;
            f6 = f2;
            f2 = vertex;
        }

        if (this.mirrorxy) {
            vertex = f6;
            f6 = f2;
            f2 = vertex;
            vertex = f4;
            f4 = f;
            f = vertex;
        }

        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, f1, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, f1, f2, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(f4, f5, f2, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(f, f5, f2, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, f1, f6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, f1, f6, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(f4, f5, f6, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(f, f5, f6, 8.0F, 0.0F);
        this.corners[0] = positiontexturevertex;
        this.corners[1] = positiontexturevertex1;
        this.corners[2] = positiontexturevertex2;
        this.corners[3] = positiontexturevertex3;
        this.corners[4] = positiontexturevertex4;
        this.corners[5] = positiontexturevertex5;
        this.corners[6] = positiontexturevertex6;
        this.corners[7] = positiontexturevertex7;
        this.faces[0] = new TexturedQuad(
                new PositionTextureVertex[] {
                        positiontexturevertex2,
                        positiontexturevertex3,
                        positiontexturevertex7,
                        positiontexturevertex6 },
                this.textureOffsetX, this.textureOffsetY,
                this.textureOffsetX + i, this.textureOffsetY + k,
                this.textureWidth, this.textureHeight);
        if (this.mirror || this.mirrory) {
            this.faces[0].flipFace();
        }

    }

    public void setRotationPoint(float f, float f1, float f2) {
        this.rotationPointX = f;
        this.rotationPointY = f1;
        this.rotationPointZ = f2;
    }

    public void render(float f) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(f);
                }

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                    if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                        callList(this.displayList);
                    } else {
                        translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                        callList(this.displayList);
                        translate(-this.rotationPointX * f, -this.rotationPointY * f, -this.rotationPointZ * f);
                    }
                } else {
                    pushMatrix();
                    translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                    if (this.rotateAngleZ != 0.0F) {
                        rotate(this.rotateAngleZ * 57.29578F, 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F) {
                        rotate(this.rotateAngleY * 57.29578F, 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F) {
                        rotate(this.rotateAngleX * 57.29578F, 1.0F, 0.0F, 0.0F);
                    }

                    callList(this.displayList);
                    popMatrix();
                }

            }
        }
    }

    public void renderWithRotation(float f) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(f);
                }

                pushMatrix();
                translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                if (this.rotateAngleY != 0.0F) {
                    rotate(this.rotateAngleY * 57.29578F, 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    rotate(this.rotateAngleX * 57.29578F, 1.0F, 0.0F, 0.0F);
                }

                if (this.rotateAngleZ != 0.0F) {
                    rotate(this.rotateAngleZ * 57.29578F, 0.0F, 0.0F, 1.0F);
                }

                callList(this.displayList);
                popMatrix();
            }
        }
    }

    public void postRender(float f) {
        if (!this.isHidden) {
            if (this.showModel) {
                if (!this.compiled) {
                    this.compileDisplayList(f);
                }

                if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                    if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                        translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                    }
                } else {
                    translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                    if (this.rotateAngleZ != 0.0F) {
                        rotate(this.rotateAngleZ * 57.29578F, 0.0F, 0.0F, 1.0F);
                    }

                    if (this.rotateAngleY != 0.0F) {
                        rotate(this.rotateAngleY * 57.29578F, 0.0F, 1.0F, 0.0F);
                    }

                    if (this.rotateAngleX != 0.0F) {
                        rotate(this.rotateAngleX * 57.29578F, 1.0F, 0.0F, 0.0F);
                    }
                }

            }
        }
    }

    private void compileDisplayList(float f) {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, 4864);
        WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();

        for (TexturedQuad face : this.faces) {
            face.draw(wr, f);
        }

        GL11.glEndList();
        this.compiled = true;
    }

    public PlaneRenderer setTextureSize(int i, int j) {
        this.textureWidth = i;
        this.textureHeight = j;
        return this;
    }

    public void setToModel(ModelRenderer modelrenderer) {
        this.rotationPointX = modelrenderer.rotationPointX;
        this.rotationPointY = modelrenderer.rotationPointY;
        this.rotationPointZ = modelrenderer.rotationPointZ;
        this.rotateAngleX = modelrenderer.rotateAngleX;
        this.rotateAngleY = modelrenderer.rotateAngleY;
        this.rotateAngleZ = modelrenderer.rotateAngleZ;
    }
}
