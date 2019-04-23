package com.massivecraft.factions.zcore.factionupgrades;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.units.UnitFactionUpgrade;
import com.massivecraft.factions.zcore.util.TL;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum FactionUpgrade {

    CHUNK_SPAWNER_LIMIT;

    private transient boolean enabled = false;
    private transient int slot;
    private transient Map<String, Object> metadata;
    private transient Int2ObjectOpenHashMap<Pair<CostType, Double>> upgradeCosts;
    private transient MenuItem menuItem;

    public static FactionUpgrade parse(String s) {
        try {
            return valueOf(s.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Pair<CostType, Double> getCostPair(int index) {
        return upgradeCosts.get(index);
    }

    public void loadValues(ConfigurationSection root) {
        if (this.enabled = root.getBoolean("enabled")) {
            this.upgradeCosts = new Int2ObjectOpenHashMap<>();
            this.metadata = new HashMap<>();
            this.slot = root.getInt("slot");
            this.menuItem = MenuItem.of(ItemStackBuilder.of(root.getConfigurationSection("gui-item")).build()).setInventoryClickEventConsumer(event -> {
                Player player = (Player) event.getWhoClicked();
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                Faction faction = fPlayer.getFaction();
                Integer integer = faction.getUpgrades().get(FactionUpgrade.this);
                if (integer == null) {
                    Pair<CostType, Double> costPair = getCostPair(0);
                    if (costPair.getLeft().purchase(player, costPair.getRight())) {
                        for (FPlayer fPlayer1 : faction.getFPlayersWhereOnline(true)) {
                            if (fPlayer1.isViewingUpgradeMenu()) {
                                Player player1 = fPlayer1.getPlayer();
                                Menu menu = (Menu) player1.getOpenInventory().getTopInventory().getHolder();
                                menu.clear();
                                menu.initialize();
                                player1.updateInventory();
                            }
                        }
                        player.playSound(player.getLocation(), UnitFactionUpgrade.upgraded.getLeft(), UnitFactionUpgrade.upgraded.getMiddle(), UnitFactionUpgrade.upgraded.getRight());
                        faction.getUpgrades().computeIfPresent(this, (factionUpgrade, integer1) -> integer1 + 1);
                    } else {
                        player.sendMessage(TL.FACTION_UPGRADES_CANT_AFFORD.format(1, getPrettyName(), costPair.getRight()));
                    }
                } else if (integer >= getLevels()) {
                    player.playSound(player.getLocation(), UnitFactionUpgrade.cantAfford.getLeft(), UnitFactionUpgrade.cantAfford.getMiddle(), UnitFactionUpgrade.cantAfford.getRight());
                    player.sendMessage(TL.FACTION_UPGRADES_CANT_MAX_LEVEL.toString());
                    player.closeInventory();
                } else {
                    int nextUpgrade = integer + 1;
                    Pair<CostType, Double> costPair = getCostPair(nextUpgrade);
                    if (costPair.getLeft().purchase(player, costPair.getRight())) {
                        for (FPlayer fPlayer1 : faction.getFPlayersWhereOnline(true)) {
                            if (fPlayer1.isViewingUpgradeMenu()) {
                                Player player1 = fPlayer1.getPlayer();
                                Menu menu = (Menu) player1.getOpenInventory().getTopInventory().getHolder();
                                menu.clear();
                                menu.initialize();
                                player1.updateInventory();
                            }
                        }
                        player.playSound(player.getLocation(), UnitFactionUpgrade.upgraded.getLeft(), UnitFactionUpgrade.upgraded.getMiddle(), UnitFactionUpgrade.upgraded.getRight());
                        faction.getUpgrades().computeIfPresent(this, (factionUpgrade, integer1) -> integer1 + 1);
                    } else {
                        player.sendMessage(TL.FACTION_UPGRADES_CANT_AFFORD.format(1, getPrettyName(), costPair.getRight()));
                    }
                }
            });
            ConfigurationSection levels = root.getConfigurationSection("levels");
            for (String key : levels.getKeys(false)) {
                ConfigurationSection level = levels.getConfigurationSection(key);
                for (String levelKey : level.getKeys(false)) {
                    if (levelKey.equalsIgnoreCase("cost")) {
                        ConfigurationSection cost = level.getConfigurationSection("cost");
                        CostType costType = CostType.fromString(cost.getString("cost-type"));
                        if (costType == null) {
                            throw new RuntimeException("Unable to parse \"" + cost.getString("cost-type") + "\" as a CostType.");
                        }
                        upgradeCosts.put(key.equalsIgnoreCase("default") ? 0 : Integer.parseInt(key), Pair.of(costType, cost.getDouble("amount")));
                    } else {
                        metadata.put(key + "-" + levelKey.toLowerCase(), level.get(levelKey));
                    }
                }
            }
        }
    }

    public int getLevels() {
        return upgradeCosts.size();
    }

    public void setMeta(String key, Object value) {
        metadata.put(key, value);
    }

    public String getMetaString(String key) {
        return String.valueOf(metadata.get(key));
    }

    public int getMetaInteger(String key) {
        return Integer.parseInt(String.valueOf(metadata.get(key)));
    }

    public boolean getMetaBoolean(String key) {
        return Boolean.parseBoolean(String.valueOf(metadata.get(key)));
    }

    public boolean isMetaBoolean(String key) {
        return metadata.get(key) instanceof Boolean;
    }

    public boolean isMetaInteger(String key) {
        return metadata.get(key) instanceof Integer;
    }

    public boolean isMetaDouble(String key) {
        return metadata.get(key) instanceof Double;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getSlot() {
        return slot;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Int2ObjectOpenHashMap<Pair<CostType, Double>> getUpgradeCosts() {
        return upgradeCosts;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public MenuItem getMenuItem(Faction faction) {
        ItemStack clone = menuItem.getItemStack().clone();
        ItemMeta itemMeta = clone.getItemMeta();
        List<String> lore = itemMeta.getLore()
                .stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s.replace("{level}", String.valueOf(faction.getUpgrades().getOrDefault(this, 1)))))
                .collect(Collectors.toList());
        itemMeta.setLore(lore);
        clone.setItemMeta(itemMeta);
        return MenuItem.of(clone).setInventoryClickEventConsumer(menuItem.getInventoryClickEventConsumer());
    }

    public String getPrettyName() {
        return StringUtils.capitalize(name().replace("_", " ").toLowerCase());
    }
}
