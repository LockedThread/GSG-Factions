package frontierfactions.units;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import frontierfactions.FrontierCore;
import frontierfactions.tasks.TaskTriviaQuestionPicker;
import frontierfactions.trivia.TriviaQuestion;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UnitTrivia extends Unit {

    private static final FrontierCore FRONTIER_CORE = FrontierCore.getInstance();
    private static TriviaQuestion currentTriviaQuestion;

    public static TriviaQuestion getCurrentTriviaQuestion() {
        return currentTriviaQuestion;
    }

    public static void setCurrentTriviaQuestion(TriviaQuestion currentTriviaQuestion) {
        UnitTrivia.currentTriviaQuestion = currentTriviaQuestion;
    }

    @Override
    public void setup() {
        EventPost.of(AsyncPlayerChatEvent.class, EventPriority.MONITOR)
                .filter(event -> currentTriviaQuestion != null)
                .filter(event -> currentTriviaQuestion.answerQuestion(event.getMessage()))
                .handle(event -> currentTriviaQuestion.answeredCorrectly(event.getPlayer()))
                .post(FRONTIER_CORE);

        new TaskTriviaQuestionPicker().runTaskTimer(FRONTIER_CORE, FRONTIER_CORE.getConfig().getLong("trivia.timer-in-minutes") * 1200, FRONTIER_CORE.getConfig().getLong("trivia.timer-in-minutes") * 1200);
    }
}
