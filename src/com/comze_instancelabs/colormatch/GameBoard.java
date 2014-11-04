package com.comze_instancelabs.colormatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import com.comze_instancelabs.colormatch.patterns.PatternBase;
import com.comze_instancelabs.colormatch.patterns.logic.StartCountdown;
import com.comze_instancelabs.colormatch.patterns.logic.StateEngine;
import com.comze_instancelabs.colormatch.patterns.logic.WaitingForPlayers;

public class GameBoard {
	private static HashMap<Player, GameBoard> allGames = new HashMap<Player, GameBoard>();
	
	public static GameBoard getGame(Player player) {
		return allGames.get(player);
	}
	
	private Random random;
	
	private ArenaSettings settings;
	private Location spawn;
	
	private Material material;
	private PatternBase currentPattern;
	private DyeColor currentColour;
	private int round;
	
	private List<Player> allPlayers;
	private HashMap<Player, PlayerData> playerData;
	private List<Player> spectators;
	
	private StateEngine<GameBoard> engine;
	private List<Block> activeBlocks;
	
	public GameBoard(Plugin plugin, ArenaSettings settings) {
		random = new Random();
		activeBlocks = new ArrayList<Block>();
		engine = new StateEngine<GameBoard>(plugin);
		this.settings = settings;
		
		allPlayers = new LinkedList<Player>();
		playerData = new HashMap<Player, PlayerData>();
		spectators = new LinkedList<Player>();
		
		engine.start(new WaitingForPlayers(), this);
	}
	
	public void clear() {
		for (Block block : activeBlocks) {
			block.setType(Material.AIR);
		}
		
		activeBlocks.clear();
	}
	
	public void clearExcept(DyeColor colour) {
		for (Block block : activeBlocks) {
			if (Utilities.getBlockColour(block) != colour)
				block.setType(Material.AIR);
		}
	}
	
	public Location getBoardOrigin() {
		return new Location(spawn.getWorld(), spawn.getBlockX() - currentPattern.getWidth() / 2, spawn.getBlockY(), spawn.getBlockZ() - currentPattern.getHeight() / 2);
	}
	
	public Location getPlayerSpawn() {
		return new Location(spawn.getWorld(), spawn.getX(), spawn.getY() + 2, spawn.getZ());
	}
	
	public Location getSpectatorSpawn() {
		return new Location(spawn.getWorld(), spawn.getX(), spawn.getY() + settings.getArenaSpectateHeight(), spawn.getZ());
	}
	
	public void generate() {
		clear();
		currentPattern.placeAt(getBoardOrigin(), material, activeBlocks, random);
	}
	
	public void broadcast(String message) {
		for (Player player : allPlayers) {
			player.sendMessage(message);
		}
	}
	
	public void updateSigns(String message, DyeColor colour) {
		//TODO: This
	}
	
	public boolean joinArena(Player player) {
		if (allGames.containsKey(player))
			return false;
		
		if (!(engine.getCurrentState() instanceof WaitingForPlayers) && !(engine.getCurrentState() instanceof StartCountdown)) {
			player.sendMessage(Messages.arena_ingame);
			return false;
		}
		
		if (allPlayers.size() >= settings.getArenaMaxPlayers()) {
			player.sendMessage(Messages.arena_full);
			return false;
		}
		
		if (allPlayers.isEmpty())
			updateSigns("Waiting", DyeColor.LIME);
		
		PlayerData data = new PlayerData(player);
		playerData.put(player, data);
		
		allPlayers.add(player);
		allGames.put(player, this);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.updateInventory();
		player.setFoodLevel(20);
		player.setSaturation(20);
		
		player.teleport(getPlayerSpawn());
		
		engine.sendEvent("join", player);
		return true;
	}
	
	public void leaveArena(Player player) {
		PlayerData data = playerData.remove(player);
		data.apply(player);
		
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		
		spectators.remove(player);
		allPlayers.remove(player);
		allGames.remove(player);
		
		player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE);
		player.setFlying(false);
		
		// TODO: Do rewards
		
		engine.sendEvent("leave", player);
	}
	
	public void setSpectator(Player player) {
		if (!allPlayers.contains(player))
			return;
		
		spectators.add(player);
		
		player.teleport(getSpectatorSpawn());
		player.setAllowFlight(true);
		player.setFlying(true);
	}
	
	public boolean isSpectator(Player player) {
		return spectators.contains(player);
	}
	
	public void killPlayer(Player player) {
		engine.sendEvent("kill", player);
	}
	
	public void start() {
		if (engine.getCurrentState() instanceof WaitingForPlayers) {
			engine.setState(new StartCountdown());
		}
	}
	
	public void stop() {
		// TODO: this maybe
	}
	
	public Random getRandom() {
		return random;
	}
	
	public void setColour(DyeColor colour) {
		currentColour = colour;
	}
	
	public DyeColor getColour() {
		return currentColour;
	}
	
	public DyeColor getRandomColour() {
		return Main.colors.get(random.nextInt(Main.colors.size()));
	}
	
	public void setPattern(PatternBase pattern) {
		currentPattern = pattern;
	}
	
	public PatternBase getPattern() {
		return currentPattern;
	}
	
	public void setSpawn(Location location) {
		spawn = location;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(allPlayers);
	}
	
	public List<Player> getRemainingPlayers() {
		ArrayList<Player> players = new ArrayList<Player>(allPlayers);
		players.removeAll(spectators);
		return Collections.unmodifiableList(players);
	}
	
	public boolean isPlayer(Player player) {
		return allPlayers.contains(player);
	}
	
	public int getRound() {
		return round;
	}
	
	public ArenaSettings getSettings() {
		return settings;
	}
}
