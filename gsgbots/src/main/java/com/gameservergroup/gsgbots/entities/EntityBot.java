package com.gameservergroup.gsgbots.entities;

import com.gameservergroup.gsgbots.GSGBots;
import com.gameservergroup.gsgcore.utils.Text;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class EntityBot extends EntityVillager {

    private static final LinkedHashSet EMPTY_LINKED_HASHSET = new LinkedHashSet() {
        @Override
        public boolean add(Object o) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }
    };
    private static Villager.Profession villagerProfession;
    private static Field entityVillager_b, entityVillager_c;

    static {
        try {
            entityVillager_b = EntityVillager.class.getField("b");
            entityVillager_b.setAccessible(true);
            entityVillager_c = EntityVillager.class.getField("c");
            entityVillager_c.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public EntityBot(World world, Player player, int sand) {
        super(world);

        try {
            entityVillager_b.set(this.goalSelector, EMPTY_LINKED_HASHSET);
            entityVillager_b.set(this.targetSelector, EMPTY_LINKED_HASHSET);
            entityVillager_c.set(this.goalSelector, EMPTY_LINKED_HASHSET);
            entityVillager_c.set(this.targetSelector, EMPTY_LINKED_HASHSET);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        this.setProfession(villagerProfession.getId());

        this.setCustomNameVisible(true);
        this.setCustomName(Text.toColor(GSGBots.getInstance().getConfig().getString("bot.entity.name"))
                .replace("{sand}", String.valueOf(sand))
                .replace("{player}", player.getName()));

    }

    public static void setVillagerProfession(Villager.Profession villagerProfession) {
        EntityBot.villagerProfession = villagerProfession;
    }

    public static void registerEntityBot() {
        try {
            List<Map<?, ?>> dataMap = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }

            if (dataMap.get(2).containsKey(120)) {
                dataMap.get(0).remove("Villager");
                dataMap.get(2).remove(120);
            }

            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, EntityBot.class, "Villager", 120);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void move(double d0, double d1, double d2) {

    }

    @Override
    public void collide(Entity entity) {

    }

    @Override
    public void g(double d0, double d1, double d2) {

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }
}
