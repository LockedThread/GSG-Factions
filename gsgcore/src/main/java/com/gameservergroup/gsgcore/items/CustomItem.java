package com.gameservergroup.gsgcore.items;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.utils.NBTItem;
import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class CustomItem {

    private static Map<String, CustomItem> CUSTOM_ITEM_MAP;

    private final String moduleName;
    private final String name;
    private ItemStack itemStack;
    private Consumer<PlayerInteractEvent> interactEventConsumer;
    private Consumer<BlockBreakEvent> breakEventConsumer;
    private Consumer<BlockPlaceEvent> placeEventConsumer;
    private ItemEdit itemEdit;

    public CustomItem(Module module, ItemStackBuilder itemStackBuilder, String name) {
        this(module, new NBTItem(itemStackBuilder.build()).set(name, true).buildItemStack(), name);
        CUSTOM_ITEM_MAP.put(name, this);
    }

    public CustomItem(Module module, ItemStack itemStack, String name) {
        this.moduleName = module.getName();
        this.name = name;
        this.itemStack = itemStack;
        CUSTOM_ITEM_MAP.put(name, this);
    }

    public static CustomItem of(Module module, ConfigurationSection section, String name) {
        return of(module, ItemStackBuilder.of(section), name);
    }

    public static CustomItem of(Module module, ConfigurationSection configurationSection) {
        return of(module, configurationSection, configurationSection.getName());
    }

    public static CustomItem of(Module module, ItemStackBuilder itemStackBuilder, String name) {
        return new CustomItem(module, new NBTItem(itemStackBuilder.build()).set(name, true).buildItemStack(), name);
    }

    public static Map<String, CustomItem> getCustomItemMap() {
        return CUSTOM_ITEM_MAP;
    }

    public static CustomItem findCustomItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getAmount() == 0 || itemStack.getType() == Material.AIR) return null;
        if (GSGCore.getInstance().getConfig().getBoolean("items-check-nbt")) {
            return new NBTItem(itemStack).getKeys()
                    .stream()
                    .map(CUSTOM_ITEM_MAP::get)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return getCustomItemMap()
                .values()
                .stream()
                .filter(customItem -> customItem.getItemStack().getType() == itemStack.getType())
                .filter(customItem -> customItem.getItemStack().hasItemMeta() && itemStack.hasItemMeta() && customItem.getItemStack().getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName())
                .filter(customItem -> customItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(Text.toColor(itemStack.getItemMeta().getDisplayName())))
                .findFirst()
                .orElse(null);
    }

    public static CustomItem getCustomItem(String name) {
        return CUSTOM_ITEM_MAP.get(name);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomItem that = (CustomItem) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public Consumer<BlockPlaceEvent> getPlaceEventConsumer() {
        return placeEventConsumer;
    }

    public CustomItem setPlaceEventConsumer(Consumer<BlockPlaceEvent> placeEventConsumer) {
        this.placeEventConsumer = placeEventConsumer;
        return this;
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

    public String getModuleName() {
        return moduleName;
    }

    public interface ItemEdit {

        ItemStack getEditedItemStack();

        default <T> ItemStack getEditedItemStack(Map<String, T> map) {
            return null;
        }
    }
}
