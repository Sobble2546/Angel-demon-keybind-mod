package com.example.dashabilitymod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class DashAbilityMod implements ClientModInitializer {
    private static KeyBinding abilityKeybind;
    private static long lastJumpTime = 0;
    private static long lastRightClickTime = 0;

    @Override
    public void onInitializeClient() {
        // Register keybinding for R
        abilityKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dashabilitymod.ability",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.dashabilitymod"
        ));

        // Handle right click detection
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player != null && world.isClient) {
                lastRightClickTime = System.currentTimeMillis();
                checkForDashAbility();
            }
            return ActionResult.PASS;
        });

        // Handle jump detection
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (!client.player.isOnGround() && client.player.getVelocity().y > 0) {
                    // Player is moving upward and not on ground (jumping)
                    lastJumpTime = System.currentTimeMillis();
                    checkForDashAbility();
                }
            }

            // Check R key
            if (abilityKeybind.wasPressed()) {
                if (client.player != null) {
                    client.player.networkHandler.sendChatMessage("/ability");
                }
            }
        });
    }

    private static void checkForDashAbility() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        long now = System.currentTimeMillis();
        if (Math.abs(lastJumpTime - lastRightClickTime) < 200) { // within 200 ms
            client.player.networkHandler.sendChatMessage("/dashability");
            // Reset times to avoid spam
            lastJumpTime = 0;
            lastRightClickTime = 0;
        }
    }
}
