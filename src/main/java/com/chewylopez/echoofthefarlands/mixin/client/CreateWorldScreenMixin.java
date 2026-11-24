package com.chewylopez.echoofthefarlands.mixin.client;

import com.chewylopez.echoofthefarlands.client.WorldCreationFarlandsScreen;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    @Shadow
    private boolean displayOptions;

    @Unique
    private Button eotfl$farlandsButton;

    private CreateWorldScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void eotfl$addFarlandsButton(CallbackInfo ci) {
        int rightColumn = this.width / 2 + 5;
        this.eotfl$farlandsButton = this.addButton(new Button(
                rightColumn, 151, 150, 20,
                new StringTextComponent("Farlands Options..."),
                b -> this.minecraft.setScreen(new WorldCreationFarlandsScreen(this))));
        this.eotfl$farlandsButton.visible = this.displayOptions;
    }

    @Inject(method = "setDisplayOptions", at = @At("TAIL"))
    private void eotfl$syncFarlandsButtonVisibility(boolean show, CallbackInfo ci) {
        if (this.eotfl$farlandsButton != null) {
            this.eotfl$farlandsButton.visible = show;
        }
    }
}
