package com.comze_instancelabs.colormatch.patterns.logic;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.comze_instancelabs.colormatch.GameBoard;

public class RoundState extends TimerState {
	private long startTime;
	private float lastPercent;
	
	@Override
	public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
		// Calculate the round end time
		int difficulty = game.getSettings().getArenaDifficulty();
		int roundLimit = 15 - difficulty * 3;
		double roundTime = (5 - difficulty / 2.0) - (game.getRound() / (double)roundLimit)*2;
		endTime = System.currentTimeMillis() + (long)(roundTime * 1000);
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void onTick(StateEngine<GameBoard> engine, GameBoard game) {
		super.onTick(engine, game);
		
		float percent = (System.currentTimeMillis() - startTime) / (float)(endTime - startTime);
		for(Player player : game.getPlayers()) {
			player.setExp(percent);
			
			if ((lastPercent < 0.333f && percent >= 0.333f) ||
				(lastPercent < 0.666f && percent >= 0.666f) ||
				(percent >= 1)) 
			{
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 0);
			}
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
		List<Player> remaining = game.getRemainingPlayers(); 
		if (remaining.size() == 1)
			engine.setState(new PostGame());
		else if (remaining.size() == 0)
			engine.abortState(new WaitingForPlayers());
	}
	
	@Override
	public void onEvent(String name, Object data, StateEngine<GameBoard> engine, GameBoard game) {
		if (name.equals("kill")) {
			Player player = (Player)data;
			game.setSpectator(player);
			
			checkEnd(engine, game);
		} else if (name.equals("leave")) {
			Player player = (Player)data;
			checkEnd(engine, game);
		}
	}
}
