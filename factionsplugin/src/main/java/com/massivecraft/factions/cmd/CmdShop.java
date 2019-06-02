package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.factionshop.Shop;
import com.massivecraft.factions.zcore.factionshop.ShopType;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShop extends FCommand {

    public CmdShop() {
        super();
        this.aliases.add("shop");
        this.permission = Permission.SHOP.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOP_DESCRIPTION;
    }

    @Override
    public void perform() {
        me.openInventory(Shop.getShopMap().get(ShopType.CATALOG).getInventory());
    }
}
