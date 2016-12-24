package com.minelittlepony.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyConfig;
import com.minelittlepony.PonyLevel;
import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class PonySettingPanel extends GuiScreen {

    private static final String _PREFIX = "minelp.options.";
    private static final String TITLE = _PREFIX + "title";
    private static final String PONY_LEVEL = _PREFIX + "ponylevel";
    private static final String PONY = PONY_LEVEL + ".ponies";
    private static final String HUMAN = PONY_LEVEL + ".humans";
    private static final String BOTH = PONY_LEVEL + ".both";
    private static final String OPTIONS = _PREFIX + "options";
    private static final String HD = _PREFIX + "hd";
    private static final String SIZES = _PREFIX + "sizes";
    private static final String SNUZZLES = _PREFIX + "snuzzles";
    private static final String SHOW_SCALE = _PREFIX + "showscale";

    private static final String MOB_PREFIX = "minelp.mobs.";

    private static final String MOB_TITLE = MOB_PREFIX + "title";
    private static final String VILLAGERS = MOB_PREFIX + "villagers";
    private static final String ZOMBIES = MOB_PREFIX + "zombies";
    private static final String ZOMBIE_PIGMEN = MOB_PREFIX + "zombiepigmen";
    private static final String SKELETONS = MOB_PREFIX + "skeletons";

    private static final int PONY_ID = 0;
    private static final int HUMAN_ID = 1;
    private static final int BOTH_ID = 2;
    private static final int HD_ID = 3;
    private static final int SIZES_ID = 4;
    private static final int SNUZZLES_ID = 5;
    private static final int SHOW_SCALE_ID = 6;

    private static final int VILLAGERS_ID = 7;
    private static final int ZOMBIES_ID = 8;
    private static final int ZOMBIE_PIGMEN_ID = 9;
    private static final int SKELETONS_ID = 10;

    private PonyConfig config;

    private GuiCheckbox ponies;
    private GuiCheckbox humans;
    private GuiCheckbox both;

    public PonySettingPanel() {
        config = MineLittlePony.getConfig();
    }

    @SuppressWarnings("UnusedAssignment")
    @Override
    public void initGui() {
        final int LEFT = width / 10 + 16;
        GuiCheckbox pony, human, both, hd, sizes, snuzzles, showscale, villager, zombie, pigmen, skeleton;
        int row = 32;
        this.buttonList.add(pony = ponies = new GuiCheckbox(PONY_ID, LEFT, row += 15, I18n.format(PONY)));
        this.buttonList.add(human = humans = new GuiCheckbox(HUMAN_ID, LEFT, row += 15, I18n.format(HUMAN)));
        this.buttonList.add(both = this.both = new GuiCheckbox(BOTH_ID, LEFT, row += 15, I18n.format(BOTH)));
        row += 15;
        this.buttonList.add(hd = new GuiCheckbox(HD_ID, LEFT, row += 15, I18n.format(HD)));
        this.buttonList.add(sizes = new GuiCheckbox(SIZES_ID, LEFT, row += 15, I18n.format(SIZES)));
        this.buttonList.add(snuzzles = new GuiCheckbox(SNUZZLES_ID, LEFT, row += 15, I18n.format(SNUZZLES)));
        this.buttonList.add(showscale = new GuiCheckbox(SHOW_SCALE_ID, LEFT, row += 15, I18n.format(SHOW_SCALE)));

        final int RIGHT = width - width / 3;
        row = 32;
        this.buttonList.add(villager = new GuiCheckbox(VILLAGERS_ID, RIGHT, row += 15, I18n.format(VILLAGERS)));
        this.buttonList.add(zombie = new GuiCheckbox(ZOMBIES_ID, RIGHT, row += 15, I18n.format(ZOMBIES)));
        this.buttonList.add(pigmen = new GuiCheckbox(ZOMBIE_PIGMEN_ID, RIGHT, row += 15, I18n.format(ZOMBIE_PIGMEN)));
        this.buttonList.add(skeleton = new GuiCheckbox(SKELETONS_ID, RIGHT, row += 15, I18n.format(SKELETONS)));

        switch (config.getPonyLevel()) {
            default:
            case PONIES:
                pony.checked = true;
                break;
            case HUMANS:
                human.checked = true;
                break;
            case BOTH:
                both.checked = true;
                break;
        }
        hd.checked = config.hd;
        sizes.checked = config.sizes;
        snuzzles.checked = config.snuzzles;
        showscale.checked = config.showscale;
        villager.checked = config.villagers;
        zombie.checked = config.zombies;
        pigmen.checked = config.pigzombies;
        skeleton.checked = config.skeletons;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        this.drawCenteredString(mc.fontRendererObj, I18n.format(TITLE), width / 2, 12, -1);

        this.drawString(mc.fontRendererObj, I18n.format(MOB_TITLE), width - width / 3 - 16, 32, -1);
        this.drawString(mc.fontRendererObj, I18n.format(PONY_LEVEL), width / 10, 32, -1);
        this.drawString(mc.fontRendererObj, I18n.format(OPTIONS), width / 10, 94, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof GuiCheckbox) {
            boolean checked = !((GuiCheckbox) button).checked;
            ((GuiCheckbox) button).checked = checked;

            switch (button.id) {
                case PONY_ID:
                    config.setPonyLevel(PonyLevel.PONIES);
                    ponies.checked = true;
                    humans.checked = false;
                    both.checked = false;
                    break;
                case HUMAN_ID:
                    config.setPonyLevel(PonyLevel.HUMANS);
                    humans.checked = true;
                    ponies.checked = false;
                    both.checked = false;
                    break;
                case BOTH_ID:
                    config.setPonyLevel(PonyLevel.BOTH);
                    both.checked = true;
                    ponies.checked = false;
                    humans.checked = false;
                    break;
                case HD_ID:
                    config.hd = checked;
                    break;
                case SIZES_ID:
                    config.sizes = checked;
                    break;
                case SNUZZLES_ID:
                    config.snuzzles = checked;
                    break;
                case SHOW_SCALE_ID:
                    config.showscale = checked;
                    break;

                case VILLAGERS_ID:
                    config.villagers = checked;
                    break;
                case ZOMBIES_ID:
                    config.zombies = checked;
                    break;
                case ZOMBIE_PIGMEN_ID:
                    config.pigzombies = checked;
                    break;
                case SKELETONS_ID:
                    config.skeletons = checked;
                    break;
            }
        }
    }

    @Override
    public void onGuiClosed() {
        LiteLoader.getInstance().writeConfig(config);
        MineLittlePony.getInstance().initializeMobRenderers(mc.getRenderManager());
    }
}
