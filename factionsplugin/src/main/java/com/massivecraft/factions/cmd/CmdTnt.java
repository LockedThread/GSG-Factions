package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class CmdTnt extends FCommand {

    public CmdTnt() {
        super();

        this.addSubCommand(new CmdTntfill("f", "fill"));

        this.aliases.add("tnt");

        this.requiredArgs.add("balance|withdraw|deposit|fill");
        this.optionalArgs.put("amount", "");
        this.optionalArgs.put("radius", "");

        this.permission = Permission.TNT.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = fme.getFaction();

        Access access = faction.getAccess(fme, PermissableAction.TNTBANK);
        if (access == Access.DENY || (access == Access.UNDEFINED && !fme.getRole().isAtLeast(Role.MODERATOR))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "tnt");
            return;
        }

        String arg0 = argAsString(0);
        if (arg0.equalsIgnoreCase("balance") || arg0.equalsIgnoreCase("b")) {
            msg("<i>Your Faction's TNT balance is: &f&n" + format(faction.getTntBankBalance()) + "/" + format(faction.getTntBankLimit()));
        } else if (arg0.equalsIgnoreCase("give") && fme.isAdminBypassing()) {
            if (args.size() < 2) {
                msg("&cToo few arguments. &eUse like this:");
                msg("&b/f tnt give &3<amount>");
                return;
            }

            Integer amount = argAsInt(1);
            if (amount == null || amount < 1) {
                msg("<b>Amount must be an integer greater than zero!");
                return;
            }

            faction.setTntBankBalance(Math.min(faction.getTntBankBalance() + amount, faction.getTntBankLimit()));
            msg("<i>Success!");
        } else if (arg0.equalsIgnoreCase("withdraw") || arg0.equalsIgnoreCase("w")) {
            if (args.size() < 2) {
                msg("&cToo few arugments. &eUse like this:");
                msg("&b/f tnt w,withdraw &3<amount>");
                return;
            }

            Integer amount = argAsInt(1);
            if (amount == null || amount < 1) {
                msg("<b>Amount must be an integer greater than zero!");
                return;
            }

            if (amount > faction.getTntBankBalance()) {
                msg("<b>Your Faction doesn't have enough TNT to withdraw that much!");
                return;
            }

            int stacks = amount / 64;
            if (amount % 64 != 0) stacks++;
            int free = 0;
            for (ItemStack stack : me.getInventory()) {
                if (stack == null || stack.getType().equals(Material.AIR)) {
                    if (++free >= stacks) {
                        // enough free space
                        faction.setTntBankBalance(faction.getTntBankBalance() - amount);
                        int toGive = amount;
                        while (toGive > 0) {
                            int thisStack = toGive > 64 ? 64 : toGive;
                            toGive -= thisStack;
                            me.getInventory().addItem(new ItemStack(Material.TNT, thisStack));
                        }
                        msg("<i>You have successfully withdrawn &f&n" + format(amount) + "&e TNT from your Faction");
                        return;
                    }
                }
            }

            msg("<b>You don't have enough inventory space to withdraw that much TNT!");
        } else if (arg0.equalsIgnoreCase("deposit") || arg0.equalsIgnoreCase("d")) {
            if (args.size() < 2) {
                msg("&cToo few arugments. &eUse like this:");
                msg("&b/f tnt d,deposit &3<amount> [radius=]");
                return;
            }

            Integer amount = argAsInt(1);
            if (amount == null || amount < 1) {
                msg("<b>Amount must be an integer greater than zero!");
                return;
            }

            if (faction.getTntBankBalance() + amount > faction.getTntBankLimit()) {
                msg("<b>Your TNT bank limit is " + format(faction.getTntBankLimit()) + " and depositing " + format(amount) + " would put you over your limit!");
                return;
            }

            if (args.size() > 2) {
                // from nearby containers
                Integer radius = argAsInt(2);
                if (radius == null || radius < 1 || radius > 16) {
                    msg("<b>Radius must be an integer between 1 and 16!");
                    return;
                }

                if (!Board.getInstance().getFactionAt(new FLocation(fme)).getRelationTo(fme).isAtLeast(Relation.ALLY)) {
                    msg("<b>You can only deposit from containers in friendly territory!");
                    return;
                }

                int radiusSq = radius * radius;
                int collected = 0;

                Chunk centre = me.getLocation().getChunk();
                outer:
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Chunk chunk = me.getWorld().getChunkAt(centre.getX() + x, centre.getZ() + z);
                        Faction factionAt = Board.getInstance().getFactionAt(new FLocation(chunk.getWorld().getName(), chunk.getX(), chunk.getZ()));
                        if (factionAt == null || !faction.getRelationTo(factionAt).isAtLeast(Relation.ALLY)) {
                            continue;
                        }

                        for (BlockState state : chunk.getTileEntities()) {
                            if (state instanceof InventoryHolder) {
                                if (state.getLocation().distanceSquared(me.getLocation()) > radiusSq) {
                                    continue;
                                }

                                boolean touched = false;
                                boolean breakout = false;
                                InventoryHolder holder = (InventoryHolder) state;
                                for (int k = 0; k < holder.getInventory().getSize(); k++) {
                                    ItemStack stack = holder.getInventory().getItem(k);
                                    if (stack != null && stack.getType().equals(Material.TNT)) {
                                        touched = true; // always going to touch TNT
                                        if (collected + stack.getAmount() > amount) {
                                            stack.setAmount(stack.getAmount() - (amount - collected));
                                            collected = amount;
                                            breakout = true;
                                            break;
                                        } else if (collected + stack.getAmount() == amount) {
                                            holder.getInventory().clear(k);
                                            collected = amount;
                                            breakout = true;
                                            break;
                                        } else {
                                            collected += stack.getAmount();
                                            holder.getInventory().clear(k);
                                        }
                                    }
                                }

                                if (touched) {
                                    state.update();
                                }
                                if (breakout) {
                                    break outer;
                                }
                            }
                        }
                    }
                }

                if (collected <= 0) {
                    msg("<b>There isn't enough TNT in nearby containers!");
                    return;
                }

                if (collected < amount) {
                    msg("<b>There was only " + format(collected) + " TNT in containers within " + radius + " blocks!");
                }
                faction.setTntBankBalance(faction.getTntBankBalance() + collected);
                msg("<i>Successfully deposited " + format(collected) + " TNT to your Faction's TNT bank from containers within " + radius + " blocks of you");
            } else {
                // from inventory
                int inInventory = 0;
                for (ItemStack stack : me.getInventory()) {
                    if (stack != null && stack.getType().equals(Material.TNT)) {
                        inInventory += stack.getAmount();
                        if (inInventory >= amount) break;
                    }
                }

                if (inInventory < amount) {
                    msg("<b>You only have " + format(inInventory) + " TNT in your inventory!");
                    amount = inInventory;
                }

                int toCollect = amount;
                for (int k = 0; k < me.getInventory().getSize(); k++) {
                    ItemStack stack = me.getInventory().getItem(k);
                    if (stack != null && stack.getType().equals(Material.TNT)) {
                        if (toCollect >= stack.getAmount()) {
                            toCollect -= stack.getAmount();
                            me.getInventory().clear(k);
                            if (toCollect <= 0) break;
                        } else {
                            stack.setAmount(stack.getAmount() - toCollect);
                            break;
                        }
                    }
                }

                faction.setTntBankBalance(faction.getTntBankBalance() + amount);
                msg("<i>Successfully deposited " + format(amount) + " TNT to your Faction's TNT bank from your inventory");
            }
        } else {
            msg("&cToo few arugments. &eUse like this:");
            msg("&b/f tnt balance|withdraw|deposit|fill &3[amount] [radius]");
        }
    }

    private String format(int amount) {
        if (amount < 1000) {
            return String.valueOf(amount);
        }

        return String.format("%,d", amount);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_DESCRIPTION;
    }
}
