package com.massivecraft.factions.zcore.factionshields.menus;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.menus.fill.FillOptions;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import org.bukkit.configuration.ConfigurationSection;

import java.util.stream.Collectors;

public class FactionShieldMainMenu extends Menu {

    private static final ConfigurationSection MAIN_MENU_SECTION = P.p.getConfig().getConfigurationSection("faction-shields.main-menu");
    private static final boolean PLAYERS_CAN_CONFIGURE = P.p.getConfig().getBoolean("faction-shields.players-can-configure");

    private final FPlayer fPlayer;

    public FactionShieldMainMenu(FPlayer fPlayer) {
        super(MAIN_MENU_SECTION.getString("name"), MAIN_MENU_SECTION.getInt("size"));
        this.fPlayer = fPlayer;
        ConfigurationSection fillSection = MAIN_MENU_SECTION.getConfigurationSection("fill");
        if (fillSection.getBoolean("enabled")) {
            setFillOptions(new FillOptions(fillSection));
        }
        initialize();
    }

    @Override
    public void initialize() {
        setItem(MAIN_MENU_SECTION.getInt("configure-item.slot"), MenuItem.of(ItemStackBuilder.of(MAIN_MENU_SECTION.getConfigurationSection("configure-item")).build())
                .setInventoryClickEventConsumer(event -> {
                    event.setCancelled(true);
                    if (PLAYERS_CAN_CONFIGURE) {
                        if (fPlayer.getFaction().getFPlayerAdmin().getAccountId().equals(fPlayer.getAccountId())) {
                            event.getWhoClicked().openInventory(FactionShieldConfigureMenu.getInstance().getInventory());
                        }
                    }
                }));


        setItem(MAIN_MENU_SECTION.getInt("information-item.slot"), MenuItem.of(ItemStackBuilder.of(MAIN_MENU_SECTION.getConfigurationSection("information-item"))
                .consumeItemMeta(itemMeta -> {
                    // TODO: COMPLETE THIS
                    itemMeta.setLore(itemMeta.getLore()
                            .stream()
                            .map(s -> s.replace("{last-shield-change}", "").replace("{time-remaining}", ""))
                            .collect(Collectors.toList()));
                }).build()));

        fill();
    }
}
