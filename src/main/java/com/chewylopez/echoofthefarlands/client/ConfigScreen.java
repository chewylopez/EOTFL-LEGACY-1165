package com.chewylopez.echoofthefarlands.client;

import com.chewylopez.echoofthefarlands.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class ConfigScreen extends Screen {

    private static final int VALID_TEXT_COLOUR = 14737632;
    private static final int COMMITTED_TEXT_COLOUR = 424242;
    private static final int REJECTED_TEXT_COLOUR = 0xFF5555;

    private final Screen parent;

    private TextFieldWidget locationBox;
    private String feedback = "";

    public ConfigScreen(Screen parent) {
        super(new StringTextComponent("Echo of the Farlands Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.locationBox = new TextFieldWidget(this.font, this.width / 2 - 100, 100, 200, 20,
                new StringTextComponent("farlandsLocation"));
        this.locationBox.setValue(Integer.toString(Config.FARLANDS_LOCATION_CUSTOM.get()));
        this.locationBox.setFilter(text -> {
            if (text.isEmpty()) {
                return true;
            }
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
        this.addButton(this.locationBox);

        this.addButton(new Button(this.width / 2 - 100, 130, 200, 20,
                new StringTextComponent("Update Value"), b -> this.commit()));

        this.addButton(new Button(this.width / 2 - 100, this.height - 40, 200, 20,
                DialogTexts.GUI_DONE, b -> this.onClose()));

        this.setInitialFocus(this.locationBox);
    }

    private void commit() {
        String raw = this.locationBox.getValue();

        int parsed;
        try {
            parsed = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            this.locationBox.setTextColor(REJECTED_TEXT_COLOUR);
            this.feedback = raw.isEmpty() ? "enter a value" : "not a whole number";
            return;
        }

        if (parsed < 1) {
            this.locationBox.setTextColor(REJECTED_TEXT_COLOUR);
            this.feedback = "must be 1 or greater";
            return;
        }

        Config.FARLANDS_LOCATION_CUSTOM.set(parsed);
        Config.FARLANDS_LOCATION_CUSTOM.save();
        Config.SPEC.save();

        this.locationBox.setTextColor(COMMITTED_TEXT_COLOUR);
        this.feedback = "saved";
    }

    @Override
    public void tick() {
        this.locationBox.tick();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(stack);

        drawCenteredString(stack, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        drawCenteredString(stack, this.font, "farlands location (default is 1,000,000)",
                this.width / 2, 76, 0xA0A0A0);

        super.render(stack, mouseX, mouseY, partialTick);

        if (!this.feedback.isEmpty()) {
            drawCenteredString(stack, this.font, this.feedback, this.width / 2, 158, 0xA0A0A0);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
