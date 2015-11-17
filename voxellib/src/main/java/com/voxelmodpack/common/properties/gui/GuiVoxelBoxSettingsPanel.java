package com.voxelmodpack.common.properties.gui;

import static com.mumfrey.liteloader.gl.GL.*;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import com.voxelmodpack.common.LiteModVoxelCommon;
import com.voxelmodpack.common.gui.GuiScreenEx;
import com.voxelmodpack.common.properties.ModConfig;
import com.voxelmodpack.common.properties.VoxelProperty;

/**
 * Base class for voxelmod gui config screens
 * 
 * @author Adam Mummery-Smith
 */
public abstract class GuiVoxelBoxSettingsPanel extends GuiScreenEx implements ConfigPanel {
    /**
     * Mod config
     */
    protected ModConfig config;

    /**
     * Property widgets to display
     */
    protected ArrayList<VoxelProperty<?>> properties = new ArrayList<VoxelProperty<?>>();

    /**
     * Shared panel width, calculated from the width of the tabs
     */
    protected static int PANEL_WIDTH = 330;

    /**
     * Panel left edge position
     */
    protected static int PANEL_LEFT = 97;

    /**
     * Panel top edge position
     */
    protected static int PANEL_TOP = 0;

    /**
     * 
     */
    protected static int PANEL_HEIGHT = 220;

    /**
     * Pixel spacing between tabs
     */
    protected static int TAB_SPACING = 2;

    protected boolean overCloseButton = false;

    protected SettingsPanelManager panelManager = SettingsPanelManager.getInstance();

    protected boolean isPanel = false;

    protected int contentHeight = 240;

    public ModConfig getConfig() {
        return this.config;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.setTexMapSize(256);

        glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        glEnableBlend();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int right = PANEL_LEFT + PANEL_WIDTH;
        int bottom = PANEL_TOP + PANEL_HEIGHT;

        this.zLevel = -50;
        this.drawTabs(mouseX, mouseY, partialTicks, true);
        this.drawPanel(right, bottom);
        this.drawTabs(mouseX, mouseY, partialTicks, false);
        this.zLevel = 0;

        this.drawCloseButton(mouseX, mouseY, right - 4, PANEL_TOP + 20);
        for (VoxelProperty<?> property : this.properties) {
            if (property.isVisible()) {
                property.draw(this, mouseX, mouseY);
            }
        }
    }

    /**
     * @param right
     * @param bottom
     */
    protected void drawPanel(int right, int bottom) {
        PANEL_HEIGHT = Math.max(220, this.height - PANEL_TOP - 2);

        this.zLevel = -100;
        glEnableDepthTest();

        this.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256, PANEL_LEFT, PANEL_TOP, right, bottom, 0,
                16, 16, 32, 4);

        this.zDrop();
        this.drawDepthRect(PANEL_LEFT + 1, PANEL_TOP + 1, right - 1, bottom - 1, 0x80000000);

        glDisableDepthTest();
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    protected void drawTabs(int mouseX, int mouseY, float partialTicks, boolean mask) {
        this.zDrop();

        this.panelManager.renderTabs(this, mouseX, mouseY, partialTicks, PANEL_LEFT, PANEL_TOP, TAB_SPACING, mask);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (this.overCloseButton) // mouseIn(mouseX, mouseY, 315, 5, 330, 20))
        {
            this.onClosed();
            this.mc.displayGuiScreen(null);
            return;
        }

        if (!this.panelManager.mouseClicked(this, mouseX, mouseY, button, PANEL_LEFT, PANEL_TOP, TAB_SPACING)) {
            super.mouseClicked(mouseX, mouseY, button);

            for (VoxelProperty<?> property : this.properties) {
                if (property.isVisible()) {
                    property.mouseClicked(mouseX, mouseY);
                }
            }
        }
    }

