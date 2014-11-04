package com.comze_instancelabs.colormatch.patterns.logic;

import org.bukkit.entity.Player;

import com.comze_instancelabs.colormatch.GameBoard;

public class WaitingForPlayers extends State<GameBoard> {
	
	private void onPlayerJoin(StateEngine<GameBoard> engine, GameBoard game, Player player) {
		if (game.getPlayers().size() >= game.getSettings().getArenaMinPlayers()) {
			engine.setState(new StartCountdown());
		}
	}
	
	private void onPlayerLeave(StateEngine<GameBoard> engine, GameBoard game, Player player) {
		
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
