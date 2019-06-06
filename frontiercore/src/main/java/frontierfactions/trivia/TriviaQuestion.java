package frontierfactions.trivia;

import com.gameservergroup.gsgcore.utils.Text;
import frontierfactions.FrontierCore;
import frontierfactions.rewards.Reward;
import frontierfactions.rewards.RewardType;
import frontierfactions.units.UnitTrivia;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TriviaQuestion {

    private final String question;
    private final String displayedAnswer;
    private final String[] possibleAnswers;
    private final TrivaAnswerType trivaAnswerType;

    public TriviaQuestion(String question, String displayedAnswer, TrivaAnswerType trivaAnswerType, List<String> possibleAnswers) {
        this.question = question;
        this.displayedAnswer = displayedAnswer;
        this.trivaAnswerType = trivaAnswerType;
        this.possibleAnswers = possibleAnswers.toArray(new String[0]);
    }

    public void broadcastQuestion() {
        Bukkit.broadcastMessage(Text.toColor(FrontierCore.getInstance().getConfig().getString("trivia.messages.new-trivia-question").replace("{question}", question)));
    }

    public void answeredCorrectly(Player player) {
        UnitTrivia.setCurrentTriviaQuestion(null);
        Reward[] rewards = FrontierCore.getInstance().getRewardMap().get(RewardType.TRIVIA);
        Reward reward = rewards[FrontierCore.getInstance().getRandom().nextInt(rewards.length - 1)];
        reward.executeCommands(player, RewardType.TRIVIA);
    }

    public boolean answerQuestion(String attempt) {
        switch (trivaAnswerType) {
            case ANY_EQUALS:
                return Arrays.stream(possibleAnswers).anyMatch(attempt::equalsIgnoreCase);
            case ANY_CONTAINS:
                return Arrays.stream(possibleAnswers).anyMatch(possibleAnswer -> attempt.toLowerCase().contains(possibleAnswer.toLowerCase()));
            case ALL_CONTAINS:
                return Arrays.stream(possibleAnswers).allMatch(possibleAnswer -> attempt.toLowerCase().contains(possibleAnswer.toLowerCase()));
        }
        return false;
    }

    public String getQuestion() {
        return question;
    }

    public String getDisplayedAnswer() {
        return displayedAnswer;
    }

    public String[] getPossibleAnswers() {
        return possibleAnswers;
    }

    public TrivaAnswerType getTrivaAnswerType() {
        return trivaAnswerType;
    }
}
