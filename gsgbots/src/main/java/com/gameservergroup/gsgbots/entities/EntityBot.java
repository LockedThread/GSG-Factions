package com.gameservergroup.gsgbots.entities;

import com.gameservergroup.gsgbots.GSGBots;
import com.gameservergroup.gsgbots.utils.Utils;
import com.gameservergroup.gsgcore.utils.Text;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.lang.reflect.Field;
import java.util.Map;

public class EntityBot extends EntityVillager {

    private static final UnsafeList EMPTY_UNSAFE_LIST = new UnsafeList() {
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
    private static Field pathfinderGoalSelector_b, pathfinderGoalSelector_c;

    static {
        try {
            pathfinderGoalSelector_b = PathfinderGoalSelector.class.getDeclaredField("b");
            pathfinderGoalSelector_b.setAccessible(true);
            pathfinderGoalSelector_c = PathfinderGoalSelector.class.getDeclaredField("c");
            pathfinderGoalSelector_c.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public EntityBot(World world) {
        super(world);

        try {
            pathfinderGoalSelector_b.set(this.goalSelector, EMPTY_UNSAFE_LIST);
            pathfinderGoalSelector_b.set(this.targetSelector, EMPTY_UNSAFE_LIST);
            pathfinderGoalSelector_c.set(this.goalSelector, EMPTY_UNSAFE_LIST);
            pathfinderGoalSelector_c.set(this.targetSelector, EMPTY_UNSAFE_LIST);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        this.setHealth(20);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0);
    }

    public static void setVillagerProfession(Villager.Profession villagerProfession) {
        EntityBot.villagerProfession = villagerProfession;
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

    @SuppressWarnings("unchecked")
    public static void registerEntityBot() {
        try {
            String name = "bot";
            int id = EntityType.VILLAGER.getTypeId();

            ((Map) Utils.getPrivateField(EntityTypes.class, "c").get(null)).put(name, EntityBot.class);
            ((Map) Utils.getPrivateField(EntityTypes.class, "d").get(null)).put(EntityBot.class, name);
            ((Map) Utils.getPrivateField(EntityTypes.class, "e").get(null)).put(id, EntityBot.class);
            ((Map) Utils.getPrivateField(EntityTypes.class, "f").get(null)).put(EntityBot.class, id);
            ((Map) Utils.getPrivateField(EntityTypes.class, "g").get(null)).put(name, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void update(Player player, int sand) {
        this.setProfession(villagerProfession.getId());

        this.setCustomNameVisible(true);
        this.setCustomName(Text.toColor(GSGBots.getInstance().getConfig().getString("bot.entity.name"))
                .replace("{sand}", String.valueOf(sand))
                .replace("{player}", player.getName()));
    }
}
