package au.com.addstar.colormatch.patterns.logic;

import au.com.addstar.colormatch.GameBoard;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class PostGame extends TimerState {

    private List<MinigamePlayer> winners;
    private List<MinigamePlayer> losers;

    @Override
    public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
        endTime = System.currentTimeMillis() + game.getModule().getPostGameTime();
        game.generate();
        game.updateSigns(game.getModule().getIdleMessage(), Material.RED_CONCRETE);

        winners = new ArrayList<MinigamePlayer>(game.getRemainingCount());
        losers = new ArrayList<MinigamePlayer>(game.getMinigame().getPlayers().size() - game.getRemainingCount());

        for (MinigamePlayer player : game.getMinigame().getPlayers()) {
            if (game.isSpectator(player.getPlayer())) {
                losers.add(player);
            } else {
                winners.add(player);
            }
        }
    }

    @Override
    public void onEvent(String name, Object data, StateEngine<GameBoard> engine, GameBoard game) {
    }

    @Override
    protected void onNotifyTimeLeft(long remaining, StateEngine<GameBoard> engine, GameBoard game) {
        if (remaining == 0) {
            Minigames.getPlugin().getPlayerManager().endMinigame(game.getMinigame(), winners, losers);
            game.stop();
        }
    }
}
