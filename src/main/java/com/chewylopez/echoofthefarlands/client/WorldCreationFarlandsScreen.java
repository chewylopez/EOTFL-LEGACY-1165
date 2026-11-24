package com.chewylopez.echoofthefarlands.client;

import com.chewylopez.echoofthefarlands.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class WorldCreationFarlandsScreen extends Screen {

    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;

    private final Screen parent;

    private int labelX;
    private int locationRowY;
    private int genTypeRowY;

    public WorldCreationFarlandsScreen(Screen parent) {
        super(new StringTextComponent("Farlands Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        MenuOptionsGrouping[] distances = PresetLists.buildDistances();
        MenuOptionsGrouping[] genTypes = PresetLists.buildGenTypes();

        this.labelX = this.width / 2 - 155;
        int buttonX = this.width / 2 + 5;
        this.locationRowY = this.height / 2 - 30;
        this.genTypeRowY = this.locationRowY + 25;

        this.addButton(new CyclingOptionButton(buttonX, this.locationRowY, BUTTON_WIDTH, BUTTON_HEIGHT,
                distances, Config.FARLANDS_LOCATION_WORLD.get(),
                value -> Config.FARLANDS_LOCATION_WORLD.set(value), this));

        this.addButton(new CyclingOptionButton(buttonX, this.genTypeRowY, BUTTON_WIDTH, BUTTON_HEIGHT,
                genTypes, Config.FARLANDS_GEN_TYPE.get(),
                value -> Config.FARLANDS_GEN_TYPE.set(value), this));

        this.addButton(new Button(this.width / 2 - 100, this.height - 28, 200, BUTTON_HEIGHT,
                DialogTexts.GUI_DONE, b -> this.onClose()));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title, this.width / 2, 15, 0xFFFFFF);

        drawString(stack, this.font, "Farlands Location", this.labelX, this.locationRowY + 6, 0xFFFFFF);
        drawString(stack, this.font, "Generation Type", this.labelX, this.genTypeRowY + 6, 0xFFFFFF);

        super.render(stack, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Config.SPEC.save();
        this.minecraft.setScreen(this.parent);
    }
}
