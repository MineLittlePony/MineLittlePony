package com.mumfrey.liteloader.client.gui;

import static com.mumfrey.liteloader.gl.GL.*;
import static com.mumfrey.liteloader.gl.GLClippingPlanes.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

/**
 * Config panel container, this handles drawing the configuration panel chrome
 * and also hosts the configuration panels themselves to support scrolling and
 * stuff.
 *
 * @author Adam Mummery-Smith
 */
class GuiPanelConfigContainer extends GuiPanel implements ConfigPanelHost
{
    /**
     * Panel we are hosting
     */
    private ConfigPanel panel;

    /**
     * Mod being configured, the panel may want a reference to it
     */
    private LiteMod mod;

    /**
     * Scroll bar for the panel
     */
    GuiSimpleScrollBar scrollBar = new GuiSimpleScrollBar();

    /**
     * Panel's internal height (for scrolling)
     */
    private int totalHeight = -1;

    /**
     * @param minecraft
     * @param panel
     * @param mod
     */
    GuiPanelConfigContainer(Minecraft minecraft, ConfigPanel panel, LiteMod mod)
    {
        super(minecraft);

        this.panel = panel;
        this.mod   = mod;
    }

    /**
     * 
     */
    String getPanelTitle()
    {
        String panelTitle = this.panel.getPanelTitle();
        return panelTitle != null ? panelTitle : I18n.format("gui.settings.title", this.mod.getName());
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.modconfig.ConfigPanelHost#getMod()
     */
    @SuppressWarnings("unchecked")
    @Override
    public <TModClass extends LiteMod> TModClass getMod()
    {
        return (TModClass)this.mod;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.modconfig.ConfigPanelHost#getWidth()
     */
    @Override
    public int getWidth()
    {
        return this.innerWidth;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.modconfig.ConfigPanelHost#getHeight()
     */
    @Override
    public int getHeight()
    {
        return this.innerHeight;
    }

    /**
     * Callback from parent screen when window is resized
     * 
     * @param width
     * @param height
     */
    @Override
    void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.panel.onPanelResize(this);
        this.controls.add(new GuiButton(0, this.width - 99 - MARGIN, this.height - BOTTOM + 9, 100, 20, I18n.format("gui.saveandclose")));
    }

    /**
     * Callback from parent screen when panel is displayed
     */
    @Override
    void onShown()
    {
        try
        {
            this.panel.onPanelShown(this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Callback from parent screen when panel is hidden
     */
    @Override
    void onHidden()
    {
        try
        {
            this.panel.onPanelHidden();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Callback from parent screen every tick
     */
    @Override
    void onTick()
    {
        this.panel.onTick(this);
    }

    /**
     * Draw the panel and chrome
     * 
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    void draw(int mouseX, int mouseY, float partialTicks)
    {
        // Scroll position
        this.innerTop = TOP - this.scrollBar.getValue();

        // Draw panel title
        this.mc.fontRendererObj.drawString(this.getPanelTitle(), MARGIN, TOP - 14, 0xFFFFFFFF);

        // Draw top and bottom horizontal bars
        drawRect(MARGIN, TOP - 4, this.width - MARGIN, TOP - 3, 0xFF999999);
        drawRect(MARGIN, this.height - BOTTOM + 2, this.width - MARGIN, this.height - BOTTOM + 3, 0xFF999999);

        // Clip rect
        glEnableClipping(MARGIN, this.width - MARGIN - 6, TOP, this.height - BOTTOM);

        // Offset by scroll
        glPushMatrix();
        glTranslatef(MARGIN, this.innerTop, 0.0F);

        // Draw panel contents
        this.panel.drawPanel(this, mouseX - MARGIN - (this.mouseOverPanel(mouseX, mouseY) ? 0 : 99999), mouseY - this.innerTop, partialTicks);
        glClear(GL_DEPTH_BUFFER_BIT);

        // Disable clip rect
        glDisableClipping();

        // Restore transform
        glPopMatrix();

        // Get total scroll height from panel
        this.totalHeight = Math.max(-1, this.panel.getContentHeight());

        // Update and draw scroll bar
        this.scrollBar.setMaxValue(this.totalHeight - this.innerHeight);
        this.scrollBar.drawScrollBar(mouseX, mouseY, partialTicks, this.width - MARGIN - 5, TOP, 5, this.innerHeight,
                Math.max(this.innerHeight, this.totalHeight));

        // Draw other buttons
        super.draw(mouseX, mouseY, partialTicks);
    }

    /**
     * @param control
     */
    @Override
    void actionPerformed(GuiButton control)
    {
        if (control.id == 0) this.close();
    }

    /**
     * @param mouseWheelDelta
     */
    @Override
    void mouseWheelScrolled(int mouseWheelDelta)
    {
        this.scrollBar.offsetValue(-mouseWheelDelta / 8);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    @Override
    void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            if (this.scrollBar.wasMouseOver())
            {
                this.scrollBar.setDragging(true);
            }
        }

        super.mousePressed(mouseX, mouseY, mouseButton);

        if (this.mouseOverPanel(mouseX, mouseY))
        {
            this.panel.mousePressed(this, mouseX - MARGIN, mouseY - this.innerTop, mouseButton);
        }
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    @Override
    void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            this.scrollBar.setDragging(false);
        }

        this.panel.mouseReleased(this, mouseX - MARGIN, mouseY - this.innerTop, mouseButton);
    }

    /**
     * @param mouseX
     * @param mouseY
     */
    @Override
    void mouseMoved(int mouseX, int mouseY)
    {
        this.panel.mouseMoved(this, mouseX - MARGIN, mouseY - this.innerTop);
    }

    /**
     * @param keyChar
     * @param keyCode
     */
    @Override
    void keyPressed(char keyChar, int keyCode)
    {
        this.panel.keyPressed(this, keyChar, keyCode);
    }
}
