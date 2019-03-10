package com.gameservergroup.gsgcore.items;

import com.gameservergroup.gsgcore.utils.NBTItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
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
    private Consumer<PlayerBucketEmptyEvent> bucketEmptyEventConsumer;

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
        return new NBTItem(itemStack).getKeys()
                .stream()
                .map(key -> customItems.get(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public static CustomItem getCustomItem(String name) {
        return customItems.get(name);
    }

    public ItemStack getItemStack() {
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

    public Consumer<PlayerBucketEmptyEvent> getBucketEmptyEventConsumer() {
        return bucketEmptyEventConsumer;
    }

    public CustomItem setBucketEmptyEventConsumer(Consumer<PlayerBucketEmptyEvent> bucketEmptyEventConsumer) {
        this.bucketEmptyEventConsumer = bucketEmptyEventConsumer;
        return this;
    }
}
