package com.voxelmodpack.hdskins.mod;

import com.voxelmodpack.common.VoxelCommonLiteMod;

public class LiteModHDSkins extends VoxelCommonLiteMod {
   public LiteModHDSkins() {
        super("com.voxelmodpack.hdskins.mod.HDSkinsModCore");
   }

   @Override
public String getVersion() {
      return "4.0.1";
   }

   @Override
public String getName() {
      return "HDSkins";
   }
}
