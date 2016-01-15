package com.mumfrey.liteloader.client.gui.modlist;

import static com.mumfrey.liteloader.gl.GL.*;
import static com.mumfrey.liteloader.gl.GLClippingPlanes.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import com.mumfrey.liteloader.api.ModInfoDecorator;
import com.mumfrey.liteloader.client.gui.GuiLiteLoaderPanel;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.util.render.IconClickable;
import com.mumfrey.liteloader.util.render.IconTextured;

public class GuiModListPanel extends Gui
{
    static final int BLACK                     = 0xFF000000;
    static final int DARK_GREY                 = 0xB0333333;
    static final int GREY                      = 0xFF999999;
    static final int WHITE                     = 0xFFFFFFFF;

    static final int BLEND_2THRDS              = 0xB0FFFFFF;
    static final int BLEND_HALF                = 0x80FFFFFF;

    static final int API_COLOUR                = 0xFFAA00AA;
    static final int EXTERNAL_ENTRY_COLOUR     = 0xFF47D1AA;
    static final int MISSING_DEPENDENCY_COLOUR = 0xFFFFAA00;
    static final int ERROR_COLOUR              = 0xFFFF5555;
    static final int ERROR_GRADIENT_COLOUR     = 0xFFAA0000;
    static final int ERROR_GRADIENT_COLOUR2    = 0xFF550000;

    static final int VERSION_TEXT_COLOUR       = GuiModListPanel.GREY;
    static final int GRADIENT_COLOUR2          = GuiModListPanel.BLEND_2THRDS & GuiModListPanel.DARK_GREY;
    static final int HANGER_COLOUR             = GuiModListPanel.GREY;
    static final int HANGER_COLOUR_MOUSEOVER   = GuiModListPanel.WHITE;

    static final int PANEL_HEIGHT              = 32;
    static final int PANEL_SPACING             = 3;

    protected ModListEntry owner;

    /**
     * For text display
     */
    protected final FontRenderer fontRenderer;

    protected final int brandColour;

    protected final List<ModInfoDecorator> decorators;

    protected final ModInfo<?> modInfo;

    /**
     * True if the mouse was over this mod on the last render
     */
    private boolean mouseOver;

    private IconClickable mouseOverIcon = null;

    private List<IconTextured> modIcons = new ArrayList<IconTextured>();

    public GuiModListPanel(ModListEntry owner, FontRenderer fontRenderer, int brandColour, ModInfo<?> modInfo, List<ModInfoDecorator> decorators)
    {
        this.owner        = owner;
        this.fontRenderer = fontRenderer;
        this.brandColour  = brandColour;
        this.modInfo      = modInfo;
        this.decorators   = decorators;

        for (ModInfoDecorator decorator : this.decorators)
        {
            decorator.addIcons(modInfo, this.modIcons);
        }
    }

    public void draw(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, boolean selected, int pass)
    {
        if (pass == 0)
        {
            this.render(mouseX, mouseY, partialTicks, xPosition, yPosition, width, selected);
        }
        else if (pass == 1)
        {
            this.postRender(mouseX, mouseY, partialTicks, xPosition, yPosition, width, selected);
        }
    }

