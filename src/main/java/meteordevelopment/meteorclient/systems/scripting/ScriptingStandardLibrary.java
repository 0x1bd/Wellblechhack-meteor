/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.scripting;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Objects;

public class ScriptingStandardLibrary {

    public void info(String message) {
        MeteorClient.LOG.info(message);
    }

    public void warn(String message) {
        MeteorClient.LOG.warn(message);
    }

    public void error(String message) {
        MeteorClient.LOG.error(message);
    }

    public void feedback(String message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(message));
    }

    public void command(String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendCommand(command);
    }

    public void chat(String message) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendChatMessage(message);
    }

    public boolean isItemInSlot(int slot, String item, String where) {
        Objects.requireNonNull(MinecraftClient.getInstance().player);

        if (where.equalsIgnoreCase("inventory")) {
            return MinecraftClient.getInstance().player.getInventory().getStack(slot).getItem().getName().getString().equalsIgnoreCase(item);
        } else if (where.equalsIgnoreCase("container")) {
            return MinecraftClient.getInstance().player.currentScreenHandler.slots.get(slot).getStack().getItem().getName().getString().equalsIgnoreCase(item);
        } else {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }
    }

    public String getOpenContainer() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen == null) return "none";
        return switch (screen) {
            case GenericContainerScreen genericContainerScreen -> "generic";
            case ShulkerBoxScreen shulkerBoxScreen -> "shulker";
            case FurnaceScreen furnaceScreen -> "furnace";
            case HopperScreen hopperScreen -> "hopper";
            case AnvilScreen anvilScreen -> "anvil";
            case CrafterScreen crafterScreen -> "crafter";
            case CraftingScreen craftingScreen -> "crafting";
            case Generic3x3ContainerScreen generic3x3ContainerScreen -> "generic3x3";
            case InventoryScreen inventoryScreen -> "inventory";
            case GrindstoneScreen grindstoneScreen -> "grindstone";
            case LecternScreen lecternScreen -> "lectern";
            case LoomScreen loomScreen -> "loom";
            case SignEditScreen signEditScreen -> "sign";
            case JigsawBlockScreen jigsawBlockScreen -> "jigsaw";
            case CommandBlockScreen commandBlockScreen -> "commandblock";
            case StructureBlockScreen structureBlockScreen -> "structureblock";
            case StonecutterScreen stonecutterScreen -> "stonecutter";
            case HandledScreen<?> handledScreen -> "other";
            default -> "none";
        };
    }

    public void clickSlot(int slot, int button, String where) {
        Objects.requireNonNull(MinecraftClient.getInstance().player);
        Objects.requireNonNull(MinecraftClient.getInstance().interactionManager);

        if (where.equalsIgnoreCase("inventory")) {
            MinecraftClient.getInstance().interactionManager.clickSlot(
                MinecraftClient.getInstance().player.currentScreenHandler.syncId,
                SlotUtils.indexToId(slot),
                button,
                SlotActionType.PICKUP,
                MinecraftClient.getInstance().player
            );
        } else if (where.equalsIgnoreCase("container")) {
            MinecraftClient.getInstance().player.currentScreenHandler.onSlotClick(
                slot,
                button,
                SlotActionType.PICKUP,
                MinecraftClient.getInstance().player
            );
        } else {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }
    }

    public void moveItem(int sourceSlot, int destinationSlot, String where) {
        moveItem(sourceSlot, destinationSlot, where, false, () -> dropStack(destinationSlot, where));
    }

    public void moveItem(int sourceSlot, int destinationSlot, String where, boolean simulate) {
        moveItem(sourceSlot, destinationSlot, where, simulate, () -> dropStack(destinationSlot, where));
    }

    public void moveItem(int sourceSlot, int destinationSlot, String where, boolean simulate, Runnable replaceAction) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (where.equalsIgnoreCase("inventory")) {
            if (!Objects.requireNonNull(client.player).getInventory().getStack(destinationSlot).isEmpty()) {
                replaceAction.run();
            }
        } else if (where.equalsIgnoreCase("container")) {
            if (!Objects.requireNonNull(client.player).currentScreenHandler.slots.get(destinationSlot).getStack().isEmpty()) {
                replaceAction.run();
            }
        } else {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }

        clickSlot(sourceSlot, 0, where);
        clickSlot(destinationSlot, 0, where);

        if (simulate) {
            client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(client.player.playerScreenHandler.syncId));
        }
    }

    public void dropStack(int slot, String where) {
        MinecraftClient client = MinecraftClient.getInstance();

        Objects.requireNonNull(client.player).swingHand(Hand.MAIN_HAND);

        if (where.equalsIgnoreCase("inventory")) {
            Objects.requireNonNull(client.interactionManager).clickSlot(
                client.player.currentScreenHandler.syncId,
                SlotUtils.indexToId(slot),
                0,
                SlotActionType.THROW,
                client.player
            );
        } else if (where.equalsIgnoreCase("container")) {
            Objects.requireNonNull(client.interactionManager).clickSlot(
                client.player.currentScreenHandler.syncId,
                slot,
                0,
                SlotActionType.THROW,
                client.player
            );
        } else {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }
    }

    public List<ItemStack> getItems(String where) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (where.equalsIgnoreCase("inventory")) {
            if (client.player.getInventory() == null) return List.of();
            return client.player.getInventory().main;
        } else if (where.equalsIgnoreCase("container")) {
            if (client.player.currentScreenHandler == null) return List.of();
            return client.player.currentScreenHandler.slots.stream().map(Slot::getStack).toList();
        } else {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }
    }

    // =========
    // Item Data
    // =========
    public String getItemName(ItemStack stack) {
        return stack.getName().getString();
    }

    public int getItemCount(ItemStack stack) {
        return stack.getCount();
    }

    public int getMaxItemCount(ItemStack stack) {
        return stack.getMaxCount();
    }

    public int getItemDamage(ItemStack stack) {
        return stack.getDamage();
    }

    public int getItemMaxDamage(ItemStack stack) {
        return stack.getMaxDamage();
    }

    public ItemStack getItem(int slot, String where) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (where.equalsIgnoreCase("inventory")) {
            return client.player.getInventory().getStack(slot);
        } else if (where.equalsIgnoreCase("container")) {
            return client.player.currentScreenHandler.slots.get(slot).getStack();
        } else {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }
    }

    public void delay(long duration) throws InterruptedException {
        Thread.sleep(duration);
    }
}
