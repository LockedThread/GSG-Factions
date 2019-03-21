package com.gameservergroup.gsgcollectors.enums;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;

import java.util.stream.Collectors;

public enum CollectionType {
    /*Items*/
    CACTUS(Material.CACTUS),
    SUGAR_CANE(Material.SUGAR_CANE),
    TNT(Material.TNT),

    /*Entity Types*/
    CREEPER(Material.TNT),
    SKELETON(EntityType.SKELETON),
    SPIDER(EntityType.SPIDER),
    GIANT(EntityType.GIANT),
    ZOMBIE(EntityType.ZOMBIE),
    SLIME(EntityType.SLIME),
    GHAST(EntityType.GHAST),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE),
    ENDERMAN(EntityType.ENDERMAN),
    CAVE_SPIDER(EntityType.CAVE_SPIDER),
    SILVERFISH(EntityType.SILVERFISH),
    BLAZE(EntityType.BLAZE),
    MAGMA_CUBE(EntityType.MAGMA_CUBE),
    ENDER_DRAGON(EntityType.ENDER_DRAGON),
    WITHER(EntityType.WITHER),
    BAT(EntityType.BAT),
    WITCH(EntityType.WITCH),
    ENDERMITE(EntityType.ENDERMITE),
    GUARDIAN(EntityType.GUARDIAN),
    PIG(EntityType.PIG),
    SHEEP(EntityType.SHEEP),
    COW(EntityType.COW),
    CHICKEN(EntityType.CHICKEN),
    SQUID(EntityType.SQUID),
    WOLF(EntityType.WOLF),
    MUSHROOM_COW(EntityType.MUSHROOM_COW),
    SNOWMAN(EntityType.SNOWMAN),
    OCELOT(EntityType.OCELOT),
    IRON_GOLEM(EntityType.IRON_GOLEM),
    HORSE(EntityType.HORSE),
    RABBIT(EntityType.RABBIT),
    VILLAGER(EntityType.VILLAGER),
    ENDER_CRYSTAL(EntityType.ENDER_CRYSTAL);

    private Material material = null;
    private EntityType entityType = null;
    private ItemStack itemStack;
    private int guiSlot = -1;
    private double price;

    CollectionType(EntityType entityType) {
        this.entityType = entityType;
    }

    CollectionType(Material material) {
        this.material = material;
    }

    public static CollectionType fromEntityType(EntityType entityType) {
        if (entityType == EntityType.CREEPER) {
            return TNT;
        }
        try {
            return valueOf(entityType.name());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Material getMaterial() {
        if (this == CREEPER || this == TNT) {
            return Material.TNT;
        }
        return material;
    }

    public EntityType getEntityType() {
        if (this == CREEPER || this == TNT) {
            return EntityType.CREEPER;
        }
        return entityType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getGuiSlot() {
        return guiSlot;
    }

    public void setGuiSlot(int guiSlot) {
        this.guiSlot = guiSlot;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void init(String s) {
        this.guiSlot = GSGCollectors.getInstance().getConfig().getInt("collection-types." + s + ".slot");
        this.price = GSGCollectors.getInstance().getConfig().getDouble("collection-types." + s + ".price");
        if (getMaterial() == null) {
            SpawnEgg spawnEgg = new SpawnEgg(getEntityType());
            this.itemStack = ItemStackBuilder.of(GSGCollectors.getInstance().getConfig().getConfigurationSection("menu.collection-type-format"))
                    .consumeItemMeta(itemMeta -> itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{mob}", StringUtils.capitalize(spawnEgg.getSpawnedType().name().replace("_", " ").toLowerCase()))))
                    .setMaterial(spawnEgg.getItemType()).setData(spawnEgg.getData())
                    .build();
        } else {
            this.itemStack = ItemStackBuilder.of(GSGCollectors.getInstance().getConfig().getConfigurationSection("menu.collection-type-format"))
                    .consumeItemMeta(itemMeta -> itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{mob}", StringUtils.capitalize(getMaterial().name().replace("_", " ").toLowerCase()))))
                    .setMaterial(getMaterial())
                    .build();
        }
    }

    public ItemStack buildItemStack(int amount) {
        ItemStack item = itemStack.clone();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(itemMeta.getLore().stream().map(s -> s.replace("{amount}", String.valueOf(amount))).collect(Collectors.toList()));
        item.setItemMeta(itemMeta);
        return item;
    }


}
