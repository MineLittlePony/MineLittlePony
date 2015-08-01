package com.voxelmodpack.common.status;

import static com.mumfrey.liteloader.gl.GL.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import net.minecraft.client.Minecraft;

import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.voxelmodpack.common.properties.ModConfig;
import com.voxelmodpack.common.properties.gui.SettingsPanelManager;

/**
 * @author anangrybeaver
 */
public class StatusMessageManager implements Tickable {
    private static StatusMessageManager instance;

    private static int order = 0;

    private Map<String, StatusMessage> statusMessages = new HashMap<String, StatusMessage>();
    private TreeSet<StatusMessage> sortedMessage = new TreeSet<StatusMessage>();
    private StatusMessageConfig config = new StatusMessageConfig();

    private boolean showStatuses = true;

    @Override
    public void init(File configPath) {
        this.showStatuses = this.config.getBoolProperty("showStatuses");
    }

    public static StatusMessageManager getInstance() {
        if (StatusMessageManager.instance == null) {
            StatusMessageManager.instance = new StatusMessageManager();
            LiteLoader.getInterfaceManager().registerListener(StatusMessageManager.instance);
            SettingsPanelManager.addSettingsPanel("Status MSG", StatusMessageGUI.class);
        }

        return StatusMessageManager.instance;
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        this.showStatuses = this.config.getBoolProperty("showStatuses");

        if (minecraft.inGameHasFocus && !minecraft.gameSettings.hideGUI && !minecraft.gameSettings.showDebugInfo
                && this.showStatuses) {
            int xPos = 2;
            int yPos = xPos;

            glDisableBlend();
            glEnableAlphaTest();
            glAlphaFunc(GL_GREATER, 0.1F);

            for (StatusMessage msg : this.sortedMessage) {
                yPos += msg.drawStatus(minecraft.fontRendererObj, xPos, yPos);
            }
        }
    }

    public StatusMessage getStatusMessage(String label, int priority) {
        if (this.statusMessages.containsKey(label))
            return this.statusMessages.get(label);

        StatusMessage newMessage = new StatusMessage(label, priority, order++);
        this.statusMessages.put(label, newMessage);
        this.sortedMessage.add(newMessage);

        return newMessage;
    }

    public void releaseStatusMessage(StatusMessage message) {
        this.sortedMessage.remove(message);
        this.statusMessages.remove(message);
    }

    public ModConfig getConfig() {
        return this.config;
    }

    public void toggleOption(String optionName) {
        this.config.setProperty(optionName, !this.config.getBoolProperty(optionName));
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {}
}
