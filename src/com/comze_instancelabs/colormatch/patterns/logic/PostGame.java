package com.comze_instancelabs.colormatch.patterns.logic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.comze_instancelabs.colormatch.GameBoard;

public class PostGame extends TimerState {

	@Override
	public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
		endTime = System.currentTimeMillis() + 2000;
	}
	
	@Override
	protected void onNotifyTimeLeft(long remaining,	StateEngine<GameBoard> engine, GameBoard game) {
		if (remaining == 0) {
			List<Player> players = new ArrayList<Player>(game.getPlayers());
			for (Player player : players) {
				game.leaveArena(player);
			}
			engine.setState(new WaitingForPlayers());
		}
	}

}
