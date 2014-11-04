package com.comze_instancelabs.colormatch.patterns.logic;

import org.bukkit.entity.Player;

import com.comze_instancelabs.colormatch.GameBoard;
import com.comze_instancelabs.colormatch.Messages;

public class StartCountdown extends TimerState {
	
	@Override
	public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
		endTime = System.currentTimeMillis() + 5000;
		game.broadcast(Messages.starting_in + 5 + Messages.starting_in2);
	}
	
	private void onPlayerJoin(StateEngine<GameBoard> engine, GameBoard game, Player player) {
		
	}
	
	private void onPlayerLeave(StateEngine<GameBoard> engine, GameBoard game, Player player) {
		if (game.getPlayers().size() < game.getSettings().getArenaMinPlayers()) {
			// TODO: Broadcast message
			engine.abortState(new WaitingForPlayers());
		}
	}
	
	@Override
	protected void onNotifyTimeLeft(long remaining, StateEngine<GameBoard> engine, GameBoard game) {
		if (remaining == 0) {
			game.broadcast(Messages.started);
			engine.setState(new PreRoundState());
		} else
			game.broadcast(Messages.starting_in + (remaining / 1000) + Messages.starting_in2);
	}
	
	@Override
	public void onEvent(String name, Object data, StateEngine<GameBoard> engine, GameBoard game) {
		if (name.equals("join")) {
			onPlayerJoin(engine, game, (Player)data);
		} else if (name.equals("leave")) {
			onPlayerLeave(engine, game, (Player)data);
		}
	}
}
