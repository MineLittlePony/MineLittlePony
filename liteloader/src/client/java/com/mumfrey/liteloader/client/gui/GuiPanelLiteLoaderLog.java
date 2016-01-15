package com.mumfrey.liteloader.client.gui;

import static com.mumfrey.liteloader.gl.GL.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.net.LiteLoaderLogUpload;

/**
 *
 * @author Adam Mummery-Smith
 */
class GuiPanelLiteLoaderLog extends GuiPanel implements ScrollPanelContent
{
    private static boolean useNativeRes = true;

    /**
     * Scroll pane
     */
    private GuiScrollPanel scrollPane;

    private List<String> logEntries = new ArrayList<String>();

    private long logIndex = -1;

    private GuiCheckbox chkScale;

    private float guiScale;

    private GuiButton btnUpload;

    private LiteLoaderLogUpload logUpload;

    private String logURL;

    private int throb;

    private boolean closeDialog;

    private GuiLiteLoaderPanel parent;

    private int debugInfoTimer = 0;

    /**
     * @param minecraft
     * @param parent
     */
    GuiPanelLiteLoaderLog(Minecraft minecraft, GuiLiteLoaderPanel parent)
    {
        super(minecraft);
        this.parent = parent;
        this.scrollPane = new GuiScrollPanel(minecraft, this, MARGIN, TOP, this.width - (MARGIN * 2), this.height - TOP - BOTTOM);
    }

    private void updateLog()
    {
        this.logEntries = LiteLoaderLogger.getLogTail();
        this.logIndex = LiteLoaderLogger.getLogIndex();
        this.scrollPane.updateHeight();
        this.scrollPane.scrollToBottom();
    }

    @Override
    public int getScrollPanelContentHeight(GuiScrollPanel source)
    {
        return (int)(this.logEntries.size() * 10 / (this.chkScale.checked ? this.guiScale : 1.0F));
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

        this.controls.add(new GuiButton(0, this.width - 59 - MARGIN, this.height - BOTTOM + 9, 60, 20,
                I18n.format("gui.done")));
        this.controls.add(this.btnUpload = new GuiButton(1, this.width - 145 - MARGIN, this.height - BOTTOM + 9, 80, 20,
                I18n.format("gui.log.postlog")));
        this.controls.add(this.chkScale = new GuiCheckbox(2, MARGIN, this.height - BOTTOM + 15,
                I18n.format("gui.log.scalecheckbox")));

        this.chkScale.checked = GuiPanelLiteLoaderLog.useNativeRes;

        ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.guiScale = res.getScaleFactor();

        this.scrollPane.setSizeAndPosition(MARGIN, TOP, this.width - (MARGIN * 2), this.height - TOP - BOTTOM);

        this.updateLog();
    }

    /**
     * Callback from parent screen when panel is displayed
     */
    @Override
    void onShown()
    {
    }

    /**
     * Callback from parent screen when panel is hidden
     */
    @Override
    void onHidden()
    {
    }

    /**
     * Callback from parent screen every tick
     */
    @Override
    void onTick()
    {
        this.throb++;

        if (LiteLoaderLogger.getLogIndex() > this.logIndex)
        {
            this.updateLog();
        }

        if (this.logUpload != null && this.logUpload.isCompleted())
        {
            this.logURL = this.logUpload.getLogUrl().trim();
            this.logUpload = null;

            int xMid = this.width / 2;
            if (this.logURL.startsWith("http:"))
            {
                LiteLoaderLogger.info("Log file upload succeeded, url is %s", this.logURL);
                int urlWidth = this.mc.fontRendererObj.getStringWidth(this.logURL);
                this.controls.add(new GuiHoverLabel(3, xMid - (urlWidth / 2), this.height / 2, this.mc.fontRendererObj, "\247n" + this.logURL,
                        this.parent.getBrandColour()));
            }
            else
            {
                LiteLoaderLogger.info("Log file upload failed, reason is %s", this.logURL);
            }

            this.controls.add(new GuiButton(4, xMid - 40, this.height - BOTTOM - MARGIN - 24, 80, 20, I18n.format("gui.log.closedialog")));
        }

        if (this.closeDialog)
        {
            this.closeDialog = false;
            this.logURL = null;
            this.setSize(this.width, this.height);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_F3))
        {
            this.debugInfoTimer++;
            if (this.debugInfoTimer == 60)
            {
                LiteLoader.dumpDebugInfo();
            }
        }
        else
        {
            this.debugInfoTimer = 0;
        }
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
        // Draw panel title
        this.mc.fontRendererObj.drawString(I18n.format("gui.log.title"), MARGIN, TOP - 14, 0xFFFFFFFF);

        // Draw top and bottom horizontal bars
        drawRect(MARGIN, TOP - 4, this.width - MARGIN, TOP - 3, 0xFF999999);
        drawRect(MARGIN, this.height - BOTTOM + 2, this.width - MARGIN, this.height - BOTTOM + 3, 0xFF999999);

        this.scrollPane.draw(mouseX, mouseY, partialTicks);

        int xMid = this.width / 2;
        int yMid = this.height / 2;

        if (this.logUpload != null || this.logURL != null)
        {
            drawRect(MARGIN + MARGIN, TOP + MARGIN, this.width - MARGIN - MARGIN, this.height - BOTTOM - MARGIN, 0xC0000000);

            if (this.logUpload != null)
            {
                this.drawCenteredString(this.mc.fontRendererObj, I18n.format("gui.log.uploading"), xMid, yMid - 10, 0xFFFFFFFF);
                this.drawThrobber(xMid - 90, yMid - 14, this.throb);
            }
            else
            {
                if (this.logURL.startsWith("http:"))
                {
                    this.drawCenteredString(this.mc.fontRendererObj, I18n.format("gui.log.uploadsuccess"), xMid, yMid - 14, 0xFF55FF55);
                }
                else
                {
                    this.drawCenteredString(this.mc.fontRendererObj, I18n.format("gui.log.uploadfailed"), xMid, yMid - 10, 0xFFFF5555);
                }
            }
        }

