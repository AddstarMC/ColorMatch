package com.comze_instancelabs.colormatch.patterns.logic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;

import com.comze_instancelabs.colormatch.GameBoard;

public class PostGame extends TimerState {

	private List<MinigamePlayer> winners;
	private List<MinigamePlayer> losers;
	
	@Override
	public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
		endTime = System.currentTimeMillis() + 2000;
		game.generate();
		game.updateSigns(game.getModule().getIdleMessage(), DyeColor.RED);
		
		winners = new ArrayList<MinigamePlayer>(game.getRemainingCount());
		losers = new ArrayList<MinigamePlayer>(game.getMinigame().getPlayers().size()-game.getRemainingCount());
		
		for (MinigamePlayer player : game.getMinigame().getPlayers()) {
			if (game.isSpectator(player.getPlayer())) {
				losers.add(player);
			} else {
				winners.add(player);
			}
		}
	}
	
	@Override
	protected void onNotifyTimeLeft(long remaining,	StateEngine<GameBoard> engine, GameBoard game) {
		if (remaining == 0) {
			Minigames.plugin.pdata.endMinigame(game.getMinigame(), winners, losers);
			game.stop();
		}
	}
}
