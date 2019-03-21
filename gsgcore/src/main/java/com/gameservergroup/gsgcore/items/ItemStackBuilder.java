package com.gameservergroup.gsgcore.items;

import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemStackBuilder {

    private ItemStack itemStack;

    private ItemStackBuilder() {
        this.itemStack = new ItemStack(Material.WOOD, 1);
    }

    private ItemStackBuilder(Material material) {
        this.itemStack = new ItemStack(material, 1);
    }

    private ItemStackBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    private ItemStackBuilder(Material material, DyeColor dyeColor) {
        this.itemStack = new ItemStack(material, material == Material.WOOL ? dyeColor.getWoolData() : dyeColor.getDyeData());
    }

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static ItemStackBuilder of(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack);
    }

    public static ItemStackBuilder of(ConfigurationSection section) {
        ItemStackBuilder itemStackBuilder = section.isSet("material") ? ItemStackBuilder.of(Material.matchMaterial(section.getString("material"))) : new ItemStackBuilder();
        if (section.isSet("amount")) {
            if (section.isInt("amount")) {
                itemStackBuilder.setAmount(section.getInt("amount"));
            } else {
                throw new RuntimeException(section.getCurrentPath() + ".amount can't be parsed as an integer");
            }
        }
        if (section.isSet("data")) {
            if (section.isString("data")) {
                final DyeColor dyeColor = parseDyeColor(section.getString("data"));
                if (dyeColor != null) {
                    itemStackBuilder.setDyeColor(dyeColor);
                } else {
                    itemStackBuilder.setData(Short.parseShort(section.getString("data")));
                }
            } else {
                itemStackBuilder.setData((short) section.getInt("data"));
            }
        }
        if (section.isSet("color")) {
            if (section.isColor("color")) {
                itemStackBuilder.setColor(section.getColor("color"));
            } else {
                throw new RuntimeException(section.getCurrentPath() + ".color can't be parsed as a org.bukkit.Color");
            }
        }
        if (section.isSet("name") && section.isString("name")) {
            itemStackBuilder.setDisplayName(section.getString("name"));
        }
        if (section.isSet("lore") && section.isList("lore")) {
            itemStackBuilder.setLore(section.getStringList("lore"));
        }
        if (section.isSet("itemflags") && section.isList("itemflags")) {
            itemStackBuilder.addItemFlags(section.getStringList("itemflags").stream().map(ItemStackBuilder::parseItemFlag).toArray(ItemFlag[]::new));
        }
        if (section.isSet("enchants") && section.isList("enchants")) {
            for (String enchants : section.getStringList("enchants")) {
                String[] split = enchants.split(":");
                itemStackBuilder.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]));
            }
        }
        if (section.isSet("unbreakable") && section.isBoolean("unbreakable")) {
            itemStackBuilder.setUnbreakable(section.getBoolean("unbreakable"));
        }
        return itemStackBuilder;
    }

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(material);
    }

    private static DyeColor parseDyeColor(String s) {
        try {
            return DyeColor.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to parse " + s + " as a Bukkit DyeColor", e);
        }
    }

    private static ItemFlag parseItemFlag(String s) {
        try {
            return ItemFlag.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to parse " + s + " as a Bukkit DyeColor", e);
        }
    }

    public ItemStackBuilder setUnbreakable(boolean b) {
        itemStack.getItemMeta().spigot().setUnbreakable(b);
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemStackBuilder addEnchant(Enchantment enchantment, int level) {
        consumeItemMeta(itemMeta -> itemMeta.addEnchant(enchantment, level, true));
        return this;
    }

    public ItemStackBuilder consumeItemStack(Consumer<ItemStack> itemStackConsumer) {
        itemStackConsumer.accept(itemStack);
        return this;
    }

    public ItemStackBuilder consumeItemMeta(Consumer<ItemMeta> itemMetaConsumer) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMetaConsumer.accept(itemMeta);
            itemStack.setItemMeta(itemMeta);
        }
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
