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

    public boolean isItemInSlot(int slot, String item) {
        Objects.requireNonNull(MinecraftClient.getInstance().player);
        Objects.requireNonNull(MinecraftClient.getInstance().player.getInventory());

        return MinecraftClient.getInstance().player.getInventory().getStack(slot).getItem().getName().getString().equalsIgnoreCase(item);
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

    public void click(int slot, int button) {
        Objects.requireNonNull(MinecraftClient.getInstance().player);
        Objects.requireNonNull(MinecraftClient.getInstance().interactionManager);
        MinecraftClient.getInstance().interactionManager
            .clickSlot(
                MinecraftClient.getInstance().player.currentScreenHandler.syncId,
                SlotUtils.indexToId(slot),
                button,
                SlotActionType.PICKUP,
                MinecraftClient.getInstance().player
            );
    }


    public void moveItem(int sourceSlot, int destinationSlot) {
        moveItem(sourceSlot, destinationSlot, false, () -> dropStack(destinationSlot));
    }

    public void moveItem(int sourceSlot, int destinationSlot, boolean simulate) {
        moveItem(sourceSlot, destinationSlot, simulate, () -> dropStack(destinationSlot));
    }

    public void moveItem(int sourceSlot, int destinationSlot, boolean simulate, Runnable replaceAction) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!Objects.requireNonNull(client.player).getInventory().getStack(destinationSlot).isEmpty()) {
            replaceAction.run();
        }

        click(SlotUtils.indexToId(sourceSlot), 0);
        click(SlotUtils.indexToId(destinationSlot), 0);

        if (simulate) {
            client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(client.player.playerScreenHandler.syncId));
        }
    }

    public void dropStack(int slot) {
        MinecraftClient client = MinecraftClient.getInstance();

        Objects.requireNonNull(client.player).swingHand(Hand.MAIN_HAND);

        Objects.requireNonNull(client.interactionManager).clickSlot(
            client.player.currentScreenHandler.syncId,
            SlotUtils.indexToId(slot),
            0,
            SlotActionType.THROW,
            client.player
        );
    }

    public List<ItemStack> getInventoryItems() {
        if (MinecraftClient.getInstance().player.getInventory() == null) return List.of();
        return MinecraftClient.getInstance().player.getInventory().main;
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

    public ItemStack getPlayerItem(int slot) {
        return MinecraftClient.getInstance().player.getInventory().getStack(slot);
    }

    public ItemStack getContainerItem(int slot) {
        return MinecraftClient.getInstance().player.currentScreenHandler.slots.get(slot).getStack();
    }

    public void delay(long duration) throws InterruptedException {
        Thread.sleep(duration);
    }

}
