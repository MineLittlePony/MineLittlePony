package com.voxelmodpack.common.properties;

import com.voxelmodpack.common.gui.interfaces.IExtendedGui;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;

/**
 * Label
 * 
 * @author Adam Mummery-Smith
 */
public class VoxelPropertyLabel extends VoxelProperty<IVoxelPropertyProvider> {
    private int colour = 0x99CCFF;

    public VoxelPropertyLabel(String displayText, int xPos, int yPos) {
        this(displayText, xPos, yPos, 0x99CCFF);
    }

    public VoxelPropertyLabel(String displayText, int xPos, int yPos, int colour) {
        super(null, null, displayText, xPos, yPos);
        this.colour = colour;
    }

    @Override
    public void draw(IExtendedGui host, int mouseX, int mouseY) {
        this.drawString(this.mc.fontRendererObj, this.displayText, this.xPosition, this.yPosition, this.colour);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY) {}

    @Override
    public void keyTyped(char keyChar, int keyCode) {}
}
