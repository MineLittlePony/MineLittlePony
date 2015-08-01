package com.voxelmodpack.common.properties;

import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderInteger;

/**
 * Adapted from xTiming's text field code
 * 
 * @author Adam Mummery-Smith
 */
public class VoxelPropertyIntField extends VoxelPropertyAbstractTextField<IVoxelPropertyProviderInteger> {
    private int minFieldValue = 1;

    private int maxFieldValue = 60;

    public VoxelPropertyIntField(IVoxelPropertyProvider propertyProvider, String binding, String text, int xPos,
            int yPos, int fieldOffset) {
        super(propertyProvider, binding, text, xPos, yPos, fieldOffset);
    }

    @Override
    protected void onLostFocus() {
        if (this.fieldValue.length() == 0)
            this.fieldValue = this.propertyProvider.getDefaultPropertyValue(this.propertyBinding);
        if (Integer.valueOf(this.fieldValue) < this.minFieldValue)
            this.fieldValue = String.valueOf(this.minFieldValue);
        this.propertyProvider.setProperty(this.propertyBinding, Integer.parseInt(this.fieldValue));
        this.fieldValue = this.propertyProvider.getStringProperty(this.propertyBinding);
        this.focused = false;
    }

    /**
     * @return
     */
    @Override
    protected boolean checkInvalidValue() {
        return this.fieldValue.length() > 0 && Integer.valueOf(this.fieldValue) > this.maxFieldValue;
    }

    public int getMinFieldValue() {
        return this.minFieldValue;
    }

    public void setMinFieldValue(int minFieldValue) {
        this.minFieldValue = minFieldValue;
    }

    public int getMaxFieldValue() {
        return this.maxFieldValue;
    }

    public void setMaxFieldValue(int maxFieldValue) {
        this.maxFieldValue = maxFieldValue;
    }
}
