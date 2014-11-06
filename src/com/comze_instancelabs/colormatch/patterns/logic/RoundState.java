package com.comze_instancelabs.colormatch.patterns.logic;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigamePlayer;

import com.comze_instancelabs.colormatch.GameBoard;

public class RoundState extends TimerState {
	private long startTime;
	private float lastPercent;
	
	@Override
	public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
		// Calculate the round end time
		int difficulty = game.getModule().getDifficulty();
		int roundLimit = 15 - difficulty * 3;
		double roundTime = (5 - difficulty / 2.0) - (game.getRound() / (double)roundLimit)*2;
		endTime = System.currentTimeMillis() + (long)(roundTime * 1000);
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void onTick(StateEngine<GameBoard> engine, GameBoard game) {
		super.onTick(engine, game);
		
		float percent = (System.currentTimeMillis() - startTime) / (float)(endTime - startTime);
		for(MinigamePlayer player : game.getMinigame().getPlayers()) {
			player.getPlayer().setExp(1 - percent);
			
			if ((lastPercent < 0.333f && percent >= 0.333f) ||
				(lastPercent < 0.666f && percent >= 0.666f)) 
			{
				player.getPlayer().playSound(player.getLocation(), Sound.NOTE_BASS, 1, 0);
			} else if (percent >= 1) {
				player.getPlayer().playSound(player.getLocation(), Sound.NOTE_BASS, 1, 2);
			}
			
			lastPercent = percent;
		}
	}
	
	@Override
	protected void onNotifyTimeLeft(long remaining,	StateEngine<GameBoard> engine, GameBoard game) {
		if (remaining == 0) {
			game.clearExcept(game.getColour());
			engine.setState(new BetweenRoundState());
		}
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
