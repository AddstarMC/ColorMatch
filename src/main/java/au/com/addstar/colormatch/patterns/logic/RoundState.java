package au.com.addstar.colormatch.patterns.logic;

import au.com.addstar.colormatch.GameBoard;
import au.com.mineauz.minigames.MinigamePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class RoundState extends TimerState {
    private long startTime;
    private float lastPercent;

    @Override
    public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
        // Calculate the round end time

        startTime = System.currentTimeMillis();

        double time = 0;
        if (game.getRound() > game.getModule().getRoundDelay()) {
            time = (game.getRound() - game.getModule().getRoundDelay()) / (double) game.getModule().getRoundSpan();
        }

        if (time > 1)
            time = 1;

        time = game.getModule().getInitialRoundTime() * (1 - time) + game.getModule().getMinRoundTime() * time;

        endTime = System.currentTimeMillis() + (long) (time);
    }

    @Override
    public void onTick(StateEngine<GameBoard> engine, GameBoard game) {
        super.onTick(engine, game);

        float percent = (System.currentTimeMillis() - startTime) / (float) (endTime - startTime);
        for (MinigamePlayer player : game.getMinigame().getPlayers()) {
            float remain = ((1 - percent) < 0) ? 0 : (1 - percent);
            player.getPlayer().setExp(remain);
            if ((lastPercent < 0.333f && percent >= 0.333f) ||
                    (lastPercent < 0.666f && percent >= 0.666f)) {
                player.getPlayer().playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 0);
            } else if (percent >= 1) {
                player.getPlayer().playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 2);
            }

            lastPercent = percent;
        }
    }

    @Override
    protected void onNotifyTimeLeft(long remaining, StateEngine<GameBoard> engine, GameBoard game) {
        if (remaining == 0) {
            game.clearExcept(game.getCurrentMaterial());
            engine.setState(new BetweenRoundState());
        }
    }

    protected void checkEnd(StateEngine<GameBoard> engine, GameBoard game) {
        if (game.getRemainingCount() <= 1)
            engine.setState(new PostGame());
    }

}
