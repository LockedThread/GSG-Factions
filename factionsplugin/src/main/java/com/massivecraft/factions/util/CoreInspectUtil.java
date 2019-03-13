package com.massivecraft.factions.util;

import com.gameservergroup.gsgcore.utils.TimeUtil;
import net.coreprotect.Functions;
import net.coreprotect.database.Database;
import net.coreprotect.model.Config;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.sql.ResultSet;
import java.sql.Statement;

// Don't look at this class unless you want to cry
public class CoreInspectUtil {

    public static Vector getVector(String[] data) {
        int xx = Integer.parseInt(data[2]);
        int yx = Integer.parseInt(data[3]);
        int zxx = Integer.parseInt(data[4]);
        return new Vector(xx, yx, zxx);
    }

    public static String format(String[] data) {
        return formatWithLocation(data)[0];
    }

    public static String[] formatWithLocation(String[] data) {
        String string_amount = "";
        int drb = Integer.parseInt(data[8]);
        String rbd = "";
        if (drb == 1) {
            rbd = "§m";
        }

        String time = data[0];
        String dplayerx = data[1];
        int xx = Integer.parseInt(data[2]);
        int yx = Integer.parseInt(data[3]);
        int zxx = Integer.parseInt(data[4]);
        String dtype = data[5];
        int ddata = Integer.parseInt(data[6]);
        int daction = Integer.parseInt(data[7]);
//        int widxx = Integer.parseInt(data[9]);
        String a = "placed";
//        if (arg_action.contains(4) || arg_action.contains(5)) {
//            amount = Integer.parseInt(data[10]);
//            string_amount = "x" + amount + " ";
//            a = "added";
//        }

        if (daction == 0) {
            a = "removed";
        } else if (daction == 2) {
            a = "clicked";
        } else if (daction == 3) {
            a = "killed";
        }

        int unixtimestamp = (int) (System.currentTimeMillis() / 1000L);
        double time_since = (double) unixtimestamp - Double.parseDouble(time);
        String timeago = TimeUtil.toShortForm((long) time_since);
//        double time_lengthx = (double)timeago.replaceAll("[^0-9]", "").length() * 1.5D;
//        int paddingx = (int)(time_lengthx + 12.5D);
//        String left_paddingx = StringUtils.leftPad("", paddingx, ' ');
//        String worldx = Functions.getWorldName(widxx);
        String dname;
        if (daction == 3) {
            dname = Functions.getEntityType(Integer.parseInt(dtype)).name();
        } else {
            dname = Functions.getType(Integer.parseInt(dtype)).name().toLowerCase();
            dname = Functions.nameFilter(dname, ddata);
        }

        if (dname.length() > 0) {
            dname = "minecraft:" + dname.toLowerCase();
        }

        if (dname.contains("minecraft:")) {
            String[] block_name_split = dname.split(":");
            dname = block_name_split[1];
        }

        return new String[]{
                "§b" + rbd + dplayerx + " §f" + rbd + a + " " + string_amount + "§3" + rbd + dname + " §7" + timeago + " ago",
                "§7    (X " + xx + " | Y " + yx + " | Z " + zxx + ")"
//                "§7" + timeago + " ago §- §3" + rbd + dplayerx + " §f" + rbd + a + " " + string_amount + "§3" + rbd + dname + "§f.",
//                "§f" + left_paddingx + "§7(x" + xx + "/y" + yx + "/z" + zxx + "/" + worldx + ")"
        };
    }

    public static String chestTranscations(Statement statement, Location l) {
        String result = "";

        try {
            if (l == null) {
                return result;
            }

            boolean found = false;
            int x = (int) Math.floor(l.getX());
            int y = (int) Math.floor(l.getY());
            int z = (int) Math.floor(l.getZ());
            int x2 = (int) Math.ceil(l.getX());
            int y2 = (int) Math.ceil(l.getY());
            int z2 = (int) Math.ceil(l.getZ());
            int time = (int) (System.currentTimeMillis() / 1000L);
            int wid = Functions.getWorldId(l.getWorld().getName());
            int count = 0;
            String query = "SELECT COUNT(*) as count from " + Config.prefix + "container WHERE wid = '" + wid + "' AND (x = '" + x + "' OR x = '" + x2 + "') AND (z = '" + z + "' OR z = '" + z2 + "') AND y = '" + y + "' LIMIT 0, 1";

            ResultSet rs;
            for (rs = statement.executeQuery(query); rs.next(); count = rs.getInt("count")) {
            }

            rs.close();
            query = "SELECT time,user,action,type,data,amount,rolled_back FROM " + Config.prefix + "container WHERE wid = '" + wid + "' AND (x = '" + x + "' OR x = '" + x2 + "') AND (z = '" + z + "' OR z = '" + z2 + "') AND y = '" + y + "' ORDER BY rowid DESC";

            int result_amount;
            String result_user;
            String timeago;
            String a2;
            String rbd;
            String dname;
//            for(rs = statement.executeQuery(query); rs.next(); result = result + "§7" + timeago + "/h ago §f- §3" + rbd + result_user + " §f" + rbd + a2 + " x" + result_amount + " §3" + rbd + dname + "§f.\n") {
            for (rs = statement.executeQuery(query); rs.next(); result = result + "§b" + rbd + result_user + " §f" + rbd + a2 + " " + result_amount + "x §3" + rbd + dname + " §7" + timeago + " ago\n") {
                int result_userid = rs.getInt("user");
                int result_action = rs.getInt("action");
                int result_type = rs.getInt("type");
                int result_data = rs.getInt("data");
                int result_time = rs.getInt("time");
                result_amount = rs.getInt("amount");
                int result_rolled_back = rs.getInt("rolled_back");
                if (Config.player_id_cache_reversed.get(result_userid) == null) {
                    Database.loadUserName(statement.getConnection(), result_userid);
                }

                result_user = Config.player_id_cache_reversed.get(result_userid);
                double time_since = (double) time - ((double) result_time + 0.0D);
                timeago = TimeUtil.toShortForm((long) time_since);

                found = true;
                a2 = "added";
                if (result_action == 0) {
                    a2 = "removed";
                }

                rbd = "";
                if (result_rolled_back == 1) {
                    rbd = "§m";
                }

                dname = Functions.getType(result_type).name().toLowerCase();
                dname = Functions.nameFilter(dname, result_data);
                if (dname.length() > 0) {
                    dname = "minecraft:" + dname.toLowerCase();
                }

                if (dname.contains("minecraft:")) {
                    String[] block_name_split = dname.split(":");
                    dname = block_name_split[1];
                }
            }

            rs.close();
            if (!found) {
                result = "§c§oNo container data at this location";
            }
        } catch (Exception var36) {
            var36.printStackTrace();
        }

        return result;
    }
}
