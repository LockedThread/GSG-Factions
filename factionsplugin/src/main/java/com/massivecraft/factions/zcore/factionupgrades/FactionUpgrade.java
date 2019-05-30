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

public enum FactionUpgrade implements FactionUpgradeUpdate {

    CHUNK_SPAWNER_LIMIT,
    FACTION_TNTBANK_STORAGE,
    FACTION_MEMBER_LIMIT,
    FACTION_CHEST_ROWS,
    FACTION_WARP_LIMIT,
    MAX_PLAYER_POWER,
    SPAWNER_SPAWN_RATE;

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

    public static FactionUpgrade getFactionUpgradeBySlot(int slot) {
        for (FactionUpgrade factionUpgrade : values()) {
            if (factionUpgrade.getSlot() == slot) {
                return factionUpgrade;
            }
        }
        return null;
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
        return (int) metadata.get(key);
    }

    public double getMetaDouble(String key) {
        return (double) metadata.get(key);
    }

    public boolean getMetaBoolean(String key) {
        return (boolean) metadata.get(key);
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

    @Override
    public void update(Faction faction, int newLevel) {
        switch (this) {
            case FACTION_TNTBANK_STORAGE:
                faction.setTntBankLimit(getMetaInteger(newLevel + "-amount"));
                break;
            case FACTION_MEMBER_LIMIT:
                faction.setMaxMembers(getMetaInteger(newLevel + "-members"));
                break;
            case FACTION_CHEST_ROWS:
                faction.getFactionChest().setRows(getMetaInteger(newLevel + "-rows"));
                break;
            case FACTION_WARP_LIMIT:
                faction.setMaxWarps(getMetaInteger(newLevel + "-amount"));
                break;
            default:
                break;
        }
    }

    public String getPrettyName() {
        return StringUtils.capitalize(name().replace("_", " ").toLowerCase());
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
                event.setCancelled(true);
                if (integer == null) {
                    Pair<CostType, Double> costPair = getCostPair(1);

                    if (costPair.getLeft().purchase(player, costPair.getRight())) {
                        player.playSound(player.getLocation(), UnitFactionUpgrade.upgraded.getLeft(), UnitFactionUpgrade.upgraded.getMiddle(), UnitFactionUpgrade.upgraded.getRight());
                        faction.getUpgrades().put(this, 1);
                        Menu menu = (Menu) event.getClickedInventory().getHolder();
                        //noinspection ConstantConditions
                        menu.setItem(event.getRawSlot(), getFactionUpgradeBySlot(event.getRawSlot()).getMenuItem(faction));
                        update(faction, 1);
                    } else {
                        player.sendMessage(TL.FACTION_UPGRADES_CANT_AFFORD.toString().replace("{level}", String.valueOf(1)).replace("{upgrade}", getPrettyName()).replace("{cost}", String.valueOf(costPair.getRight())));
                    }
                } else if (integer >= getLevels()) {
                    player.playSound(player.getLocation(), UnitFactionUpgrade.cantAfford.getLeft(), UnitFactionUpgrade.cantAfford.getMiddle(), UnitFactionUpgrade.cantAfford.getRight());
                    player.sendMessage(TL.FACTION_UPGRADES_CANT_MAX_LEVEL.toString());
                } else {
                    int nextUpgrade = integer + 1;
                    Pair<CostType, Double> costPair = getCostPair(nextUpgrade);
                    if (costPair.getLeft().purchase(player, costPair.getRight())) {
                        faction.getUpgrades().put(this, nextUpgrade);
                        player.playSound(player.getLocation(), UnitFactionUpgrade.upgraded.getLeft(), UnitFactionUpgrade.upgraded.getMiddle(), UnitFactionUpgrade.upgraded.getRight());
                        Menu menu = (Menu) event.getClickedInventory().getHolder();
                        //noinspection ConstantConditions
                        menu.setItem(event.getRawSlot(), getFactionUpgradeBySlot(event.getRawSlot()).getMenuItem(faction));
                        update(faction, nextUpgrade);
                    } else {
                        player.sendMessage(TL.FACTION_UPGRADES_CANT_AFFORD.toString().replace("{level}", String.valueOf(1)).replace("{upgrade}", getPrettyName()).replace("{cost}", String.valueOf(costPair.getRight())));
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

    public MenuItem getMenuItem(Faction faction) {
        ItemStack clone = menuItem.getItemStack().clone();
        ItemMeta itemMeta = clone.getItemMeta();
        List<String> lore = itemMeta.getLore()
                .stream()
                .map(s -> {
                    Integer integer = faction.getUpgrades().getOrDefault(this, 0);
                    return ChatColor.translateAlternateColorCodes('&', s.replace("{level}", integer == 0 ? "default" : String.valueOf(integer)));
                })
                .collect(Collectors.toList());
        itemMeta.setLore(lore);
        clone.setItemMeta(itemMeta);
        return MenuItem.of(clone).setInventoryClickEventConsumer(menuItem.getInventoryClickEventConsumer());
    }

    public Pair<CostType, Double> getCostPair(int index) {
        return upgradeCosts.get(index);
    }
}
