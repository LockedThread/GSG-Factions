package com.gameservergroup.gsgcore.items;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.utils.NBTItem;
import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class CustomItem {

    private static HashMap<String, CustomItem> customItems = new HashMap<>();
    private String name;
    private ItemStack itemStack;
    private Consumer<PlayerInteractEvent> interactEventConsumer;
    private Consumer<BlockBreakEvent> breakEventConsumer;
    private Consumer<BlockPlaceEvent> placeEventConsumer;
    private ItemEdit itemEdit;

    public CustomItem(CustomItem customItem) {
        this.name = customItem.getName();
        this.itemStack = customItem.getItemStack();
        this.interactEventConsumer = customItem.getInteractEventConsumer();
        this.breakEventConsumer = customItem.getBreakEventConsumer();
        this.placeEventConsumer = customItem.getPlaceEventConsumer();
        this.itemEdit = customItem.getItemEdit();
        customItems.put(name, this);
    }

    private CustomItem(String name, ItemStack itemStack) {
        this.name = name;
        this.itemStack = itemStack;
        customItems.put(name, this);
    }

    public static CustomItem of(ConfigurationSection section, String name) {
        return of(ItemStackBuilder.of(section), name);
    }

    public static CustomItem of(ConfigurationSection configurationSection) {
        return of(configurationSection, configurationSection.getName());
    }

    public static CustomItem of(ItemStackBuilder itemStackBuilder, String name) {
        return new CustomItem(name, new NBTItem(itemStackBuilder.build()).set(name, true).buildItemStack());
    }

    public static HashMap<String, CustomItem> getCustomItems() {
        return customItems;
    }

    public static CustomItem findCustomItem(ItemStack itemStack) {
        if (GSGCore.getInstance().getConfig().getBoolean("items-check-nbt")) {
            return new NBTItem(itemStack).getKeys()
                    .stream()
                    .map(key -> customItems.get(key))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return getCustomItems()
                .values()
                .stream()
                .filter(customItem -> customItem.getItemStack().getType() == itemStack.getType())
                .filter(customItem -> customItem.getItemStack().hasItemMeta() && itemStack.hasItemMeta() && customItem.getItemStack().getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName())
                .filter(customItem -> customItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(Text.toColor(itemStack.getItemMeta().getDisplayName())))
                .findFirst()
                .orElse(null);
    }

    public static CustomItem getCustomItem(String name) {
        return customItems.get(name);
    }

    public ItemStack getItemStack() {
        return itemEdit != null ? itemEdit.getEditedItemStack() : itemStack;
    }

    public ItemStack getOriginalItemStack() {
        return itemStack;
    }

    public String getName() {
        return name;
    }

    public Consumer<PlayerInteractEvent> getInteractEventConsumer() {
        return interactEventConsumer;
    }

    public CustomItem setInteractEventConsumer(Consumer<PlayerInteractEvent> interactEventConsumer) {
        this.interactEventConsumer = interactEventConsumer;
        return this;
    }

    public Consumer<BlockBreakEvent> getBreakEventConsumer() {
        return breakEventConsumer;
    }

    public CustomItem setBreakEventConsumer(Consumer<BlockBreakEvent> breakEventConsumer) {
        this.breakEventConsumer = breakEventConsumer;
        return this;
    }

    public Consumer<BlockPlaceEvent> getPlaceEventConsumer() {
        return placeEventConsumer;
    }

    public CustomItem setPlaceEventConsumer(Consumer<BlockPlaceEvent> placeEventConsumer) {
        this.placeEventConsumer = placeEventConsumer;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomItem that = (CustomItem) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(itemStack, that.itemStack) &&
                Objects.equals(interactEventConsumer, that.interactEventConsumer) &&
                Objects.equals(breakEventConsumer, that.breakEventConsumer) &&
                Objects.equals(placeEventConsumer, that.placeEventConsumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, itemStack, interactEventConsumer, breakEventConsumer, placeEventConsumer);
    }

    @Override
    public String toString() {
        return "CustomItem{" +
                "name='" + name + '\'' +
                ", itemStack=" + itemStack +
                ", interactEventConsumer=" + interactEventConsumer +
                ", breakEventConsumer=" + breakEventConsumer +
                ", placeEventConsumer=" + placeEventConsumer +
                '}';
    }

    public ItemEdit getItemEdit() {
        return itemEdit;
    }

    public void setItemEdit(ItemEdit itemEdit) {
        this.itemEdit = itemEdit;
    }

    public interface ItemEdit {

        ItemStack getEditedItemStack();

    }
}
