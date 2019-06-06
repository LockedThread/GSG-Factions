package frontierfactions.tasks;

import frontierfactions.FrontierCore;
import frontierfactions.units.UnitTrivia;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskTriviaQuestionPicker extends BukkitRunnable {

    private static final FrontierCore FRONTIER_CORE = FrontierCore.getInstance();

    @Override
    public void run() {
        if (UnitTrivia.getCurrentTriviaQuestion() != null) {
            UnitTrivia.setCurrentTriviaQuestion(null);
        }
        UnitTrivia.setCurrentTriviaQuestion(FRONTIER_CORE.getTriviaQuestions()[FRONTIER_CORE.getRandom().nextInt(FRONTIER_CORE.getTriviaQuestions().length - 1)]);
        UnitTrivia.getCurrentTriviaQuestion().broadcastQuestion();
    }
}
