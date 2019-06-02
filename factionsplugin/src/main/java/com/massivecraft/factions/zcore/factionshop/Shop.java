package com.massivecraft.factions.zcore.factionshop;

import com.gameservergroup.gsgcore.menus.Menu;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

public class Shop extends Menu {

    private static final ShopEnumMap SHOP_MAP = new ShopEnumMap();

    private final ShopType shopType;
    private final ConfigurationSection section;

    public Shop(ConfigurationSection section) {
        super(section.getString("title"), section.getInt("size"));
        this.shopType = getShopType(section.getString("shop-type"));
        this.section = section;
        initialize();
    }

    public static Map<ShopType, Shop> getShopMap() {
        return SHOP_MAP;
    }

    public static Optional<Shop> getShop(ShopType shopType) {
        return Optional.ofNullable(SHOP_MAP.get(shopType));
    }

    public static Optional<Shop> getShop(String shopName) {
        return getShop(ShopType.valueOf(shopName.toUpperCase().replace(" ", "_").replace("-", "_")));
    }

    public static ShopType getShopType(String toUpperCase) {
        try {
            return ShopType.valueOf(toUpperCase.toUpperCase().replace(" ", "_").replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void initialize() {
        ConfigurationSection items = section.getConfigurationSection("items");
        for (String key : items.getKeys(false)) {
            ShopItem shopItem = new ShopItem(items.getConfigurationSection(key));
            shopItem.setInventoryClickEventConsumer(event -> {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) event.getWhoClicked());
                Faction faction = fPlayer.getFaction();
                event.setCancelled(true);
                if (faction.getPoints() >= shopItem.getPoints()) {
                    Long aLong = faction.getShopCooldown().get(shopItem.getName());
                    if (aLong != null) {
                        if (System.currentTimeMillis() >= aLong) {
                            faction.getShopCooldown().remove(shopItem.getName());
                        } else {
                            return;
                        }
                    }

                    shopItem.execute(faction, (Player) event.getWhoClicked());
                }
            });
            setItem(items.getInt(key + ".slot"), shopItem);
        }
    }

    public ShopType getShopType() {
        return shopType;
    }
}
