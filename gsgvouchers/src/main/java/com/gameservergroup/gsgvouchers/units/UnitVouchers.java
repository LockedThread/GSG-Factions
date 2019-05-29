package com.gameservergroup.gsgvouchers.units;

import com.gameservergroup.gsgcore.commands.arguments.ArgumentRegistry;
import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgvouchers.GSGVouchers;
import com.gameservergroup.gsgvouchers.objs.Voucher;
import com.google.common.base.Joiner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UnitVouchers extends Unit {

    private Map<String, Voucher> vouchers;

    @Override
    public void setup() {
        ConfigurationSection voucherSection = GSGVouchers.getInstance().getConfig().getConfigurationSection("vouchers");
        Map<String, Voucher> map = new HashMap<>();
        for (String key : voucherSection.getKeys(false)) {
            Voucher voucher = new Voucher(key, voucherSection.getConfigurationSection(key), voucherSection.getStringList(key + ".commands"));
            voucher.setInteractEventConsumer(event -> {
                System.out.println("0");
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                    System.out.println("1");
                    voucher.execute(event.getPlayer());
                }
            });
            map.put(key.toLowerCase(), voucher);
        }
        this.vouchers = map;

        ArgumentRegistry.getInstance().register(Voucher.class, () -> s -> Optional.ofNullable(vouchers.get(s.toLowerCase())));

        CommandPost.of()
                .builder()
                .assertPermission("gsgvouchers.admin")
                .handler(c -> {
                    if (c.getRawArgs().length == 0) {
                        c.reply("", " &d/vouchers give [player] [voucherName] {amount}", " &d/vouchers list", "");
                    } else if (c.getRawArgs().length == 1) {
                        if (c.getRawArg(0).equalsIgnoreCase("list")) {
                            c.reply("", "&dVouchers: &f" + Joiner.on(", ").skipNulls().join(vouchers.keySet()), "");
                        } else {
                            c.reply("&cInvalid arguments!");
                        }
                    } else if (c.getRawArgs().length >= 3 && c.getRawArgs().length <= 4) {
                        Player player = c.getArg(1).forceParse(Player.class);
                        Voucher voucher = c.getArg(2).forceParse(Voucher.class);
                        for (int i = 0; i < c.getArg(3).parse(int.class).orElse(1); i++) {
                            player.getInventory().addItem(voucher.getItemStack());
                        }
                    } else {
                        c.reply("&cInvalid arguments!");
                    }
                }).post(GSGVouchers.getInstance(), "vouchers", "voucher");
    }
}
