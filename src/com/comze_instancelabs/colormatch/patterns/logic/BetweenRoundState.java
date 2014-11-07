package com.comze_instancelabs.colormatch.patterns.logic;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigamePlayer;

import com.comze_instancelabs.colormatch.GameBoard;

public class BetweenRoundState extends TimerState {

	@Override
	public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
		endTime = System.currentTimeMillis() + game.getModule().getRoundWaitTime();
		game.updateSigns("Wait", DyeColor.GRAY);
		game.setRound(game.getRound()+1);
		
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

	private void checkEnd(StateEngine<GameBoard> engine, GameBoard game) {
		if (game.getRemainingCount() <= 1)
			engine.setState(new PostGame());
	}
	
	@Override
	public void onEvent(String name, Object data, StateEngine<GameBoard> engine, GameBoard game) {
		if (name.equals("kill")) {
			Player player = (Player)data;
			game.setSpectator(player);
			
			checkEnd(engine, game);
		} else if (name.equals("leave")) {
			checkEnd(engine, game);
		}
	}
}
