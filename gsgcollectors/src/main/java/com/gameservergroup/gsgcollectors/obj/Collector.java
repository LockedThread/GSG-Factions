package com.gameservergroup.gsgcollectors.obj;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.enums.CollectionType;
import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
import com.gameservergroup.gsgcollectors.menus.MenuCollector;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.util.Arrays;
import java.util.EnumMap;

public class Collector {

    private BlockPosition blockPosition;
    private EnumMap<CollectionType, Integer> amounts;
    private String landOwner;
    private transient MenuCollector menuCollector;

    public Collector(Location location) {
        this(BlockPosition.of(location));
    }

    public Collector(BlockPosition blockPosition) {
        this.blockPosition = blockPosition;
    }

    public void sellAll(Player player, CollectionType collectionType) {
        double money = collectionType.getPrice() * getAmounts().getOrDefault(collectionType, 0);
        if (money > 0.0) {
            if (GSGCollectors.getInstance().getUnitCollectors().isUseTitles()) {
                player.sendTitle(Title.builder().title(CollectorMessages.TITLE_SELL.toString().replace("{money}", String.valueOf(money))).fadeIn(5).fadeOut(5).stay(25).build());
            } else {
                player.sendMessage(CollectorMessages.TITLE_SELL.toString().replace("{money}", String.valueOf(money)));
            }
        }
        reset(collectionType);
    }

    public void sellAll(Player player) {
        double money = getAmounts().entrySet().stream().filter(entry -> entry.getKey() != CollectionType.TNT).mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue()).sum();
        if (money > 0.0) {
            Module.getEconomy().depositPlayer(player, money);
            if (GSGCollectors.getInstance().getUnitCollectors().isUseTitles()) {
                player.sendTitle(Title.builder().title(CollectorMessages.TITLE_SELL.toString().replace("{money}", String.valueOf(money))).fadeIn(5).fadeOut(5).stay(25).build());
            } else {
                player.sendMessage(CollectorMessages.TITLE_SELL.toString().replace("{money}", String.valueOf(money)));
            }
            resetWithBlackList(CollectionType.TNT);
        } else {
            if (GSGCollectors.getInstance().getUnitCollectors().isUseTitles()) {
                player.sendTitle(Title.builder().title(CollectorMessages.CANT_SELL_NOTHING.toString().replace("{money}", String.valueOf(money))).fadeIn(5).fadeOut(5).stay(25).build());
            } else {
                player.sendMessage(CollectorMessages.CANT_SELL_NOTHING.toString());
            }
        }
    }

    public void resetWithBlackList(CollectionType... collectionTypes) {
        amounts.keySet()
                .forEach(collectionType -> Arrays.stream(collectionTypes)
                        .filter(type -> collectionType != type)
                        .forEach(type -> getAmounts().remove(collectionType)));
    }

    public void depositTnt(Player player) {
        int tntAmount = getAmounts().getOrDefault(CollectionType.TNT, 0);
        if (tntAmount > 0) {
            FactionsBankIntegration factionsBankIntegration = GSGCollectors.getInstance().getUnitCollectors().getFactionsBankIntegration();
            if (factionsBankIntegration != null) {
                if (factionsBankIntegration.setTntBankBalance(factionsBankIntegration.getFaction(player), factionsBankIntegration.getTntBankBalance(factionsBankIntegration.getFaction(player)) + tntAmount)) {
                    if (GSGCollectors.getInstance().getUnitCollectors().isUseTitles()) {
                        player.sendTitle(Title.builder().title(CollectorMessages.DEPOSITED_TNT.toString().replace("{tnt}", String.valueOf(tntAmount))).fadeIn(5).stay(25).fadeOut(5).build());
                    } else {
                        player.sendMessage(CollectorMessages.DEPOSITED_TNT.toString().replace("{tnt}", String.valueOf(tntAmount)));
                    }
                    reset(CollectionType.TNT);
                }
            }
        } else if (GSGCollectors.getInstance().getUnitCollectors().isUseTitles()) {
            player.sendTitle(Title.builder().title(CollectorMessages.CANT_DEPOSIT_NOTHING.toString()).fadeIn(5).stay(25).fadeOut(5).build());
        } else {
            player.sendMessage(CollectorMessages.CANT_DEPOSIT_NOTHING.toString());
        }
    }

    public void removeAmount(CollectionType collectionType, int amount) {
        getAmounts().computeIfPresent(collectionType, (collectionType1, integer) -> integer -= amount);
    }

    public void addAmount(CollectionType collectionType, int amount) {
        getAmounts().computeIfPresent(collectionType, (collectionType1, integer) -> integer += amount);
        getAmounts().putIfAbsent(collectionType, 1);
        if (!getMenuCollector().getInventory().getViewers().isEmpty()) {
            getMenuCollector().update(collectionType, getAmounts().getOrDefault(collectionType, 1));
        }
    }

    public void reset() {
        getAmounts().clear();
    }

    public void reset(CollectionType... collectionTypes) {
        for (CollectionType collectionType : collectionTypes) {
            getAmounts().remove(collectionType);
        }
    }

    public Faction getFaction() {
        return Board.getInstance().getFactionAt(new FLocation(blockPosition.getLocation()));
    }

    public Chunk getChunk() {
        return blockPosition.getLocation().getChunk();
    }

    public void setBlockPosition(BlockPosition blockPosition) {
        this.blockPosition = blockPosition;
    }

    public BlockPosition getBlockPosition() {
        return blockPosition;
    }

    public EnumMap<CollectionType, Integer> getAmounts() {
        return this.amounts == null ? this.amounts = new EnumMap<>(CollectionType.class) : this.amounts;
    }

    public void setAmounts(EnumMap<CollectionType, Integer> amounts) {
        this.amounts = amounts;
    }

    public MenuCollector getMenuCollector() {
        return this.menuCollector == null ? this.menuCollector = new MenuCollector(Collector.this) : this.menuCollector;
    }

    public String getLandOwner() {
        return landOwner;
    }

    public void setLandOwner(String landOwner) {
        this.landOwner = landOwner;
    }
}
