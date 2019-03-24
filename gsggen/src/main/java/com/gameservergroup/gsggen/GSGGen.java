package com.gameservergroup.gsggen;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsggen.integration.CombatIntegration;
import com.gameservergroup.gsggen.integration.combat.impl.CombatTagPlusImpl;
import com.gameservergroup.gsggen.menu.GenMenu;
import com.gameservergroup.gsggen.units.UnitGen;
import org.bukkit.plugin.Plugin;

public class GSGGen extends Module {

    private static GSGGen instance;
    private UnitGen unitGen;
    private GenMenu genMenu;
    private boolean enableCombatTagPlusIntegration;
    private CombatIntegration combatIntegration;

    public static GSGGen getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getBoolean("enable-combattagplus-integration")) {
            Plugin combatTagPlus = getServer().getPluginManager().getPlugin("CombatTagPlus");
            if (combatTagPlus != null) {
                getLogger().info("Enabled CombatTagPlus Integration");
                this.enableCombatTagPlusIntegration = getConfig().getBoolean("enable-combattagplus-integration");
                this.combatIntegration = new CombatTagPlusImpl(combatTagPlus);
            } else {
                getLogger().severe("You don't have CombatTagPlus installed, if you don't wish to use it disable the integration in GSGGen's config.yml");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }
        registerUnits(unitGen = new UnitGen());
        this.genMenu = new GenMenu();
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> getUnitGen().getGenerations().removeIf(generation -> generation.isVertical() ? !generation.generateVertical() : !generation.generateHorizontal()), getConfig().getLong("interval"), getConfig().getLong("interval"));
    }

    @Override
    public void reload() {
        reloadConfig();
        unitGen.load();
        this.genMenu = new GenMenu();
    }

    @Override
    public void disable() {
        instance = null;
    }

    public UnitGen getUnitGen() {
        return unitGen;
    }

    public GenMenu getGenMenu() {
        return genMenu;
    }

    public boolean isEnableCombatTagPlusIntegration() {
        return enableCombatTagPlusIntegration;
    }

    public CombatIntegration getCombatIntegration() {
        return combatIntegration;
    }
}
