package com.voxelmodpack.common.properties;

import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProvider;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderFloat;

/**
 * Adapted from xTiming's text field code
 * 
 * @author Adam Mummery-Smith
 */
public class VoxelPropertyFloatField extends VoxelPropertyAbstractTextField<IVoxelPropertyProviderFloat> {
    private float minFieldValue = 0.1F;

    private float maxFieldValue = 10.0F;

    public VoxelPropertyFloatField(IVoxelPropertyProvider propertyProvider, String binding, String text, int xPos,
            int yPos, int fieldOffset) {
        super(propertyProvider, binding, text, xPos, yPos, fieldOffset);
        this.allowedCharacters = "0123456789.";
    }

    @Override
    protected void onLostFocus() {
        if (this.fieldValue.length() == 0)
            this.fieldValue = this.propertyProvider.getDefaultPropertyValue(this.propertyBinding);
        if (Float.valueOf(this.fieldValue) < this.minFieldValue)
            this.fieldValue = String.valueOf(this.minFieldValue);
        this.propertyProvider.setProperty(this.propertyBinding, Float.parseFloat(this.fieldValue));
        this.fieldValue = this.propertyProvider.getStringProperty(this.propertyBinding);
        this.focused = false;
    }

    /**
     * @return
     */
    @Override
    protected boolean checkInvalidValue() {
        return this.fieldValue.length() > 0 && Float.valueOf(this.fieldValue) > this.maxFieldValue;
    }
}
