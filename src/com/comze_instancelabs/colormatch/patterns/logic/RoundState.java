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
		
		startTime = System.currentTimeMillis();
		
		double time = 0;
		if (game.getRound() > game.getModule().getRoundDelay()) {
			time = (game.getRound() - game.getModule().getRoundDelay()) / (double)game.getModule().getRoundSpan();
		}
		
		if (time > 1)
			time = 1;
		
		time = game.getModule().getInitialRoundTime() * (1-time) + game.getModule().getMinRoundTime() * time;
		
		endTime = System.currentTimeMillis() + (long)(time);
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
