package au.com.addstar.colormatch.patterns.logic;

import au.com.addstar.colormatch.GameBoard;
import au.com.mineauz.minigames.MinigamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BetweenRoundState extends TimerState {

    @Override
    public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
        endTime = System.currentTimeMillis() + game.getModule().getRoundWaitTime();
        game.updateSigns("Wait", Material.GRAY_CONCRETE);
        game.setRound(game.getRound() + 1);

        for (MinigamePlayer player : game.getMinigame().getPlayers()) {
            player.getPlayer().getInventory().clear();
            player.updateInventory();
        }

        game.updateScoreboard();
    }

    @Override
    protected void onNotifyTimeLeft(long remaining, StateEngine<GameBoard> engine, GameBoard game) {
        if (remaining == 0)
            engine.setState(new PreRoundState());
    }

    @Override
    protected void checkEnd(StateEngine<GameBoard> engine, GameBoard game) {
        if (game.getRemainingCount() <= 1)
            engine.setState(new PostGame());
    }
}
