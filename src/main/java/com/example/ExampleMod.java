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
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isSpectator() || player.isCreative() || !player.isAlive()) {
                    continue;
                }

                ensureArmor(player, EquipmentSlot.HEAD, Items.IRON_HELMET);
                ensureArmor(player, EquipmentSlot.CHEST, Items.IRON_CHESTPLATE);
                ensureArmor(player, EquipmentSlot.LEGS, Items.IRON_LEGGINGS);
                ensureArmor(player, EquipmentSlot.FEET, Items.IRON_BOOTS);

                ensureTool(player, Items.IRON_SWORD);
                ensureTool(player, Items.IRON_PICKAXE);
                ensureTool(player, Items.IRON_AXE);
                ensureTool(player, Items.IRON_SHOVEL);
            }
        });
    }

    private void ensureArmor(ServerPlayerEntity player, EquipmentSlot slot, Item expectedItem) {
        ItemStack current = player.getEquippedStack(slot);
        if (current.isEmpty() || !current.isOf(expectedItem)) {
            player.equipStack(slot, new ItemStack(expectedItem));
        }
    }

    private void ensureTool(ServerPlayerEntity player, Item toolItem) {
        if (!hasItemInInventory(player, toolItem)) {
            player.getInventory().insertStack(new ItemStack(toolItem));
        }
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