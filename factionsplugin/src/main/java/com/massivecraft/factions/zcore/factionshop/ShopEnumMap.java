package com.massivecraft.factions.zcore.factionshop;

import com.massivecraft.factions.P;

import java.util.EnumMap;

public class ShopEnumMap extends EnumMap<ShopType, Shop> {

    public ShopEnumMap() {
        super(ShopType.class);
    }

    @Override
    public Shop get(Object key) {
        if (key instanceof ShopType) {
            ShopType shopType = (ShopType) key;
            Shop shop = super.get(shopType);

            if (shop != null) {
                return shop;
            } else if (shopType == ShopType.BLACK_MARKET) {
                shop = new Shop(P.p.getConfig().getConfigurationSection("faction-shops.black-market"));
            } else if (shopType == ShopType.CATALOG) {
                shop = new Shop(P.p.getConfig().getConfigurationSection("faction-shops.catalog"));
            } else if (shopType == ShopType.DEFENSIVE) {
                shop = new Shop(P.p.getConfig().getConfigurationSection("faction-shops.defensive-shop"));
            } else if (shopType == ShopType.OFFENSIVE) {
                shop = new Shop(P.p.getConfig().getConfigurationSection("faction-shops.offensive-shop"));
            }
            put(shopType, shop);
            return shop;
        }
        return super.get(key);
    }
}
