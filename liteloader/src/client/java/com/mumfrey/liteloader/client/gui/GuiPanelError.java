package com.mumfrey.liteloader.client.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.core.ModInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiPanelError extends GuiPanel implements ScrollPanelContent
{
    private final ModInfo<?> mod;

    private GuiScrollPanel scrollPane;

    private List<String> scrollPaneContent = new ArrayList<String>();

    public GuiPanelError(Minecraft minecraft, GuiLiteLoaderPanel parent, ModInfo<?> mod)
    {
        super(minecraft);

        this.mod = mod;
        this.scrollPane = new GuiScrollPanel(minecraft, this, MARGIN, TOP, this.width - (MARGIN * 2), this.height - TOP - BOTTOM);

        this.populateScrollPaneContent();
    }

    private void populateScrollPaneContent()
    {
        for (Throwable th : this.mod.getStartupErrors())
        {
            StringWriter sw = new StringWriter();
            th.printStackTrace(new PrintWriter(sw, true));
            for (String line : sw.toString().split("\\r?\\n"))
            {
                this.scrollPaneContent.add(line.replace("\t", "   "));
            }

            this.scrollPaneContent.add("!");
        }

        this.scrollPaneContent.remove(this.scrollPaneContent.size() - 1);
    }

    @Override
    public int getScrollPanelContentHeight(GuiScrollPanel source)
    {
        return this.scrollPaneContent.size() * 10;
    }

    @Override
    public void drawScrollPanelContent(GuiScrollPanel source, int mouseX, int mouseY, float partialTicks, int scrollAmount, int visibleHeight)
    {
        int yPos = -10;

        for (String line : this.scrollPaneContent)
        {
            if ("!".equals(line))
            {
                yPos += 10;
                drawRect(0, yPos + 4, this.width, yPos + 5, 0xFF555555);
            }
            else
            {
                boolean indented = line.startsWith("   ");
                line = line.replaceAll("\\((.+?\\.java:[0-9]+)\\)", "(\247f$1\247r)");
                line = line.replaceAll("at ([^\\(]+)\\(", "at \2476$1\247r(");
                this.mc.fontRendererObj.drawString(line, 2, yPos += 10, indented ? 0xFF999999 : 0xFFFF5555);
            }
        }
    }

    @Override
    public void scrollPanelActionPerformed(GuiScrollPanel source, GuiButton control)
    {
    }

    @Override
    public void scrollPanelMousePressed(GuiScrollPanel source, int mouseX, int mouseY, int mouseButton)
    {
    }

    @Override
    void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.scrollPane.setSizeAndPosition(MARGIN, TOP, this.width - (MARGIN * 2), this.height - TOP - BOTTOM);
        this.controls.add(new GuiButton(0, this.width - 59 - MARGIN, this.height - BOTTOM + 9, 60, 20, I18n.format("gui.done")));
    }

    @Override
    void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.mc.fontRendererObj.drawString(I18n.format("gui.error.title", this.mod.getDisplayName()), MARGIN, TOP - 14, 0xFFFFFFFF);

        drawRect(MARGIN, TOP - 4, this.width - MARGIN, TOP - 3, 0xFF999999);
        drawRect(MARGIN, this.height - BOTTOM + 2, this.width - MARGIN, this.height - BOTTOM + 3, 0xFF999999);

        this.scrollPane.draw(mouseX, mouseY, partialTicks);

        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    void onTick()
    {
    }

    @Override
    void onHidden()
    {
    }

    @Override
    void onShown()
    {
    }

    @Override
    void keyPressed(char keyChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE) this.close();
    }

    @Override
    void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollPane.mousePressed(mouseX, mouseY, mouseButton);
        super.mousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    void mouseMoved(int mouseX, int mouseY)
    {
    }

    @Override
    void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollPane.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    void mouseWheelScrolled(int mouseWheelDelta)
    {
        this.scrollPane.mouseWheelScrolled(mouseWheelDelta);
    }

    @Override
    void actionPerformed(GuiButton control)
    {
        if (control.id == 0) this.close();
    }
}
