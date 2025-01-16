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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Objects;

public class ScriptingStandardLibrary {

    private final MinecraftClient client = MinecraftClient.getInstance();

    // ======================
    // Logging to Chat HUD
    // ======================
    public void info(String message) {
        Objects.requireNonNull(client.inGameHud.getChatHud()).addMessage(Text.literal(message).formatted(Formatting.WHITE));
    }

    public void warn(String message) {
        Objects.requireNonNull(client.inGameHud.getChatHud()).addMessage(Text.literal(message).formatted(Formatting.YELLOW));
    }

    public void error(String message) {
        Objects.requireNonNull(client.inGameHud.getChatHud()).addMessage(Text.literal(message).formatted(Formatting.RED));
    }

    // =====================
    // Command and Chat
    // =====================
    public void command(String command) {
        Objects.requireNonNull(client.getNetworkHandler()).sendCommand(command);
    }

    public void chat(String message) {
        Objects.requireNonNull(client.getNetworkHandler()).sendChatMessage(message);
    }

    // =====================
    // Inventory and Slots
    // =====================
    public boolean isItemInSlot(int slot, String item, String where) {
        Objects.requireNonNull(client.player);

        ItemStack stack = getItem(slot, where);
        return stack.getItem().getName().getString().equalsIgnoreCase(item);
    }

    public String getOpenContainer() {
        Screen screen = client.currentScreen;
        if (screen == null) return "none";

        return switch (screen) {
            case GenericContainerScreen ignored -> "generic";
            case ShulkerBoxScreen ignored -> "shulker";
            case FurnaceScreen ignored -> "furnace";
            case HopperScreen ignored -> "hopper";
            case AnvilScreen ignored -> "anvil";
            case CrafterScreen ignored -> "crafter";
            case CraftingScreen ignored -> "crafting";
            case Generic3x3ContainerScreen ignored -> "generic3x3";
            case InventoryScreen ignored -> "inventory";
            case GrindstoneScreen ignored -> "grindstone";
            case LecternScreen ignored -> "lectern";
            case LoomScreen ignored -> "loom";
            case SignEditScreen ignored -> "sign";
            case JigsawBlockScreen ignored -> "jigsaw";
            case CommandBlockScreen ignored -> "commandblock";
            case StructureBlockScreen ignored -> "structureblock";
            case StonecutterScreen ignored -> "stonecutter";
            case HandledScreen<?> ignored -> "other";
            default -> "none";
        };
    }

    public void clickSlot(int slot, int button, String where) {
        Objects.requireNonNull(client.player);
        Objects.requireNonNull(client.interactionManager);

        boolean isInventory = where.equalsIgnoreCase("inventory");
        boolean isContainer = where.equalsIgnoreCase("container");

        if (!isInventory && !isContainer) {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }

        int syncId = client.player.currentScreenHandler.syncId;
        int slotId = isInventory ? SlotUtils.indexToId(slot) : slot;

        if (!validateSlot(client.player, slotId)) {
            throw new IllegalArgumentException("Invalid slot index: " + slot);
        }

        client.interactionManager.clickSlot(syncId, slotId, button, SlotActionType.PICKUP, client.player);
    }

    public void moveItem(int sourceSlot, int destinationSlot, String where) {
        Objects.requireNonNull(client.player);

        if (!isSlotEmpty(destinationSlot, where)) {
            dropStack(destinationSlot, true, where);
        }

        clickSlot(sourceSlot, 0, where);
        clickSlot(destinationSlot, 0, where);
    }

    public void dropStack(int slot, boolean wholeStack, String where) {
        Objects.requireNonNull(client.player).swingHand(Hand.MAIN_HAND);

        int syncId = client.player.currentScreenHandler.syncId;
        int slotId = where.equalsIgnoreCase("inventory") ? SlotUtils.indexToId(slot) : slot;

        Objects.requireNonNull(client.interactionManager).clickSlot(syncId, slotId, wholeStack ? 1 : 0, SlotActionType.THROW, client.player);
    }

    public List<ItemStack> getItems(String where) {
        if (where.equalsIgnoreCase("inventory")) {
            return Objects.requireNonNull(client.player).getInventory().main;
        } else if (where.equalsIgnoreCase("container")) {
            return Objects.requireNonNull(client.player.currentScreenHandler).slots.stream().map(Slot::getStack).toList();
        } else {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }
    }

    // =================
    // Item Data Helpers
    // =================
    public String getItemName(ItemStack stack) {
        return stack.getName().getString();
    }

    public int getItemCount(ItemStack stack) {
        return stack.getCount();
    }

    public ItemStack getItem(int slot, String where) {
        if (where.equalsIgnoreCase("inventory")) {
            return Objects.requireNonNull(client.player).getInventory().getStack(slot);
        } else if (where.equalsIgnoreCase("container")) {
            return Objects.requireNonNull(client.player.currentScreenHandler).slots.get(slot).getStack();
        } else {
            throw new IllegalArgumentException("Invalid 'where' parameter. Must be 'inventory' or 'container'.");
        }
    }

    public int getFirstSlotFromStack(ItemStack stack, String where) {
        List<ItemStack> items = getItems(where);

        for (int i = 0; i < items.size(); i++) {
            if (ItemStack.areEqual(stack, items.get(i))) {
                return i;
            }
        }

        return -1; // Return -1 if the item is not found
    }

    public boolean isSlotEmpty(int slot, String where) {
        return getItem(slot, where).isEmpty();
    }

    // ===========
    // Validation
    // ===========
    private boolean validateSlot(ClientPlayerEntity player, int slot) {
        return slot >= 0 && slot < player.currentScreenHandler.slots.size();
    }
}
