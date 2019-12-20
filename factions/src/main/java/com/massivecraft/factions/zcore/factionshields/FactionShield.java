package com.massivecraft.factions.zcore.factionshields;

import com.massivecraft.factions.Faction;

import java.util.Calendar;
import java.util.TimeZone;

public final class FactionShield {

    private static Calendar CURRENT_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));

    private final int minHour, maxHour;

    public FactionShield(int minHour, int maxHour) {
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    private static String calculateTime(int time) {
        if (time == 0) {
            return "12AM";
        } else if (time < 12) {
            return time + "AM";
        } else if (time == 12) {
            return "12PM";
        } else if (time == 24) {
            return "12AM";
        } else {
            int calc = Math.abs(time - 12);
            if (calc > 12) {
                return calc - 12 + " AM";
            }
            return calc + "PM";
        }
    }

    public boolean isInBetween(Faction faction) {
        return isInBetween(faction, CURRENT_CALENDAR.get(Calendar.HOUR_OF_DAY));
    }

    public String getFormattedTime() {
        return getFormattedMin() + "-" + getFormattedMax();
    }

    public String getFormattedMin() {
        return calculateTime(minHour);
    }

    public String getFormattedMax() {
        return calculateTime(maxHour);
    }

    public boolean isInBetween(Faction faction, int test) {
        if (faction == null || faction.getFactionShield() != null) {
            if (minHour > maxHour) {// If maxHour is in the next day
                return test >= minHour && test <= 24 + maxHour;
            } else if (minHour < maxHour) {
                return test >= minHour && test <= maxHour;
            } else {
                throw new RuntimeException("This shouldn't being happening. Report this to LockedThread immediately, this is a config issue.");
            }
        }
        return false;
    }

    public int getMinHour() {
        return minHour;
    }

    public int getMaxHour() {
        return maxHour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FactionShield that = (FactionShield) o;

        if (minHour != that.minHour) return false;
        return maxHour == that.maxHour;
    }

    @Override
    public int hashCode() {
        int result = minHour;
        result = 31 * result + maxHour;
        return result;
    }

    @Override
    public String toString() {
        return "FactionShield{" +
                "minHour=" + minHour +
                ", maxHour=" + maxHour +
                '}';
    }
}