        // Draw other buttons
        super.draw(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawScrollPanelContent(GuiScrollPanel source, int mouseX, int mouseY, float partialTicks, int scrollAmount, int visibleHeight)
    {
        int yPos = 0;
        int height = this.innerHeight;

        if (this.chkScale.checked)
        {
            float scale = 1.0F / this.guiScale;
            glScalef(scale, scale, scale);

            height = (int)(height * this.guiScale);
            scrollAmount = (int)(scrollAmount * this.guiScale);
        }

        for (String logLine : this.logEntries)
        {
            if (yPos > scrollAmount - 10 && yPos <= scrollAmount + height)
            {
                this.mc.fontRendererObj.drawString(logLine, 0, yPos, this.getMessageColour(logLine.toLowerCase().substring(11)));
            }
            yPos += 10;
        }
    }

    @Override
    public void scrollPanelMousePressed(GuiScrollPanel source, int mouseX, int mouseY, int mouseButton)
    {
    }

    private int getMessageColour(String logLine)
    {
        if (logLine.startsWith("liteloader")) return 0xFFFFFF;
        if (logLine.startsWith("active pack:")) return 0xFFFF55;
        if (logLine.startsWith("success")) return 0x55FF55;
        if (logLine.startsWith("discovering")) return 0xFFFF55;
        if (logLine.startsWith("searching")) return 0x00AA00;
        if (logLine.startsWith("considering")) return 0xFFAA00;
        if (logLine.startsWith("not adding")) return 0xFF5555;
        if (logLine.startsWith("mod in")) return 0xAA0000;
        if (logLine.startsWith("error")) return 0xAA0000;
        if (logLine.startsWith("adding newest")) return 0x5555FF;
        if (logLine.startsWith("found")) return 0xFFFF55;
        if (logLine.startsWith("discovered")) return 0xFFFF55;
        if (logLine.startsWith("setting up")) return 0xAA00AA;
        if (logLine.startsWith("adding \"")) return 0xAA00AA;
        if (logLine.startsWith("injecting")) return 0xFF55FF;
        if (logLine.startsWith("loading")) return 0x5555FF;
        if (logLine.startsWith("initialising")) return 0x55FFFF;
        if (logLine.startsWith("calling late")) return 0x00AAAA;
        if (logLine.startsWith("dependency check")) return 0xFFAA00;
        if (logLine.startsWith("dependency")) return 0xFF5500;
        if (logLine.startsWith("mod name collision")) return 0xAA0000;
        if (logLine.startsWith("registering discovery module")) return 0x55FF55;
        if (logLine.startsWith("registering interface provider")) return 0xFFAA00;
        if (logLine.startsWith("mod file '")) return 0xFFAA00;
        if (logLine.startsWith("classtransformer '")) return 0x5555FF;
        if (logLine.startsWith("tweakClass '")) return 0x5555FF;
        if (logLine.startsWith("baking listener list")) return 0x00AAAA;
        if (logLine.startsWith("generating new event handler")) return 0xFFFF55;

        return 0xCCCCCC;
    }

    /**
     * @param control
     */
    @Override
    public void actionPerformed(GuiButton control)
    {
        if (control.id == 0) this.close();
        if (control.id == 1) this.postLog();

        if (control.id == 2 && this.chkScale != null)
        {
            this.chkScale.checked = !this.chkScale.checked;
            GuiPanelLiteLoaderLog.useNativeRes = this.chkScale.checked;
            this.updateLog();
        }

        if (control.id == 3 && this.logURL != null)
        {
            this.openURI(URI.create(this.logURL));
        }

        if (control.id == 4)
        {
            this.closeDialog = true;
        }
    }

    @Override
    public void scrollPanelActionPerformed(GuiScrollPanel source, GuiButton control)
    {
    }

    /**
     * @param mouseWheelDelta
     */
    @Override
    void mouseWheelScrolled(int mouseWheelDelta)
    {
        this.scrollPane.mouseWheelScrolled(mouseWheelDelta);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    @Override
    void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollPane.mousePressed(mouseX, mouseY, mouseButton);

        super.mousePressed(mouseX, mouseY, mouseButton);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    @Override
    void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollPane.mouseReleased(mouseX, mouseY, mouseButton);
    }

    /**
     * @param mouseX
     * @param mouseY
     */
    @Override
    void mouseMoved(int mouseX, int mouseY)
    {
    }

    /**
     * @param keyChar
     * @param keyCode
     */
    @Override
    void keyPressed(char keyChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE) this.close();
        if (keyCode == Keyboard.KEY_SPACE) this.actionPerformed(this.chkScale);

        this.scrollPane.keyPressed(keyChar, keyCode);
    }

    private void postLog()
    {
        this.btnUpload.enabled = false;

        StringBuilder completeLog = new StringBuilder();

        for (String logLine : this.logEntries)
        {
            completeLog.append(logLine).append("\r\n");
        }

        LiteLoaderLogger.info("Uploading log file to liteloader...");
        Session session = this.mc.getSession();
        this.logUpload = new LiteLoaderLogUpload(session.getUsername(), session.getPlayerID(), completeLog.toString());
        this.logUpload.start();
    }

    private void openURI(URI uri)
    {
        try
        {
            Class<?> desktop = Class.forName("java.awt.Desktop");
            Object instance = desktop.getMethod("getDesktop").invoke(null);
            desktop.getMethod("browse", URI.class).invoke(instance, uri);
        }
        catch (Throwable th) {}
    }
}
