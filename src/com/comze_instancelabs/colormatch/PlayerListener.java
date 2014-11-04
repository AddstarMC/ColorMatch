package com.comze_instancelabs.colormatch;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		GameBoard game = GameBoard.getGame(player);
		if (game == null)
			return;
		
		if (game.isSpectator(player)) {
			Location spectateSpawn = game.getSpectatorSpawn();
			
			if (player.getLocation().getY() < spectateSpawn.getY() || player.getLocation().getY() > spectateSpawn.getY()) {
				Location newLoc = player.getLocation();
				newLoc.setY(spectateSpawn.getY());
				player.teleport(newLoc);
			}
		} else {
			Location spawn = game.getPlayerSpawn();
			if (player.getLocation().getY() < spawn.getY() - game.getSettings().getArenaFallDepth()) {
				game.killPlayer(player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		GameBoard game = GameBoard.getGame(player);
		if (game == null)
			return;
		
		game.leaveArena(player);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (GameBoard.getGame(event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onHunger(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (GameBoard.getGame(player) != null) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (GameBoard.getGame(event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (GameBoard.getGame(event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if (GameBoard.getGame(event.getPlayer()) != null && !event.getPlayer().isOp()) {
			if (!event.getMessage().startsWith("/cm") && !event.getMessage().startsWith("/colormatch")) {
				event.getPlayer().sendMessage(Utilities.translate("&cPlease use &6/cm leave &cto leave this minigame."));
				event.setCancelled(true);
				return;
			}
		}
	}
}
