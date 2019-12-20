package com.massivecraft.factions.zcore.factionshields.menus;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.menus.fill.FillOptions;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.factionshields.FactionShield;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionShieldConfigureMenu extends Menu {

    private static final ConfigurationSection CONFIGURE_MENU_SECTION = P.p.getConfig().getConfigurationSection("faction-shields.configure-menu");

    private static FactionShieldConfigureMenu instance;

    private FactionShieldConfigureMenu() {
        super(CONFIGURE_MENU_SECTION.getString("name"), CONFIGURE_MENU_SECTION.getInt("size"));
        ConfigurationSection fillSection = CONFIGURE_MENU_SECTION.getConfigurationSection("fill");
        if (fillSection.getBoolean("enabled")) {
            setFillOptions(new FillOptions(fillSection));
        }

        initialize();
    }

    public static FactionShieldConfigureMenu getInstance() {
        return instance == null ? instance = new FactionShieldConfigureMenu() : instance;
    }

    @Override
    public void initialize() {
        List<Integer> slots = CONFIGURE_MENU_SECTION.getIntegerList("shield-item.slot-array");

        int shieldTime = P.p.getConfig().getInt("faction-shields.shield-time");
        final FactionShield[] factionShields = new FactionShield[(shieldTime * 3) + 1];

        for (int i = 0; i < factionShields.length; i++) {
            factionShields[i] = new FactionShield(i, i + shieldTime);
        }

        for (int i = 0; i < slots.size(); i++) {
            FactionShield factionShield;
            try {
                factionShield = factionShields[i];
            } catch (ArrayIndexOutOfBoundsException ex) {
                break;
            }
            int slot = slots.get(i);

            ItemStackBuilder builder = ItemStackBuilder.of(CONFIGURE_MENU_SECTION.getConfigurationSection("shield-item"))
                    .replacePlaceholders("{time-start}", factionShield.getFormattedMin(), "{time-end}", factionShield.getFormattedMax());

            setItem(slot, MenuItem.of(builder.build()).setInventoryClickEventConsumer(event -> {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) event.getWhoClicked());
                Faction faction = fPlayer.getFaction();
                event.setCancelled(true);
                if (faction != null) {
                    if (faction.getFPlayerAdmin().getAccountId().equals(fPlayer.getAccountId())) {
                        faction.setFactionShield(factionShield);
                        return;
                    }
                }
                event.getWhoClicked().closeInventory();
            }));
        }
        fill();
    }
}
