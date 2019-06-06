package frontierfactions.units;

import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.NBTItem;
import frontierfactions.FrontierCore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;

import java.util.Map;
import java.util.stream.Collectors;

public class UnitFrontierItems extends Unit {

    private static final FrontierCore FRONTIER_CORE = FrontierCore.getInstance();

    @Override
    public void setup() {
        CustomItem lightningwand = CustomItem.of(FRONTIER_CORE, FRONTIER_CORE.getConfig().getConfigurationSection("items.lightningwand-item"));
        lightningwand.setInteractEventConsumer(event -> {
            ItemStack itemInHand = event.getPlayer().getItemInHand();
            final NBTItem handNbt = new NBTItem(itemInHand);
            int uses = handNbt.getInt("uses");
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                if (uses == 0) {
                    return;
                }
                event.getPlayer().getWorld().strikeLightning(event.getClickedBlock().getLocation());
                if (uses == -1) {
                    return;
                }
                handNbt.set("uses", uses - 1);
                ItemStack itemStack = handNbt.buildItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore(lightningwand.getOriginalItemStack()
                        .getItemMeta()
                        .getLore()
                        .stream()
                        .map(s -> s.replace("{uses}", String.valueOf(uses - 1)))
                        .collect(Collectors.toList()));
                itemStack.setItemMeta(itemMeta);
                event.getPlayer().setItemInHand(itemStack);
                /*event.getPlayer().setItemInHand(new NBTItem(ItemStackBuilder.of(lightningwand.getOriginalItemStack())
                        .setLore(event.getItem().getItemMeta().getLore()
                                .stream()
                                .map(s -> s.replace("{uses}", String.valueOf(uses - 1)))
                                .collect(Collectors.toList()))
                        .build())
                        .set("uses", uses - 1)
                        .buildItemStack());*/
            } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (uses == 0) {
                    return;
                }
                BlockIterator blockIterator = new BlockIterator(event.getPlayer(), FRONTIER_CORE.getConfig().getInt("items.lightningwand-item.options.max-iterator-distance"));
                while (blockIterator.hasNext()) {
                    Block next = blockIterator.next();
                    if (next != null && next.getType() != Material.AIR) {
                        event.getPlayer().getWorld().strikeLightning(next.getLocation());
                        break;
                    }
                }
                if (uses == -1) {
                    return;
                }
                handNbt.set("uses", uses - 1);
                ItemStack itemStack = handNbt.buildItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore(lightningwand.getOriginalItemStack()
                        .getItemMeta()
                        .getLore()
                        .stream()
                        .map(s -> s.replace("{uses}", String.valueOf(uses - 1)))
                        .collect(Collectors.toList()));
                itemStack.setItemMeta(itemMeta);
                event.getPlayer().setItemInHand(itemStack);
            }
        });
        lightningwand.setItemEdit(new CustomItem.ItemEdit() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public ItemStack getEditedItemStack() {
                return getEditedItemStack(null);
            }

            @Override
            public <T> ItemStack getEditedItemStack(Map<String, T> map) {
                final ItemStack itemStack = lightningwand.getOriginalItemStack();
                NBTItem nbtItem = new NBTItem(itemStack);
                for (Map.Entry<String, T> entry : map.entrySet()) {
                    nbtItem.set(entry.getKey(), entry.getValue());
                }
                return ItemStackBuilder.of(nbtItem.buildItemStack())
                        .setLore(itemStack.getItemMeta().getLore()
                                .stream()
                                .map(s -> s.replace("{uses}", Integer.parseInt(String.valueOf(map.get("uses"))) == -1 ? FRONTIER_CORE.getConfig().getString("items.lightningwand-item.options.negative-1-keyword") : String.valueOf(map.get("uses"))))
                                .collect(Collectors.toList()))
                        .build();
            }
        });
        CustomItem sandWandItem = CustomItem.of(FRONTIER_CORE, FRONTIER_CORE.getConfig().getConfigurationSection("items.sandwand-item"));
        sandWandItem.setInteractEventConsumer(event -> {
            if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && isFallingBlockMaterial(event.getClickedBlock().getType())) {
                if (GSG_CORE.canBuild(event.getPlayer(), event.getClickedBlock())) {
                    final NBTItem handNbt = new NBTItem(event.getItem());
                    int uses = handNbt.getInt("uses");
                    if (uses == 0) {
                        return;
                    }
                    Block next = event.getClickedBlock();
                    //     next.setType(Material.AIR);
                    while (next.getType() == Material.SAND) {
                        next.setTypeIdAndData(0, (byte) 0, false);
                        next = next.getRelative(BlockFace.DOWN);
                    }
                    if (uses == -1) {
                        return;
                    }
                    handNbt.set("uses", uses - 1);
                    ItemStack itemStack = handNbt.buildItemStack();
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setLore(sandWandItem.getOriginalItemStack()
                            .getItemMeta()
                            .getLore()
                            .stream()
                            .map(s -> s.replace("{uses}", String.valueOf(uses - 1)))
                            .collect(Collectors.toList()));
                    itemStack.setItemMeta(itemMeta);
                    event.getPlayer().setItemInHand(itemStack);
                }
                event.setCancelled(true);
            }
        });
        sandWandItem.setItemEdit(new CustomItem.ItemEdit() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public ItemStack getEditedItemStack() {
                return getEditedItemStack(null);
            }

            @Override
            public <T> ItemStack getEditedItemStack(Map<String, T> map) {
                final ItemStack itemStack = sandWandItem.getOriginalItemStack();
                NBTItem nbtItem = new NBTItem(itemStack);
                for (Map.Entry<String, T> entry : map.entrySet()) {
                    nbtItem.set(entry.getKey(), entry.getValue());
                }
                return ItemStackBuilder.of(nbtItem.buildItemStack())
                        .setLore(itemStack.getItemMeta().getLore()
                                .stream()
                                .map(s -> s.replace("{uses}", Integer.parseInt(String.valueOf(map.get("uses"))) == -1 ? FRONTIER_CORE.getConfig().getString("items.sandwand-item.options.negative-1-keyword") : String.valueOf(map.get("uses"))))
                                .collect(Collectors.toList()))
                        .build();
            }
        });

        ItemStack itemStack = ItemStackBuilder.of(Material.MONSTER_EGG)
                .setData(EntityType.CREEPER.getTypeId())
                .addEnchant(Enchantment.DURABILITY, 1)
                .setDisplayName(FRONTIER_CORE.getConfig().getString("items.throwablecegg-item.options.egg-name")).build();

        CustomItem throwableCegg = CustomItem.of(FRONTIER_CORE, FRONTIER_CORE.getConfig().getConfigurationSection("items.throwablecegg-item"));
        throwableCegg.setInteractEventConsumer(event -> {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
            } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                Item item = event.getPlayer().getWorld().dropItem(event.getPlayer().getEyeLocation(), itemStack);
                item.setVelocity(event.getPlayer().getLocation().getDirection().multiply(2));
                item.setPickupDelay(20000);
                FRONTIER_CORE.getServer().getScheduler().runTaskLater(FRONTIER_CORE, () -> {
                    //noinspection ConstantConditions
                    if (item != null && !item.isDead()) {
                        item.getLocation().getWorld().createExplosion(item.getLocation(), 4f);
                        if (!item.isDead()) {
                            item.remove();
                        }
                    }
                }, FRONTIER_CORE.getConfig().getLong("items.throwablecegg-item.options.explosion-delay"));
                if (event.getItem().getAmount() == 1) {
                    event.getPlayer().setItemInHand(null);
                } else {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                }
            }
        });
    }

    private boolean isFallingBlockMaterial(Material material) {
        return material == Material.GRAVEL || material == Material.ANVIL || material == Material.SAND;
    }
}