    protected void onClosed() {
        for (VoxelProperty<?> property : this.properties) {
            property.onClosed();
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        super.keyTyped(keyChar, keyCode);

        this.keyPressed(null, keyChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        for (VoxelProperty<?> property : this.properties) {
            property.updateCursorCounter();
        }
    }

    /**
     * @return
     */
    public void zDrop() {
        this.zLevel--;
    }

    protected boolean mouseIn(int mouseX, int mouseY, int x, int y, int x2, int y2) {
        return (mouseX > x && mouseX < x2 && mouseY > y && mouseY < y2);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param right
     * @param top
     */
    protected void drawCloseButton(int mouseX, int mouseY, int right, int top) {
        this.overCloseButton = this.mouseIn(mouseX, mouseY, right - 15, top - 16, right, top - 1);
        int v = this.overCloseButton ? 32 : 0;

        this.drawDepthRect(right - 1, top - 2, right - 14, top - 15, 0x80000000);
        this.drawTessellatedModalBorderRect(LiteModVoxelCommon.GUIPARTS, 256,
                this.overCloseButton ? right - 16 : right - 15, this.overCloseButton ? top - 17 : top - 16,
                this.overCloseButton ? right + 1 : right, this.overCloseButton ? top : top - 1, 0, v, 16, 16 + v, 4);
        this.drawString(this.mc.fontRendererObj, "x", right - 10, top - 13, this.overCloseButton ? 0x55FFFF : 0xAAAAAA);
    }

    @Override
    public int getContentHeight() {
        return this.contentHeight;
    }

    @Override
    public void onPanelShown(ConfigPanelHost host) {
        this.mc = Minecraft.getMinecraft();
        this.isPanel = true;
        this.initGui();
    }

    @Override
    public void onPanelResize(ConfigPanelHost host) {
        this.initGui();
    }

    @Override
    public void onPanelHidden() {}

    @Override
    public void onTick(ConfigPanelHost host) {
        this.updateScreen();
    }

    @Override
    public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks) {
        glTranslatef(-PANEL_LEFT, 0.0F, 0.0F);

        for (VoxelProperty<?> property : this.properties) {
            if (property.isVisible()) {
                property.draw(this, mouseX + PANEL_LEFT, mouseY);
            }
        }
    }

    @Override
    public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX + PANEL_LEFT, mouseY, mouseButton);
        } catch (IOException ex) {}

        for (VoxelProperty<?> property : this.properties) {
            if (property.isVisible()) {
                property.mouseClicked(mouseX + PANEL_LEFT, mouseY);
            }
        }
    }

    @Override
    public void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
        super.mouseClickMove(mouseX + PANEL_LEFT, mouseY, mouseButton, 0L);
    }

    @Override
    public void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY) {
        super.mouseClickMove(mouseX + PANEL_LEFT, mouseY, -1, 0L);
    }

    @Override
    public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && host != null) {
            host.close();
            return;
        }

        if (keyCode == Keyboard.KEY_TAB) {
            VoxelProperty<?> focused = null; // focused property
            VoxelProperty<?> next = null; // next focusable property AFTER the focused property
            VoxelProperty<?> before = null; // first focusable property BEFORE the focused property

            // Search through properties to find focus chain
            for (VoxelProperty<?> property : this.properties) {
                if (property.isFocusable() && next == null)
                    next = property;
                if (property.isFocused() && focused == null) {
                    focused = property;
                    before = next;
                    next = null;
                }
            }

            // If didn't find a focusable property after focused in the chain,
            // use the one nearest the start
            if (next == null)
                next = before;

            // If we ARE focused and have a focusable property to switch to,
            // then switch
            if (focused != null && next != null && next != focused) {
                focused.setFocused(false);
                next.setFocused(true);
                return;
            }
        }

        for (VoxelProperty<?> property : this.properties) {
            if (property.isVisible()) {
                property.keyTyped(keyChar, keyCode);
            }
        }
    }
}
