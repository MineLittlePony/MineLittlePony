package com.voxelmodpack.voxelmenu;

import net.minecraft.client.Minecraft;

public interface IPanoramaRenderer {
   void setPanoramaResolution(Minecraft var1, int var2, int var3);

   void initPanoramaRenderer();

   void updatePanorama();

   boolean renderPanorama(int var1, int var2, float var3);

   int getUpdateCounter();
}
