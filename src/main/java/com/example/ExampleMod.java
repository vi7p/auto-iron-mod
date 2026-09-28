package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class ExampleMod implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("[AutoIronMod] Mod loaded and active!");

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isSpectator() || player.isCreative() || !player.isAlive()) {
                    continue;
                }

                boolean updated = false;

                updated |= ensureArmor(player, EquipmentSlot.HEAD, Items.IRON_HELMET);
                updated |= ensureArmor(player, EquipmentSlot.CHEST, Items.IRON_CHESTPLATE);
                updated |= ensureArmor(player, EquipmentSlot.LEGS, Items.IRON_LEGGINGS);
                updated |= ensureArmor(player, EquipmentSlot.FEET, Items.IRON_BOOTS);

                updated |= ensureTool(player, Items.IRON_SWORD);
                updated |= ensureTool(player, Items.IRON_PICKAXE);
                updated |= ensureTool(player, Items.IRON_AXE);
                updated |= ensureTool(player, Items.IRON_SHOVEL);

                // Force Minecraft to sync inventory updates to the player screen immediately
                if (updated) {
                    player.playerScreenHandler.sendContentUpdates();
                }
            }
        });
    }

    private boolean ensureArmor(ServerPlayerEntity player, EquipmentSlot slot, Item expectedItem) {
        ItemStack current = player.getEquippedStack(slot);
        if (current.isEmpty() || !current.isOf(expectedItem)) {
            player.equipStack(slot, new ItemStack(expectedItem));
            return true;
        }
        return false;
    }

    private boolean ensureTool(ServerPlayerEntity player, Item toolItem) {
        if (!hasItemInInventory(player, toolItem)) {
            player.getInventory().insertStack(new ItemStack(toolItem));
            return true;
        }
        return false;
    }

    private boolean hasItemInInventory(ServerPlayerEntity player, Item item) {
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty() && stack.isOf(item)) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().offHand) {
            if (!stack.isEmpty() && stack.isOf(item)) {
                return true;
            }
        }
        return false;
    }
}