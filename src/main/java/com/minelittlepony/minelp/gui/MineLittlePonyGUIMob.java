package com.minelittlepony.minelp.gui;

import com.minelittlepony.minelp.MineLittlePony;
import com.voxelmodpack.common.properties.VoxelPropertyLabel;
import com.voxelmodpack.common.properties.gui.GuiVoxelBoxSettingsPanel;

public class MineLittlePonyGUIMob extends GuiVoxelBoxSettingsPanel {
   public MineLittlePonyGUIMob() {
        // PonyManager ponyManager = PonyManager.getInstance();
      this.config = MineLittlePony.getConfig();
      byte col1 = 30;
      this.properties.add(new VoxelPropertyLabel("If you make any changes here, you must restart", PANEL_LEFT + 15, PANEL_TOP + 11, 16737894));
      this.properties.add(new VoxelPropertyLabel("Minecraft before they will take effect!", PANEL_LEFT + 15, PANEL_TOP + 23, 16737894));
      this.properties.add(new FakeVoxelPropertyCheckBox(this.config, "villagers", "Ponify villagers", PANEL_LEFT + col1, PANEL_TOP + 42));
      this.properties.add(new FakeVoxelPropertyCheckBox(this.config, "zombies", "Ponify zombies", PANEL_LEFT + col1, PANEL_TOP + 60));
      this.properties.add(new FakeVoxelPropertyCheckBox(this.config, "pigzombies", "Ponify zombie pigmen", PANEL_LEFT + col1, PANEL_TOP + 78));
      this.properties.add(new FakeVoxelPropertyCheckBox(this.config, "skeletons", "Ponify skeletons", PANEL_LEFT + col1, PANEL_TOP + 96));
   }

    @Override
    public String getPanelTitle() {
        return "Mine Little Pony Mob Settings";
    }
}
