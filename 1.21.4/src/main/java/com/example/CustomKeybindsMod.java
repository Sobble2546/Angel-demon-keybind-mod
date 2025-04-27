package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CustomKeybindsMod implements ClientModInitializer {
    
    private static KeyBinding abilityKey;
    private static KeyBinding dashabilityKey;
    
    private boolean isSpacePressed = false;
    private long spacePressTime = 0;
    private static final long SPACE_RIGHT_CLICK_THRESHOLD = 500; // milliseconds
    
    @Override
    public void onInitializeClient() {
        // Register the 'R' key for /ability
        abilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.customkeybinds.ability",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.customkeybinds.general"
        ));
        
        // For Spacebar + Right click combo, we'll detect it in the tick event
        // Register spacebar for tracking purposes
        dashabilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.customkeybinds.dashability",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE,
            "category.customkeybinds.general"
        ));
        
        // Register tick event to check keybinds
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                // Check if R is pressed
                if (abilityKey.wasPressed()) {
                    // Use the correct method to send commands
                    client.getNetworkHandler().sendChatCommand("ability");
                }
                
                // Check for Spacebar + Right click combo
                // First, detect spacebar press
                if (dashabilityKey.isPressed() && !isSpacePressed) {
                    isSpacePressed = true;
                    spacePressTime = System.currentTimeMillis();
                }
                
                // Reset spacebar flag when released
                if (!dashabilityKey.isPressed() && isSpacePressed) {
                    isSpacePressed = false;
                }
                
                // Check for right mouse click while spacebar is pressed
                if (isSpacePressed && client.options.attackKey.isPressed()) {
                    // Only execute if spacebar was pressed within the threshold time
                    if (System.currentTimeMillis() - spacePressTime < SPACE_RIGHT_CLICK_THRESHOLD) {
                        // Use the correct method to send commands
                        client.getNetworkHandler().sendChatCommand("dashability");
                        // Brief cooldown to prevent multiple activations
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}