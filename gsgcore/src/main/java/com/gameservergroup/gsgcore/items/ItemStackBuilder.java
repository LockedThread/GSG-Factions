package com.gameservergroup.gsgcore.items;

import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemStackBuilder {

    private ItemStack itemStack;

    private ItemStackBuilder(Material material) {
        this.itemStack = new ItemStack(material, 1);
    }

    private ItemStackBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    private ItemStackBuilder(Material material, DyeColor dyeColor) {
        this.itemStack = new ItemStack(material, material == Material.WOOL ? dyeColor.getWoolData() : dyeColor.getDyeData());
    }

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(material);
    }

    public void setAmount(int amount) {
        itemStack.setAmount(amount);
    }

    public ItemStackBuilder consumeItemStack(Consumer<ItemStack> itemStackConsumer) {
        itemStackConsumer.accept(itemStack);
        return this;
    }

    public ItemStackBuilder consumeItemMeta(Consumer<ItemMeta> itemMetaConsumer) {
        itemMetaConsumer.accept(itemStack.getItemMeta());
        itemStack.setItemMeta(itemStack.getItemMeta());
        return this;
    }

    public ItemStackBuilder setColor(Color color) {
        return consumeItemMeta(itemMeta -> ((LeatherArmorMeta) itemStack.getItemMeta()).setColor(color));
    }

    public ItemStackBuilder setData(short data) {
        itemStack.setDurability(data);
        return this;
    }

    public ItemStackBuilder setMaterial(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemStackBuilder setDyeColor(DyeColor dyeColor) {
        itemStack.setDurability(itemStack.getType() == Material.WOOL ? dyeColor.getWoolData() : dyeColor.getDyeData());
        return this;
    }

    public ItemStackBuilder setDisplayName(String displayName) {
        consumeItemMeta(itemMeta -> itemMeta.setDisplayName(Text.toColor(displayName)));
        return this;
    }

    public ItemStackBuilder setLore(Collection<String> strings) {
        consumeItemMeta(itemMeta -> itemMeta.setLore(strings.stream().map(Text::toColor).collect(Collectors.toList())));
        return this;
    }

    public ItemStackBuilder addItemFlags(ItemFlag... itemFlags) {
        consumeItemMeta(itemMeta -> itemMeta.addItemFlags(itemFlags));
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }


}
