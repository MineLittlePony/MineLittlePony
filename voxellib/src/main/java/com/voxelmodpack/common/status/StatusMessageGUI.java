package com.voxelmodpack.common.status;

import com.voxelmodpack.common.properties.VoxelPropertyCheckBox;
import com.voxelmodpack.common.properties.VoxelPropertyLabel;
import com.voxelmodpack.common.properties.gui.GuiVoxelBoxSettingsPanel;

/**
 * @author anangrybeaver
 */
public class StatusMessageGUI extends GuiVoxelBoxSettingsPanel {
    public StatusMessageGUI() {
        this.config = StatusMessageManager.getInstance().getConfig();

        this.properties.add(new VoxelPropertyLabel("Status Settings", PANEL_LEFT + 15, PANEL_TOP + 10));
        this.properties.add(new VoxelPropertyCheckBox(this.config, "showStatuses", "Show Statuses", PANEL_LEFT + 20,
                this.getRowYPos(0)));
    }

    public int getRowYPos(int row) {
        return 26 + (row * 20);
    }

    @Override
    public String getPanelTitle() {
        return "Voxel Status Message";
    }
}