import com.massivecraft.factions.zcore.factionshields.FactionShield;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeTest {

    private static final long SECONDS_IN_HOUR = 3600L;

    private static final Calendar CURRENT_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));

    private static final FactionShield ZERO_TO_EIGHT = new FactionShield(0, 8);

    private static final FactionShield TWENTY_TO_FOUR = new FactionShield(20, 4);

    private static final FactionShield SIX_TO_FOUR = new FactionShield(6, 4);

    @Test
    public void testTime() {
        System.out.println("getCurrentHour() = " + getCurrentHour());
        System.out.println("System.currentTimeMillis() /1000 = " + System.currentTimeMillis() / 1000);

        long _10HoursAgo = getXHoursAgo(9);

        System.out.println("_10HoursAgo = " + _10HoursAgo);

        boolean hasBeen10Hours = hasBeenXHours(10, _10HoursAgo);

        System.out.println("hasBeen10Hours = " + hasBeen10Hours);
    }

    @Test
    public void testFactionShields() {

        int currentHour = getCurrentHour();

        System.out.println("ZERO_TO_EIGHT.isInBetween(currentHour) = " + ZERO_TO_EIGHT.isInBetween(null, currentHour));

        System.out.println("TWENTY_TO_FOUR.isInBetween(currentHour) = " + TWENTY_TO_FOUR.isInBetween(null, currentHour));

        System.out.println("SIX_TO_FOUR.isInBetween(currentHour) = " + SIX_TO_FOUR.isInBetween(null, currentHour));
    }

    public int getCurrentHour() {
        return CURRENT_CALENDAR.get(Calendar.HOUR_OF_DAY);
    }

    private long getXHoursAgo(int hours) {
        long current = System.currentTimeMillis() / 1000;

        return current - SECONDS_IN_HOUR * hours;
    }

    private boolean hasBeenXHours(int hours, long test) {
        long current = System.currentTimeMillis() / 1000;

        return (current - (SECONDS_IN_HOUR * hours)) > test;
    }

    @Test
    public void testShieldTimes() {
        int shieldTime = 8;
        final FactionShield[] FACTION_SHIELDS = new FactionShield[(shieldTime * 3) + 1];

        for (int i = 0; i < FACTION_SHIELDS.length; i++) {
            FACTION_SHIELDS[i] = new FactionShield(i, i + shieldTime);
        }

        for (FactionShield factionShield : FACTION_SHIELDS) {
            System.out.println("factionShield.getFormattedTime() = " + factionShield.getFormattedTime());
        }

        System.out.println(Arrays.deepToString(FACTION_SHIELDS));

    }

}
