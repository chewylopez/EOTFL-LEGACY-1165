package com.chewylopez.echoofthefarlands.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class CyclingOptionButton extends Button {

    private final MenuOptionsGrouping[] options;
    private final IntConsumer onSelect;
    private final Screen screen;
    private int index;

    public CyclingOptionButton(int x, int y, int width, int height,
                               MenuOptionsGrouping[] options, int initialValue,
                               IntConsumer onSelect, Screen screen) {
        super(x, y, width, height, StringTextComponent.EMPTY, b -> { });
        this.options = options;
        this.onSelect = onSelect;
        this.screen = screen;
        this.index = PresetLists.getIndexFromIntValue(initialValue, options);
        this.setMessage(new StringTextComponent(this.current().getText()));
    }

    private MenuOptionsGrouping current() {
        return this.options[this.index];
    }

    public int getSelectedValue() {
        return this.current().getValue();
    }

    @Override
    public void onPress() {
        if (this.options.length == 0) {
            return;
        }
        this.index = (this.index + 1) % this.options.length;
        this.setMessage(new StringTextComponent(this.current().getText()));
        this.onSelect.accept(this.current().getValue());
    }

    @Override
    public void renderToolTip(MatrixStack stack, int mouseX, int mouseY) {
        List<String> raw = PresetLists.splitTooltip(this.current().getTooltip());
        if (raw.isEmpty()) {
            return;
        }
        List<ITextComponent> lines = new ArrayList<>(raw.size());
        for (String line : raw) {
            lines.add(new StringTextComponent(line));
        }
        this.screen.renderWrappedToolTip(stack, lines, mouseX, mouseY, Minecraft.getInstance().font);
    }
}
