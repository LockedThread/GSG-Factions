package com.gameservergroup.gsgoutpost.menus;

import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgoutpost.GSGOutpost;

public class MenuOutpost extends Menu {

    public MenuOutpost() {
        super(GSGOutpost.getInstance().getConfig().getString("outpost.menu.name"), GSGOutpost.getInstance().getConfig().getInt("outpost.menu.size"));
    }

    @Override
    public void initialize() {

    }
}
