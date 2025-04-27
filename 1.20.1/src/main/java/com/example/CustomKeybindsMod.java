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
    
    @Override
    public void onInitializeClient() {
        // Register the 'R' key for /ability
        abilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.customkeybinds.ability",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.customkeybinds.general"
        ));
        
        // Register Right Alt for /dashability instead of Space+Right Click
        dashabilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.customkeybinds.dashability",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_ALT,  // Right Alt key
            "category.customkeybinds.general"
        ));
        
        // Register tick event to check keybinds
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                // Check if R is pressed for ability
                if (abilityKey.wasPressed()) {
                    // Send the ability command
                    client.getNetworkHandler().sendChatCommand("ability");
                }
                
                // Check if Right Alt is pressed for dashability
                if (dashabilityKey.wasPressed()) {
                    // Send the dashability command
                    client.getNetworkHandler().sendChatCommand("dashability");
                }
            }
        });
    }
}