    /**
     * Draw this list entry as a list item
     * 
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param xPosition
     * @param yPosition
     * @param width
     * @param selected
     */
    protected void render(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, boolean selected)
    {
        int gradientColour = this.getGradientColour(selected);
        int titleColour    = this.getTitleColour(selected);
        int statusColour   = this.getStatusColour(selected);

        this.drawGradientRect(xPosition, yPosition, xPosition + width, yPosition + GuiModListPanel.PANEL_HEIGHT, gradientColour,
                GuiModListPanel.GRADIENT_COLOUR2);

        String titleText = this.owner.getTitleText();
        String versionText = this.owner.getVersionText();
        String statusText = this.owner.getStatusText();

        for (ModInfoDecorator decorator : this.decorators)
        {
            String newStatusText = decorator.modifyStatusText(this.modInfo, statusText);
            if (newStatusText != null) statusText = newStatusText;
        }

        this.fontRenderer.drawString(titleText,   xPosition + 5, yPosition + 2,  titleColour);
        this.fontRenderer.drawString(versionText, xPosition + 5, yPosition + 12, GuiModListPanel.VERSION_TEXT_COLOUR);
        this.fontRenderer.drawString(statusText,  xPosition + 5, yPosition + 22, statusColour);

        this.updateMouseOver(mouseX, mouseY, xPosition, yPosition, width); 
        int hangerColour = this.mouseOver ? GuiModListPanel.HANGER_COLOUR_MOUSEOVER : GuiModListPanel.HANGER_COLOUR;
        drawRect(xPosition, yPosition, xPosition + 1, yPosition + PANEL_HEIGHT, hangerColour);

        for (ModInfoDecorator decorator : this.decorators)
        {
            decorator.onDrawListEntry(mouseX, mouseY, partialTicks, xPosition, yPosition, width, GuiModListPanel.PANEL_HEIGHT, selected,
                    this.modInfo, gradientColour, titleColour, statusColour);
        }
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param xPosition
     * @param yPosition
     * @param width
     */
    protected void updateMouseOver(int mouseX, int mouseY, int xPosition, int yPosition, int width)
    {
        this.mouseOver = this.isMouseOver(mouseX, mouseY, xPosition, yPosition, width, PANEL_HEIGHT);
    }

    protected void postRender(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, boolean selected)
    {
        xPosition += (width - 14);
        yPosition += (GuiModListPanel.PANEL_HEIGHT - 14);

        this.mouseOverIcon = null;

        for (IconTextured icon : this.modIcons)
        {
            xPosition = this.drawPropertyIcon(xPosition, yPosition, icon, mouseX, mouseY);
        }
    }

    protected int drawPropertyIcon(int xPosition, int yPosition, IconTextured icon, int mouseX, int mouseY)
    {
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(icon.getTextureResource());

        glEnableBlend();
        this.drawTexturedModalRect(xPosition, yPosition, icon.getUPos(), icon.getVPos(), icon.getIconWidth(), icon.getIconHeight());
        glDisableBlend();

        if (mouseX >= xPosition && mouseX <= xPosition + 12 && mouseY >= yPosition && mouseY <= yPosition + 12)
        {
            String tooltipText = icon.getDisplayText();
            if (tooltipText != null)
            {
                glDisableClipping();
                GuiLiteLoaderPanel.drawTooltip(this.fontRenderer, tooltipText, mouseX, mouseY, 4096, 4096, GuiModListPanel.WHITE,
                        GuiModListPanel.BLEND_HALF & GuiModListPanel.BLACK);
                glEnableClipping();
            }

            if (icon instanceof IconClickable) this.mouseOverIcon = (IconClickable)icon;
        }

        return xPosition - 14;
    }

    /**
     * @param selected
     */
    protected int getGradientColour(boolean selected)
    {
        return GuiModListPanel.BLEND_2THRDS
            & (this.owner.isErrored()
                    ? (selected ? GuiModListPanel.ERROR_GRADIENT_COLOUR : GuiModListPanel.ERROR_GRADIENT_COLOUR2)
                    : (selected ? (this.owner.isExternal() ? GuiModListPanel.EXTERNAL_ENTRY_COLOUR : this.brandColour) : GuiModListPanel.BLACK));
    }

    /**
     * @param selected
     */
    protected int getTitleColour(boolean selected)
    {
        if (this.owner.isMissingDependencies()) return GuiModListPanel.MISSING_DEPENDENCY_COLOUR;
        if (this.owner.isMissingAPIs()) return GuiModListPanel.API_COLOUR;
        if (this.owner.isErrored()) return GuiModListPanel.ERROR_COLOUR;
        if (!this.owner.isActive()) return GuiModListPanel.GREY;
        return this.owner.isExternal() ? GuiModListPanel.EXTERNAL_ENTRY_COLOUR : GuiModListPanel.WHITE;
    }

    /**
     * @param selected
     */
    protected int getStatusColour(boolean selected)
    {
        return this.owner.isExternal() ? GuiModListPanel.EXTERNAL_ENTRY_COLOUR : this.brandColour;
    }

    public boolean isVisible()
    {
        return true;
    }

    public int getSpacing()
    {
        return GuiModListPanel.PANEL_SPACING;
    }

    public int getHeight()
    {
        return GuiModListPanel.PANEL_HEIGHT;
    }

    public int getTotalHeight()
    {
        return GuiModListPanel.PANEL_HEIGHT + GuiModListPanel.PANEL_SPACING;
    }

    protected boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height)
    {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public boolean isMouseOverIcon()
    {
        return this.mouseOver && this.mouseOverIcon != null;
    }

    public boolean isMouseOver()
    {
        return this.mouseOver;
    }

    public void iconClick(Object source)
    {
        if (this.mouseOverIcon != null)
        {
            this.mouseOverIcon.onClicked(source, this);
        }
    }

    public void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        this.owner.clearHelpMessage();
    }

    public void displayModHelpMessage(ModInfo<?> mod, String title, String text)
    {
        this.owner.displayHelpMessage(title, text);
    }
}
