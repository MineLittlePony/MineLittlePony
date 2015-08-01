package com.voxelmodpack.common.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.mumfrey.liteloader.util.render.Icon;
import com.voxelmodpack.common.gui.interfaces.IListObject;

/**
 * Information about an enumerated item, for display in the item list
 * 
 * @author Adam Mummery-Smith
 */
public class ItemStackInfo implements IListObject {
    private int index;

    private Icon icon;

    /**
     * Item name
     */
    private String name, nameWithID;

    private Item item;

    private String itemId;

    private int damageValue;

    private ItemStack stack;

    /**
     * Create a new item info
     * 
     * @param id Item ID
     * @param texture Item texture
     * @param icon Item's icon index
     */
    public ItemStackInfo(int index, Icon icon, int damage, ItemStack stack) {
        this.index = index;
        this.name = stack.getDisplayName();
        this.icon = icon;
        this.damageValue = damage;
        this.stack = stack;
    }

    /**
     * 
     */
    public void updateName() {
        this.name = this.stack.getDisplayName();
        this.item = this.stack.getItem();
        this.itemId = ItemStackInfo.getItemIdentifier(this.item);
        this.nameWithID = this.name + " " + EnumChatFormatting.LIGHT_PURPLE + this.itemId;

        if (this.damageValue > 0)
            this.nameWithID += ":" + this.damageValue;
    }

    /**
     * True if the item has an icon that can be displayed in list boxes
     */
    @Override
    public boolean hasIcon() {
        return true;
    }

    @Override
    public Icon getIcon() {
        return this.icon;
    }

    public String getName(boolean withID) {
        return withID ? this.nameWithID : this.name;
    }

    public int getDamage() {
        return this.damageValue;
    }

    public Item getItem() {
        return this.item;
    }

    public String getItemId() {
        return this.itemId;
    }

    public ItemStack getItemStack() {
        return this.stack;
    }

    public boolean compareTo(ItemStackInfo other) {
        return (other.name.equalsIgnoreCase(this.name) && other.icon == this.icon);
    }

    /**
     * Get the display text to show in listboxes
     * 
     * @return display text
     */
    @Override
    public String getText() {
        return this.name;
    }

    /**
     * Get the display text to show in listboxes
     * 
     * @return display text
     */
    public String getSortText() {
        String sortText = this.name;

        if (this.stack.getItem() instanceof ItemFood)
            sortText += " food";
        if (this.name.toLowerCase().contains("wood"))
            sortText += " wood";

        return sortText;
    }

    /**
     * Get the item's ID
     */
    @Override
    public int getID() {
        return this.index;
    }

    /**
     * Get the data associated with the item
     */
    @Override
    public Object getData() {
        return null;
    }

    /**
     * Return the custom draw behaviour for the item
     */
    @Override
    public CustomDrawBehaviour getCustomDrawBehaviour() {
        return CustomDrawBehaviour.NoCustomDraw;
    }

    /**
     * Handles custom drawing, this object does not support custom draw
     * behaviour
     */
    @Override
    public void drawCustom(boolean iconEnabled, Minecraft minecraft, int mouseX, int mouseY, int xPosition,
            int yPosition, int width, int height, int updateCounter) {}

    /**
     * Handle mouse pressed, this object does not support this event
     */
    @Override
    public boolean mousePressed(boolean iconEnabled, Minecraft minecraft, int mouseX, int mouseY, int xPosition,
            int yPosition, int width, int height) {
        return false;
    }

    /**
     * Handle mouse released.
     */
    @Override
    public void mouseReleased(int mouseX, int mouseY) {}

    /**
     * Get custom action. Not supported by this object, always returns an empty
     * string.
     */
    @Override
    public String getCustomAction(boolean bClear) {
        return "";
    }

    /**
     * Get whether this object is draggable or not
     */
    @Override
    public boolean getDraggable() {
        return false;
    }

    @Override
    public void setIconTexture(String newTexture) {}

    @Override
    public void setIconID(int newIconId) {}

    @Override
    public void setText(String newText) {
        this.name = newText;

    }

    @Override
    public void setID(int newId) {}

    @Override
    public void setData(Object newData) {}

    @Override
    public boolean getCanEditInPlace() {
        return false;
    }

    @Override
    public boolean getEditingInPlace() {
        return false;
    }

    @Override
    public void beginEditInPlace() {}

    @Override
    public void endEditInPlace() {}

    @Override
    public boolean editInPlaceKeyTyped(char keyChar, int keyCode) {
        return false;
    }

    @Override
    public void editInPlaceDraw(boolean iconEnabled, Minecraft minecraft, int mouseX, int mouseY, int xPosition,
            int yPosition, int width, int height, int updateCounter) {}

    @Override
    public boolean editInPlaceMousePressed(boolean iconEnabled, Minecraft minecraft, int mouseX, int mouseY,
            int xPosition, int yPosition, int width, int height) {
        return false;
    }

    @Override
    public String serialise() {
        return null;
    }

    @Override
    public void bindIconTexture() {}

    @Override
    public int getIconSize() {
        return 16;
    }

    @Override
    public int getIconTexmapSize() {
        return 256;
    }

    @Override
    public String getDisplayName() {
        return this.name.toLowerCase();
    }

    @Override
    public void setDisplayName(String newDisplayName) {}

    public static String getItemIdentifier(Item item) {
        String itemName = (String) Item.itemRegistry.getNameForObject(item);
        return itemName == null ? "air" : ItemStackInfo.stripNamespace(itemName);
    }

    public static String getBlockIdentifier(Block block) {
        String blockName = (String) Block.blockRegistry.getNameForObject(block);
        return blockName == null ? "air" : ItemStackInfo.stripNamespace(blockName);
    }

    /**
     * @param itemName
     * @return
     */
    private static String stripNamespace(String itemName) {
        return itemName.startsWith("minecraft:") ? itemName.substring(10) : itemName;
    }
}
