package com.gameservergroup.gsgcollectors.obj;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.enums.CollectionType;
import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
import com.gameservergroup.gsgcollectors.integration.SellPriceModifierIntegration;
import com.gameservergroup.gsgcollectors.integration.impl.AceOutpostsSellPriceModifierImpl;
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
import java.util.Objects;

public class Collector {

    private transient MenuCollector menuCollector;

    private BlockPosition blockPosition;
    private EnumMap<CollectionType, Integer> amounts;
    private String landOwner;

    public Collector(Location location, String owner) {
        this(BlockPosition.of(location), owner);
    }

    public Collector(BlockPosition blockPosition, String owner) {
        this.blockPosition = blockPosition;
        this.landOwner = owner;
    }

    public void sellAll(Player player, CollectionType collectionType) {
        double money = collectionType.getPrice() * getAmounts().getOrDefault(collectionType, 0);
        if (GSGCollectors.getInstance().getSellPriceModifierIntegration() != null) {
            money = GSGCollectors.getInstance().getSellPriceModifierIntegration().getModifiedPrice(player, money);
        }
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
        sellAll(player, 0.0);
    }

    public double sellAll(Player player, double multiplier) {
        double money = getAmounts().entrySet().stream().filter(entry -> entry.getKey() != CollectionType.TNT).mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue()).sum();
        boolean outpost = false;
        SellPriceModifierIntegration sellIntegration = GSGCollectors.getInstance().getSellPriceModifierIntegration();
        if (sellIntegration != null) {
            if (sellIntegration instanceof AceOutpostsSellPriceModifierImpl) {
                outpost = true;
            } else {
                money = sellIntegration.getModifiedPrice(player, money);
            }
        }
        if (money > 0.0) {
            multiplier += outpost ? 1 : 0;
            money *= multiplier;
            if (sellIntegration != null && outpost) {
                money = sellIntegration.getModifiedPrice(player, money);
            }
            Module.getEconomy().depositPlayer(player, money);
            String replace = CollectorMessages.TITLE_SELL.toString().replace("{money}", String.valueOf(money));
            if (GSGCollectors.getInstance().getUnitCollectors().isUseTitles()) {
                player.sendTitle(Title.builder().title(replace).fadeIn(5).fadeOut(5).stay(25).build());
            } else {
                player.sendMessage(replace);
            }
            resetWithBlackList(CollectionType.TNT);
            return money;
        } else if (GSGCollectors.getInstance().getUnitCollectors().isUseTitles()) {
            player.sendTitle(Title.builder().title(CollectorMessages.CANT_SELL_NOTHING.toString().replace("{money}", String.valueOf(money))).fadeIn(5).fadeOut(5).stay(25).build());
        } else {
            player.sendMessage(CollectorMessages.CANT_SELL_NOTHING.toString());
        }
        return 0.0;
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
        return this.menuCollector == null ? this.menuCollector = new MenuCollector(this) : this.menuCollector;
    }

    public void resetCollectorMenu() {
        this.menuCollector = new MenuCollector(this);
    }

    public String getLandOwner() {
        return landOwner;
    }

    public void setLandOwner(String landOwner) {
        this.landOwner = landOwner;
    }

    @Override
    public String toString() {
        return "Collector{" +
                "menuCollector=" + menuCollector +
                ", blockPosition=" + blockPosition +
                ", amounts=" + amounts +
                ", landOwner='" + landOwner + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collector collector = (Collector) o;

        return Objects.equals(blockPosition, collector.blockPosition) && Objects.equals(landOwner, collector.landOwner);
    }

    @Override
    public int hashCode() {
        int result = 31 + (blockPosition != null ? blockPosition.hashCode() : 0);
        result = 31 * result + (amounts != null ? amounts.hashCode() : 0);
        result = 31 * result + (landOwner != null ? landOwner.hashCode() : 0);
        return result;
    }
}
