package com.comze_instancelabs.colormatch.patterns.logic;

import java.util.List;

import org.bukkit.entity.Player;

import com.comze_instancelabs.colormatch.GameBoard;

public class BetweenRoundState extends TimerState {

	@Override
	public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
		endTime = System.currentTimeMillis() + 2000;
	}
	
	@Override
	protected void onNotifyTimeLeft(long remaining, StateEngine<GameBoard> engine, GameBoard game) {
		if (remaining == 0)
			engine.setState(new PreRoundState());
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
