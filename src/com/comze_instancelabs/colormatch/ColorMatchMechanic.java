package com.comze_instancelabs.colormatch;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.mechanics.GameMechanicBase;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;

public class ColorMatchMechanic extends GameMechanicBase {
	
	private GameBoard getGame(Player player) {
		MinigamePlayer mplayer = Minigames.plugin.pdata.getMinigamePlayer(player);
		if (mplayer.isInMinigame()) {
			return getGame(mplayer.getMinigame());
		}
		
		return null;
	}
	
	private GameBoard getGame(Minigame minigame) {
		ColorMatchModule module = ColorMatchModule.getModule(minigame);
		if (module != null)
			return module.getGame();
		return null;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		GameBoard game = getGame(player);
		if (game == null)
			return;
		
		if (game.getMinigame().getState() != MinigameState.STARTED)
			return;
		
		if (game.isSpectator(player)) {
			Location spectateSpawn = game.getSpectatorSpawn();
			
			if (player.getLocation().getY() < spectateSpawn.getY() - 0.5 || player.getLocation().getY() > spectateSpawn.getY() + 0.5) {
				Location newLoc = player.getLocation();
				newLoc.setY(spectateSpawn.getY());
				MinigamePlayer mplayer = Minigames.plugin.pdata.getMinigamePlayer(player);
				mplayer.teleport(newLoc);
			}
		} else {
			Location spawn = game.getPlayerSpawn();
			player.spigot().playEffect(player.getLocation(), Effect.COLOURED_DUST, 0, 0, 1, 1, 1, 1, 60, 20);
			if (player.getLocation().getY() < spawn.getY() - game.getModule().getFallDepth()) {
				game.killPlayer(player);
			}
		}
	}
	
	@EventHandler
	private void onMinigameStart(StartMinigameEvent event) {
		GameBoard board = getGame(event.getMinigame());
		if (board == null)
			return;
		
		board.start();
	}
	
	@Override
	public String getMechanic() {
		return "colormatch";
	}

	@Override
	public EnumSet<MinigameType> validTypes() {
		return EnumSet.of(MinigameType.MULTIPLAYER);
	}

	private void send(MinigamePlayer player, String message) {
		if (player == null) {
			Bukkit.getConsoleSender().sendMessage(message);
		} else {
			player.sendMessage(message);
		}
	}
	
	@Override
	public boolean checkCanStart(Minigame minigame, MinigamePlayer player) {
		GameBoard game = getGame(minigame);
		if (game == null) {
			send(player, ChatColor.RED + "ColorMatch arena not setup. Use /cm setup " + minigame.getName(false) + " to set it up.");
			return false;
		}
		
		if (game.getSpawn() == null) {
			send(player, ChatColor.RED + "Board position is not set");
			return false;
		}
		
		if (game.getPatternMap().getPatterns().isEmpty()) {
			send(player, ChatColor.RED + "There are no defined patterns");
			return false;
		}
		
		// TODO: Check state
		return true;
	}

	@Override
	public MinigameModule displaySettings(Minigame minigame) {
		return ColorMatchModule.getModule(minigame);
	}

	@Override
	public void startMinigame(Minigame minigame, MinigamePlayer player) {} // Only for global minigames

	@Override
	public void stopMinigame(Minigame minigame, MinigamePlayer player) {} // Only for global minigames

	@Override
	public void joinMinigame(Minigame minigame, MinigamePlayer player) {
		GameBoard game = getGame(minigame);
		if (game == null)
			return;
		
		game.joinArena(player.getPlayer());
	}

	@Override
	public void quitMinigame(Minigame minigame, MinigamePlayer player, boolean forced) {
		GameBoard game = getGame(minigame);
		if (game == null)
			return;
		
		game.leaveArena(player.getPlayer());
	}

	@Override
	public void endMinigame(Minigame minigame, List<MinigamePlayer> winners, List<MinigamePlayer> losers) {
		GameBoard board = getGame(minigame);
		if (board == null)
			return;
		
		board.stop();
	}
}
