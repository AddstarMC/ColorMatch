package com.comze_instancelabs.colormatch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;

import com.comze_instancelabs.colormatch.patterns.PatternBase;
import com.comze_instancelabs.colormatch.patterns.logic.PreRoundState;
import com.comze_instancelabs.colormatch.patterns.logic.StateEngine;

public class GameBoard {
	private Random random;
	
	private Location spawn;
	
	private Material material;
	private PatternBase currentPattern;
	private DyeColor currentColour;
	private int round;
	
	private List<Player> spectators;
	
	private StateEngine<GameBoard> engine;
	private List<Block> activeBlocks;
	private ColorMatchModule module;
	
	public GameBoard(Plugin plugin) {
		random = new Random();
		activeBlocks = new ArrayList<Block>();
		engine = new StateEngine<GameBoard>(plugin);
		
		spectators = new LinkedList<Player>();
	}
	
	public void initialize(ColorMatchModule module) {
		this.module = module;
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
		return new Location(spawn.getWorld(), spawn.getX(), spawn.getY() + module.getSpectateHeight(), spawn.getZ());
	}
	
	public void generate() {
		clear();
		currentPattern.placeAt(getBoardOrigin(), material, activeBlocks, random);
	}
	
	public void broadcast(String message) {
		for (MinigamePlayer player : module.getMinigame().getPlayers()) {
			player.sendMessage(message);
		}
	}
	
	public void updateSigns(String message, DyeColor colour) {
		//TODO: This
	}
	
	public void joinArena(Player player) {
		if (module.getMinigame().getPlayers().size() == 1)
			updateSigns("Waiting", DyeColor.LIME);
		
		player.teleport(getPlayerSpawn());
		
		engine.sendEvent("join", player);
	}
	
	public void leaveArena(Player player) {
		spectators.remove(player);
		engine.sendEvent("leave", player);
	}
	
	public void setSpectator(Player player) {
		spectators.add(player);
		
		MinigamePlayer mplayer = Minigames.plugin.pdata.getMinigamePlayer(player);
		mplayer.setCanFly(true);
		player.setFlying(true);
		mplayer.teleport(getSpectatorSpawn());
	}
	
	public boolean isSpectator(Player player) {
		return spectators.contains(player);
	}
	
	public void killPlayer(Player player) {
		engine.sendEvent("kill", player);
	}
	
	public void start() {
		engine.start(new PreRoundState(), this);
	}
	
	public void stop() {
		engine.end();
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
	
	public Minigame getMinigame() {
		return module.getMinigame();
	}
	
	public ColorMatchModule getModule() {
		return module;
	}
	
	public int getRemainingCount() {
		return module.getMinigame().getPlayers().size() - spectators.size();
	}
	
	public int getRound() {
		return round;
	}
}
