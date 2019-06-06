package dev.lockedthread.frontierfactions.frontiercore;


import com.gameservergroup.gsgcore.plugin.Module;
import dev.lockedthread.frontierfactions.frontiercore.rewards.Reward;
import dev.lockedthread.frontierfactions.frontiercore.rewards.RewardType;
import dev.lockedthread.frontierfactions.frontiercore.trivia.TrivaAnswerType;
import dev.lockedthread.frontierfactions.frontiercore.trivia.TriviaQuestion;
import dev.lockedthread.frontierfactions.frontiercore.units.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class FrontierCore extends Module {

    private static FrontierCore instance;
    private Map<RewardType, Reward[]> rewardMap;
    private TriviaQuestion[] triviaQuestions;
    private Random random;
    private boolean verbose;
    private UnitGracePeriod unitGracePeriod;

    public static FrontierCore getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getBoolean("random-use-optimized-psuedo-random")) {
            try {
                this.random = (Random) Class.forName("it.unimi.dsi.util.XoRoShiRo128PlusRandom").newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                this.random = ThreadLocalRandom.current();
                getLogger().severe("Your spigot version does not support dsi utils!");
            }
        } else {
            this.random = ThreadLocalRandom.current();
        }
        this.verbose = getConfig().getBoolean("rewards.verbose-ops");
        this.rewardMap = new EnumMap<>(RewardType.class);
        ConfigurationSection section = getConfig().getConfigurationSection("trivia.questions");
        List<String> keys = new ArrayList<>(section.getKeys(false));
        this.triviaQuestions = new TriviaQuestion[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            triviaQuestions[i] = new TriviaQuestion(section.getString(key + ".question"), section.getString(key + ".display-answer"), TrivaAnswerType.valueOf(section.getString(key + ".answer-type").toUpperCase()), section.getStringList(key + ".answers"));
        }

        registerUnits(new UnitReward(), new UnitTrivia(), new UnitAutoRespawn(), new UnitFrontierItems(), unitGracePeriod = new UnitGracePeriod());
    }

    @Override
    public void disable() {
        instance = null;
    }

    @Override
    public Random getRandom() {
        return random;
    }

    public Map<RewardType, Reward[]> getRewardMap() {
        return rewardMap;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public TriviaQuestion[] getTriviaQuestions() {
        return triviaQuestions;
    }

    public UnitGracePeriod getUnitGracePeriod() {
        return unitGracePeriod;
    }
}
