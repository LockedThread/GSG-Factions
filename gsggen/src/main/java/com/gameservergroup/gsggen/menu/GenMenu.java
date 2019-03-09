package com.gameservergroup.gsggen.menu;

import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.objs.Gen;

public class GenMenu extends Menu {

    private static final GSGGen GSG_GEN = GSGGen.getInstance();

    public GenMenu() {
        super(GSG_GEN.getConfig().getString("menu.name"), GSG_GEN.getConfig().getInt("menu.size"));
    }

    @Override
    public void initialize() {
        for (String key : GSG_GEN.getConfig().getConfigurationSection("menu.items").getKeys(false)) {
            Gen gen = GSG_GEN.getUnitGen().getGenHashMap().get(GSG_GEN.getConfig().getString("menu.items." + key));
            if (gen != null) {
                setItem(Integer.parseInt(key), gen.getMenuItem());
            } else {
                throw new RuntimeException("Unable to find Gen called \"" + key + "\"");
            }
        }
    }
}
